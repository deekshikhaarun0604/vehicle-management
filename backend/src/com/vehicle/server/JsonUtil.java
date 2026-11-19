package com.vehicle.server;

import com.vehicle.model.*;
import java.util.*;

/**
 * Lightweight, zero-dependency JSON serialization utility.
 */
public class JsonUtil {

    public static String toJson(Object obj) {
        if (obj == null) return "null";

        if (obj instanceof String) {
            return "\"" + escapeJson((String) obj) + "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(toJson(list.get(i)));
            }
            sb.append("]");
            return sb.toString();
        }
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{");
            int count = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (count > 0) sb.append(",");
                sb.append("\"").append(entry.getKey()).append("\":").append(toJson(entry.getValue()));
                count++;
            }
            sb.append("}");
            return sb.toString();
        }
        if (obj instanceof Customer) {
            Customer c = (Customer) obj;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("customerId", c.getCustomerId());
            map.put("name", c.getName());
            map.put("phone", c.getPhone());
            map.put("email", c.getEmail());
            map.put("address", c.getAddress());
            map.put("createdAt", c.getCreatedAt());
            return toJson(map);
        }
        if (obj instanceof Vehicle) {
            Vehicle v = (Vehicle) obj;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("vehicleId", v.getVehicleId());
            map.put("customerId", v.getCustomerId());
            map.put("customerName", v.getCustomerName());
            map.put("registrationNumber", v.getRegistrationNumber());
            map.put("model", v.getModel());
            map.put("vehicleType", v.getVehicleType());
            map.put("manufactureYear", v.getManufactureYear());
            map.put("createdAt", v.getCreatedAt());
            return toJson(map);
        }
        if (obj instanceof Appointment) {
            Appointment a = (Appointment) obj;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("appointmentId", a.getAppointmentId());
            map.put("customerId", a.getCustomerId());
            map.put("customerName", a.getCustomerName());
            map.put("vehicleId", a.getVehicleId());
            map.put("registrationNumber", a.getRegistrationNumber());
            map.put("vehicleModel", a.getVehicleModel());
            map.put("appointmentDate", a.getAppointmentDate());
            map.put("appointmentTime", a.getAppointmentTime());
            map.put("serviceType", a.getServiceType());
            map.put("status", a.getStatus());
            map.put("notes", a.getNotes());
            map.put("createdAt", a.getCreatedAt());
            return toJson(map);
        }
        if (obj instanceof SparePart) {
            SparePart sp = (SparePart) obj;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("partId", sp.getPartId());
            map.put("partNumber", sp.getPartNumber());
            map.put("partName", sp.getPartName());
            map.put("unitPrice", sp.getUnitPrice());
            map.put("stockQuantity", sp.getStockQuantity());
            return toJson(map);
        }
        if (obj instanceof MaintenancePart) {
            MaintenancePart mp = (MaintenancePart) obj;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("maintPartId", mp.getMaintPartId());
            map.put("recordId", mp.getRecordId());
            map.put("partId", mp.getPartId());
            map.put("partName", mp.getPartName());
            map.put("quantity", mp.getQuantity());
            map.put("unitPrice", mp.getUnitPrice());
            map.put("subtotal", mp.getSubtotal());
            return toJson(map);
        }
        if (obj instanceof MaintenanceRecord) {
            MaintenanceRecord m = (MaintenanceRecord) obj;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("recordId", m.getRecordId());
            map.put("appointmentId", m.getAppointmentId());
            map.put("vehicleId", m.getVehicleId());
            map.put("registrationNumber", m.getRegistrationNumber());
            map.put("vehicleModel", m.getVehicleModel());
            map.put("vehicleType", m.getVehicleType());
            map.put("customerName", m.getCustomerName());
            map.put("customerPhone", m.getCustomerPhone());
            map.put("serviceDate", m.getServiceDate());
            map.put("workPerformed", m.getWorkPerformed());
            map.put("labourCharges", m.getLabourCharges());
            map.put("sparePartsCost", m.getSparePartsCost());
            map.put("totalBill", m.getTotalBill());
            map.put("status", m.getStatus());
            map.put("paymentStatus", m.getPaymentStatus());
            map.put("partsUsed", m.getPartsUsed());
            map.put("createdAt", m.getCreatedAt());
            return toJson(map);
        }
        if (obj instanceof Bill) {
            Bill b = (Bill) obj;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("billId", b.getBillId());
            map.put("recordId", b.getRecordId());
            map.put("registrationNumber", b.getRegistrationNumber());
            map.put("vehicleModel", b.getVehicleModel());
            map.put("customerName", b.getCustomerName());
            map.put("customerPhone", b.getCustomerPhone());
            map.put("customerAddress", b.getCustomerAddress());
            map.put("workPerformed", b.getWorkPerformed());
            map.put("labourCharges", b.getLabourCharges());
            map.put("sparePartsCost", b.getSparePartsCost());
            map.put("totalAmount", b.getTotalAmount());
            map.put("paymentStatus", b.getPaymentStatus());
            map.put("paymentMethod", b.getPaymentMethod());
            map.put("billingDate", b.getBillingDate());
            map.put("itemizedParts", b.getItemizedParts());
            map.put("createdAt", b.getCreatedAt());
            return toJson(map);
        }

        return "\"" + obj.toString() + "\"";
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Minimal JSON parser extracting key-value string pairs from simple flat JSON objects.
     */
    public static Map<String, String> parseJsonMap(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null) return map;
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        String[] tokens = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String token : tokens) {
            String[] kv = token.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 2);
            if (kv.length == 2) {
                String key = kv[0].trim().replaceAll("^\"|\"$", "");
                String val = kv[1].trim().replaceAll("^\"|\"$", "");
                map.put(key, val);
            }
        }
        return map;
    }
}
