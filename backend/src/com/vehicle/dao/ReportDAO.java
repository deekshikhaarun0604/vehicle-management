package com.vehicle.dao;

import com.vehicle.config.DBConnection;
import com.vehicle.model.*;
import java.sql.*;
import java.util.*;

/**
 * Data Access Object for Generating Reports and Workload Analytics using JDBC.
 */
public class ReportDAO {

    /**
     * Report 1: Service History (optionally filtered by vehicleId or customerId).
     */
    public List<Map<String, Object>> getServiceHistory(Integer vehicleId) {
        List<Map<String, Object>> report = new ArrayList<>();
        
        if (!DBConnection.isMySqlAvailable()) {
            for (MaintenanceRecord mr : DataStore.maintenanceRecords.values()) {
                if (vehicleId != null && mr.getVehicleId() != vehicleId) continue;
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("recordId", mr.getRecordId());
                row.put("serviceDate", mr.getServiceDate());
                row.put("vehicleId", mr.getVehicleId());
                row.put("registrationNumber", mr.getRegistrationNumber());
                row.put("model", mr.getVehicleModel());
                row.put("vehicleType", mr.getVehicleType());
                row.put("customerName", mr.getCustomerName());
                row.put("workPerformed", mr.getWorkPerformed());
                row.put("labourCharges", mr.getLabourCharges());
                row.put("sparePartsCost", mr.getSparePartsCost());
                row.put("totalBill", mr.getTotalBill());
                row.put("status", mr.getStatus());
                row.put("paymentStatus", mr.getPaymentStatus());
                report.add(row);
            }
            report.sort((a, b) -> ((String) b.get("serviceDate")).compareTo((String) a.get("serviceDate")));
            return report;
        }

        String sql = "SELECT m.record_id, m.service_date, m.vehicle_id, v.registration_number, v.model, v.vehicle_type, " +
                     "c.name AS customer_name, m.work_performed, m.labour_charges, " +
                     "COALESCE(b.spare_parts_cost, 0.00) AS parts_cost, " +
                     "COALESCE(b.total_amount, m.labour_charges) AS total_bill, " +
                     "m.status, COALESCE(b.payment_status, 'Unpaid') AS payment_status " +
                     "FROM maintenance_records m " +
                     "JOIN vehicles v ON m.vehicle_id = v.vehicle_id " +
                     "JOIN customers c ON v.customer_id = c.customer_id " +
                     "LEFT JOIN bills b ON m.record_id = b.record_id " +
                     (vehicleId != null ? "WHERE m.vehicle_id = ? " : "") +
                     "ORDER BY m.service_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (vehicleId != null) {
                ps.setInt(1, vehicleId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("recordId", rs.getInt("record_id"));
                    row.put("serviceDate", rs.getString("service_date"));
                    row.put("vehicleId", rs.getInt("vehicle_id"));
                    row.put("registrationNumber", rs.getString("registration_number"));
                    row.put("model", rs.getString("model"));
                    row.put("vehicleType", rs.getString("vehicle_type"));
                    row.put("customerName", rs.getString("customer_name"));
                    row.put("workPerformed", rs.getString("work_performed"));
                    row.put("labourCharges", rs.getDouble("labour_charges"));
                    row.put("sparePartsCost", rs.getDouble("parts_cost"));
                    row.put("totalBill", rs.getDouble("total_bill"));
                    row.put("status", rs.getString("status"));
                    row.put("paymentStatus", rs.getString("payment_status"));
                    report.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] Error generating service history: " + e.getMessage());
        }
        return report;
    }

    /**
     * Report 2: Vehicle-wise Maintenance Costs.
     */
    public List<Map<String, Object>> getVehicleWiseMaintenanceCosts() {
        List<Map<String, Object>> report = new ArrayList<>();

        if (!DBConnection.isMySqlAvailable()) {
            Map<Integer, Map<String, Object>> agg = new LinkedHashMap<>();
            for (Vehicle v : DataStore.vehicles.values()) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("vehicleId", v.getVehicleId());
                map.put("registrationNumber", v.getRegistrationNumber());
                map.put("model", v.getModel());
                map.put("vehicleType", v.getVehicleType());
                Customer c = DataStore.customers.get(v.getCustomerId());
                map.put("ownerName", c != null ? c.getName() : "Unknown");
                map.put("totalServices", 0);
                map.put("totalLabourCost", 0.0);
                map.put("totalPartsCost", 0.0);
                map.put("totalMaintenanceExpenditure", 0.0);
                agg.put(v.getVehicleId(), map);
            }

            for (MaintenanceRecord mr : DataStore.maintenanceRecords.values()) {
                Map<String, Object> map = agg.get(mr.getVehicleId());
                if (map != null) {
                    map.put("totalServices", (int) map.get("totalServices") + 1);
                    map.put("totalLabourCost", (double) map.get("totalLabourCost") + mr.getLabourCharges());
                    map.put("totalPartsCost", (double) map.get("totalPartsCost") + mr.getSparePartsCost());
                    map.put("totalMaintenanceExpenditure", (double) map.get("totalMaintenanceExpenditure") + mr.getTotalBill());
                }
            }

            report.addAll(agg.values());
            report.sort((a, b) -> Double.compare((double) b.get("totalMaintenanceExpenditure"), (double) a.get("totalMaintenanceExpenditure")));
            return report;
        }

        String sql = "SELECT v.vehicle_id, v.registration_number, v.model, v.vehicle_type, c.name AS owner_name, " +
                     "COUNT(m.record_id) AS total_services, " +
                     "COALESCE(SUM(m.labour_charges), 0.00) AS total_labour_cost, " +
                     "COALESCE(SUM(b.spare_parts_cost), 0.00) AS total_parts_cost, " +
                     "COALESCE(SUM(b.total_amount), SUM(m.labour_charges), 0.00) AS total_maintenance_expenditure " +
                     "FROM vehicles v " +
                     "JOIN customers c ON v.customer_id = c.customer_id " +
                     "LEFT JOIN maintenance_records m ON v.vehicle_id = m.vehicle_id " +
                     "LEFT JOIN bills b ON m.record_id = b.record_id " +
                     "GROUP BY v.vehicle_id, v.registration_number, v.model, v.vehicle_type, c.name " +
                     "ORDER BY total_maintenance_expenditure DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("vehicleId", rs.getInt("vehicle_id"));
                row.put("registrationNumber", rs.getString("registration_number"));
                row.put("model", rs.getString("model"));
                row.put("vehicleType", rs.getString("vehicle_type"));
                row.put("ownerName", rs.getString("owner_name"));
                row.put("totalServices", rs.getInt("total_services"));
                row.put("totalLabourCost", rs.getDouble("total_labour_cost"));
                row.put("totalPartsCost", rs.getDouble("total_parts_cost"));
                row.put("totalMaintenanceExpenditure", rs.getDouble("total_maintenance_expenditure"));
                report.add(row);
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] Error generating vehicle cost report: " + e.getMessage());
        }
        return report;
    }

    /**
     * Report 3: Service Center Workload.
     */
    public Map<String, Object> getServiceCenterWorkload() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> statusBreakdown = new ArrayList<>();
        List<Map<String, Object>> dailyBreakdown = new ArrayList<>();

        if (!DBConnection.isMySqlAvailable()) {
            Map<String, Integer> statusCounts = new LinkedHashMap<>();
            Map<String, Integer> dailyCounts = new TreeMap<>();

            for (Appointment a : DataStore.appointments.values()) {
                String st = a.getStatus();
                statusCounts.put(st, statusCounts.getOrDefault(st, 0) + 1);
                String dt = a.getAppointmentDate();
                dailyCounts.put(dt, dailyCounts.getOrDefault(dt, 0) + 1);
            }

            for (Map.Entry<String, Integer> e : statusCounts.entrySet()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("status", e.getKey());
                item.put("count", e.getValue());
                statusBreakdown.add(item);
            }

            for (Map.Entry<String, Integer> e : dailyCounts.entrySet()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("date", e.getKey());
                item.put("appointments", e.getValue());
                dailyBreakdown.add(item);
            }

            result.put("statusDistribution", statusBreakdown);
            result.put("dailyWorkload", dailyBreakdown);
            result.put("totalAppointments", DataStore.appointments.size());
            return result;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Status distribution query
            String statusSql = "SELECT status, COUNT(*) AS count FROM appointments GROUP BY status";
            try (PreparedStatement ps = conn.prepareStatement(statusSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("status", rs.getString("status"));
                    item.put("count", rs.getInt("count"));
                    statusBreakdown.add(item);
                }
            }

            // Daily distribution query
            String dailySql = "SELECT appointment_date, COUNT(*) AS count FROM appointments " +
                             "GROUP BY appointment_date ORDER BY appointment_date ASC";
            try (PreparedStatement ps = conn.prepareStatement(dailySql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("date", rs.getString("appointment_date"));
                    item.put("appointments", rs.getInt("count"));
                    dailyBreakdown.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] Error generating workload report: " + e.getMessage());
        }

        result.put("statusDistribution", statusBreakdown);
        result.put("dailyWorkload", dailyBreakdown);
        return result;
    }

    /**
     * Dashboard Summary KPIs.
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        if (!DBConnection.isMySqlAvailable()) {
            stats.put("totalCustomers", DataStore.customers.size());
            stats.put("totalVehicles", DataStore.vehicles.size());
            stats.put("totalAppointments", DataStore.appointments.size());
            stats.put("totalServices", DataStore.maintenanceRecords.size());

            double revenue = 0.0;
            int unpaid = 0;
            for (Bill b : DataStore.bills.values()) {
                if ("Paid".equalsIgnoreCase(b.getPaymentStatus())) {
                    revenue += b.getTotalAmount();
                } else {
                    unpaid++;
                }
            }
            stats.put("totalRevenue", revenue);
            stats.put("unpaidBills", unpaid);
            stats.put("activeServices", DataStore.maintenanceRecords.values().stream().filter(m -> "In Progress".equalsIgnoreCase(m.getStatus()) || "Pending".equalsIgnoreCase(m.getStatus())).count());
            return stats;
        }

        try (Connection conn = DBConnection.getConnection()) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM customers")) {
                    if (rs.next()) stats.put("totalCustomers", rs.getInt(1));
                }
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM vehicles")) {
                    if (rs.next()) stats.put("totalVehicles", rs.getInt(1));
                }
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM appointments")) {
                    if (rs.next()) stats.put("totalAppointments", rs.getInt(1));
                }
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM maintenance_records")) {
                    if (rs.next()) stats.put("totalServices", rs.getInt(1));
                }
                try (ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(total_amount), 0.00) FROM bills WHERE payment_status = 'Paid'")) {
                    if (rs.next()) stats.put("totalRevenue", rs.getDouble(1));
                }
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM bills WHERE payment_status = 'Unpaid'")) {
                    if (rs.next()) stats.put("unpaidBills", rs.getInt(1));
                }
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM maintenance_records WHERE status IN ('Pending', 'In Progress')")) {
                    if (rs.next()) stats.put("activeServices", rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] Error fetching stats: " + e.getMessage());
        }
        return stats;
    }
}
