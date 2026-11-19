package com.vehicle.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.vehicle.dao.SparePartDAO;
import com.vehicle.model.*;
import com.vehicle.service.*;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Main REST API router handling CRUD requests across all modules.
 */
public class ApiHandler implements HttpHandler {
    private final CustomerService customerService = new CustomerService();
    private final VehicleService vehicleService = new VehicleService();
    private final AppointmentService appointmentService = new AppointmentService();
    private final MaintenanceService maintenanceService = new MaintenanceService();
    private final BillingService billingService = new BillingService();
    private final ReportService reportService = new ReportService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Enable CORS
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

        String method = exchange.getRequestMethod().toUpperCase();
        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        URI requestUri = exchange.getRequestURI();
        String path = requestUri.getPath();
        String query = requestUri.getQuery();
        Map<String, String> queryParams = parseQueryParams(query);

        try {
            // Router
            if (path.startsWith("/api/customers")) {
                handleCustomers(exchange, method, path, queryParams);
            } else if (path.startsWith("/api/vehicles")) {
                handleVehicles(exchange, method, path, queryParams);
            } else if (path.startsWith("/api/appointments")) {
                handleAppointments(exchange, method, path, queryParams);
            } else if (path.startsWith("/api/spare-parts")) {
                handleSpareParts(exchange, method);
            } else if (path.startsWith("/api/maintenance")) {
                handleMaintenance(exchange, method, path, queryParams);
            } else if (path.startsWith("/api/bills")) {
                handleBills(exchange, method, path, queryParams);
            } else if (path.equals("/api/reports/history")) {
                Integer vehId = queryParams.containsKey("vehicleId") ? Integer.parseInt(queryParams.get("vehicleId")) : null;
                sendJson(exchange, 200, reportService.getServiceHistory(vehId));
            } else if (path.equals("/api/reports/vehicle-costs")) {
                sendJson(exchange, 200, reportService.getVehicleWiseMaintenanceCosts());
            } else if (path.equals("/api/reports/workload")) {
                sendJson(exchange, 200, reportService.getServiceCenterWorkload());
            } else if (path.equals("/api/dashboard/stats")) {
                sendJson(exchange, 200, reportService.getDashboardStats());
            } else {
                sendError(exchange, 404, "Endpoint not found: " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Server error: " + e.getMessage());
        }
    }

    // --- Customer Handling ---
    private void handleCustomers(HttpExchange exchange, String method, String path, Map<String, String> params) throws IOException {
        if ("GET".equals(method)) {
            sendJson(exchange, 200, customerService.getAllCustomers());
        } else if ("POST".equals(method)) {
            String body = readBody(exchange);
            Map<String, String> data = JsonUtil.parseJsonMap(body);
            Customer c = new Customer(0, data.get("name"), data.get("phone"), data.get("email"), data.get("address"));
            boolean ok = customerService.addCustomer(c);
            if (ok) sendSuccess(exchange, 201, "Customer created successfully", c);
            else sendError(exchange, 400, "Failed to create customer");
        } else if ("PUT".equals(method)) {
            String body = readBody(exchange);
            Map<String, String> data = JsonUtil.parseJsonMap(body);
            int id = Integer.parseInt(data.get("customerId"));
            Customer c = new Customer(id, data.get("name"), data.get("phone"), data.get("email"), data.get("address"));
            boolean ok = customerService.updateCustomer(c);
            if (ok) sendSuccess(exchange, 200, "Customer updated successfully", c);
            else sendError(exchange, 400, "Failed to update customer");
        } else if ("DELETE".equals(method)) {
            int id = Integer.parseInt(params.get("id"));
            boolean ok = customerService.deleteCustomer(id);
            if (ok) sendSuccess(exchange, 200, "Customer deleted", null);
            else sendError(exchange, 400, "Failed to delete customer");
        }
    }

    // --- Vehicle Handling ---
    private void handleVehicles(HttpExchange exchange, String method, String path, Map<String, String> params) throws IOException {
        if ("GET".equals(method)) {
            sendJson(exchange, 200, vehicleService.getAllVehicles());
        } else if ("POST".equals(method)) {
            String body = readBody(exchange);
            Map<String, String> data = JsonUtil.parseJsonMap(body);
            int custId = Integer.parseInt(data.get("customerId"));
            int year = Integer.parseInt(data.get("manufactureYear"));
            Vehicle v = new Vehicle(0, custId, data.get("registrationNumber"), data.get("model"), data.get("vehicleType"), year);
            boolean ok = vehicleService.addVehicle(v);
            if (ok) sendSuccess(exchange, 201, "Vehicle registered successfully", v);
            else sendError(exchange, 400, "Failed to register vehicle");
        } else if ("PUT".equals(method)) {
            String body = readBody(exchange);
            Map<String, String> data = JsonUtil.parseJsonMap(body);
            int id = Integer.parseInt(data.get("vehicleId"));
            int custId = Integer.parseInt(data.get("customerId"));
            int year = Integer.parseInt(data.get("manufactureYear"));
            Vehicle v = new Vehicle(id, custId, data.get("registrationNumber"), data.get("model"), data.get("vehicleType"), year);
            boolean ok = vehicleService.updateVehicle(v);
            if (ok) sendSuccess(exchange, 200, "Vehicle updated", v);
            else sendError(exchange, 400, "Failed to update vehicle");
        } else if ("DELETE".equals(method)) {
            int id = Integer.parseInt(params.get("id"));
            boolean ok = vehicleService.deleteVehicle(id);
            if (ok) sendSuccess(exchange, 200, "Vehicle deleted", null);
            else sendError(exchange, 400, "Failed to delete vehicle");
        }
    }

    // --- Appointment Handling with Conflict Checking ---
    private void handleAppointments(HttpExchange exchange, String method, String path, Map<String, String> params) throws IOException {
        if ("GET".equals(method)) {
            sendJson(exchange, 200, appointmentService.getAllAppointments());
        } else if ("POST".equals(method)) {
            String body = readBody(exchange);
            Map<String, String> data = JsonUtil.parseJsonMap(body);
            int custId = Integer.parseInt(data.get("customerId"));
            int vehId = Integer.parseInt(data.get("vehicleId"));
            Appointment a = new Appointment(0, custId, vehId, data.get("appointmentDate"), 
                                            data.get("appointmentTime"), data.get("serviceType"), 
                                            data.getOrDefault("status", "Scheduled"), data.get("notes"));
            try {
                boolean ok = appointmentService.scheduleAppointment(a);
                if (ok) sendSuccess(exchange, 201, "Appointment scheduled successfully", a);
                else sendError(exchange, 409, "Scheduling conflict: Vehicle already booked at this slot.");
            } catch (IllegalStateException e) {
                sendError(exchange, 409, e.getMessage());
            }
        } else if ("PUT".equals(method)) {
            String body = readBody(exchange);
            Map<String, String> data = JsonUtil.parseJsonMap(body);
            int id = Integer.parseInt(data.get("appointmentId"));
            if (data.containsKey("newStatus")) {
                boolean ok = appointmentService.updateStatus(id, data.get("newStatus"));
                if (ok) sendSuccess(exchange, 200, "Appointment status updated", null);
                else sendError(exchange, 400, "Failed to update status");
            } else {
                int custId = Integer.parseInt(data.get("customerId"));
                int vehId = Integer.parseInt(data.get("vehicleId"));
                Appointment a = new Appointment(id, custId, vehId, data.get("appointmentDate"), 
                                                data.get("appointmentTime"), data.get("serviceType"), 
                                                data.get("status"), data.get("notes"));
                try {
                    boolean ok = appointmentService.updateAppointment(a);
                    if (ok) sendSuccess(exchange, 200, "Appointment updated", a);
                    else sendError(exchange, 409, "Scheduling conflict on update.");
                } catch (IllegalStateException e) {
                    sendError(exchange, 409, e.getMessage());
                }
            }
        } else if ("DELETE".equals(method)) {
            int id = Integer.parseInt(params.get("id"));
            boolean ok = appointmentService.deleteAppointment(id);
            if (ok) sendSuccess(exchange, 200, "Appointment deleted", null);
            else sendError(exchange, 400, "Failed to delete appointment");
        }
    }

    // --- Spare Parts ---
    private void handleSpareParts(HttpExchange exchange, String method) throws IOException {
        if ("GET".equals(method)) {
            sendJson(exchange, 200, maintenanceService.getAllSpareParts());
        }
    }

    // --- Maintenance Handling ---
    private void handleMaintenance(HttpExchange exchange, String method, String path, Map<String, String> params) throws IOException {
        if ("GET".equals(method)) {
            if (params.containsKey("id")) {
                int id = Integer.parseInt(params.get("id"));
                sendJson(exchange, 200, maintenanceService.getRecordById(id));
            } else {
                sendJson(exchange, 200, maintenanceService.getAllRecords());
            }
        } else if ("POST".equals(method)) {
            String body = readBody(exchange);
            Map<String, String> data = JsonUtil.parseJsonMap(body);
            Integer apptId = (data.containsKey("appointmentId") && !data.get("appointmentId").isEmpty()) ? 
                             Integer.parseInt(data.get("appointmentId")) : null;
            int vehId = Integer.parseInt(data.get("vehicleId"));
            double labour = Double.parseDouble(data.getOrDefault("labourCharges", "0.0"));
            MaintenanceRecord m = new MaintenanceRecord(0, apptId, vehId, data.get("serviceDate"), 
                                                        data.get("workPerformed"), labour, 
                                                        data.getOrDefault("status", "Completed"));

            // Parse parts if provided in custom format or parameters
            if (data.containsKey("partId") && !data.get("partId").isEmpty()) {
                int partId = Integer.parseInt(data.get("partId"));
                int qty = Integer.parseInt(data.getOrDefault("quantity", "1"));
                SparePart sp = new SparePartDAO().getPartById(partId);
                double price = sp != null ? sp.getUnitPrice() : 0.0;
                m.addPartUsed(new MaintenancePart(0, partId, qty, price));
            }

            int recId = maintenanceService.createMaintenanceRecord(m);
            if (recId > 0) {
                // Also automatically generate bill draft
                billingService.generateBillForRecord(recId, "Cash", "Unpaid");
                sendSuccess(exchange, 201, "Maintenance record and invoice created", m);
            } else {
                sendError(exchange, 400, "Failed to create maintenance record");
            }
        } else if ("DELETE".equals(method)) {
            int id = Integer.parseInt(params.get("id"));
            boolean ok = maintenanceService.deleteRecord(id);
            if (ok) sendSuccess(exchange, 200, "Record deleted", null);
            else sendError(exchange, 400, "Failed to delete record");
        }
    }

    // --- Billing Handling ---
    private void handleBills(HttpExchange exchange, String method, String path, Map<String, String> params) throws IOException {
        if ("GET".equals(method)) {
            if (params.containsKey("id")) {
                int id = Integer.parseInt(params.get("id"));
                sendJson(exchange, 200, billingService.getBillById(id));
            } else if (params.containsKey("recordId")) {
                int recId = Integer.parseInt(params.get("recordId"));
                sendJson(exchange, 200, billingService.getBillByRecordId(recId));
            } else {
                sendJson(exchange, 200, billingService.getAllBills());
            }
        } else if ("POST".equals(method)) {
            String body = readBody(exchange);
            Map<String, String> data = JsonUtil.parseJsonMap(body);
            int recId = Integer.parseInt(data.get("recordId"));
            String status = data.getOrDefault("paymentStatus", "Paid");
            String methodPay = data.getOrDefault("paymentMethod", "Cash");
            Bill bill = billingService.generateBillForRecord(recId, methodPay, status);
            if (bill != null) {
                sendSuccess(exchange, 201, "Bill calculated and stored", bill);
            } else {
                sendError(exchange, 400, "Failed to generate bill for record " + recId);
            }
        } else if ("PUT".equals(method)) {
            String body = readBody(exchange);
            Map<String, String> data = JsonUtil.parseJsonMap(body);
            int billId = Integer.parseInt(data.get("billId"));
            String status = data.get("paymentStatus");
            String payMethod = data.getOrDefault("paymentMethod", "Cash");
            boolean ok = billingService.updatePaymentStatus(billId, status, payMethod);
            if (ok) sendSuccess(exchange, 200, "Payment status updated", null);
            else sendError(exchange, 400, "Failed to update payment status");
        } else if ("DELETE".equals(method)) {
            int id = Integer.parseInt(params.get("id"));
            boolean ok = billingService.deleteBill(id);
            if (ok) sendSuccess(exchange, 200, "Bill deleted", null);
            else sendError(exchange, 400, "Failed to delete bill");
        }
    }

    // --- Helpers ---
    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream in = exchange.getRequestBody();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int n;
            while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
            return out.toString("UTF-8");
        }
    }

    private void sendJson(HttpExchange exchange, int statusCode, Object data) throws IOException {
        byte[] bytes = JsonUtil.toJson(data).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendSuccess(HttpExchange exchange, int statusCode, String message, Object data) throws IOException {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("success", true);
        resp.put("message", message);
        if (data != null) resp.put("data", data);
        sendJson(exchange, statusCode, resp);
    }

    private void sendError(HttpExchange exchange, int statusCode, String error) throws IOException {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("success", false);
        resp.put("error", error);
        sendJson(exchange, statusCode, resp);
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);
            if (pair.length == 2) {
                map.put(pair[0], pair[1]);
            }
        }
        return map;
    }
}
