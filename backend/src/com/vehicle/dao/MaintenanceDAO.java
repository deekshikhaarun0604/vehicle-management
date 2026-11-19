package com.vehicle.dao;

import com.vehicle.config.DBConnection;
import com.vehicle.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Maintenance Records and Spare Parts consumption using JDBC.
 */
public class MaintenanceDAO {

    public List<MaintenanceRecord> getAllRecords() {
        if (!DBConnection.isMySqlAvailable()) {
            List<MaintenanceRecord> list = new ArrayList<>(DataStore.maintenanceRecords.values());
            for (MaintenanceRecord m : list) {
                populateRecordDetails(m);
            }
            return list;
        }

        List<MaintenanceRecord> list = new ArrayList<>();
        String sql = "SELECT m.record_id, m.appointment_id, m.vehicle_id, m.service_date, " +
                     "m.work_performed, m.labour_charges, m.status, m.created_at, " +
                     "v.registration_number, v.model AS vehicle_model, v.vehicle_type, " +
                     "c.name AS customer_name, c.phone AS customer_phone, " +
                     "COALESCE(b.spare_parts_cost, 0.00) AS parts_cost, " +
                     "COALESCE(b.total_amount, m.labour_charges) AS total_bill, " +
                     "COALESCE(b.payment_status, 'Unpaid') AS payment_status " +
                     "FROM maintenance_records m " +
                     "JOIN vehicles v ON m.vehicle_id = v.vehicle_id " +
                     "JOIN customers c ON v.customer_id = c.customer_id " +
                     "LEFT JOIN bills b ON m.record_id = b.record_id " +
                     "ORDER BY m.service_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MaintenanceRecord m = new MaintenanceRecord(
                    rs.getInt("record_id"),
                    (Integer) rs.getObject("appointment_id"),
                    rs.getInt("vehicle_id"),
                    rs.getString("service_date"),
                    rs.getString("work_performed"),
                    rs.getDouble("labour_charges"),
                    rs.getString("status")
                );
                m.setRegistrationNumber(rs.getString("registration_number"));
                m.setVehicleModel(rs.getString("vehicle_model"));
                m.setVehicleType(rs.getString("vehicle_type"));
                m.setCustomerName(rs.getString("customer_name"));
                m.setCustomerPhone(rs.getString("customer_phone"));
                m.setSparePartsCost(rs.getDouble("parts_cost"));
                m.setTotalBill(rs.getDouble("total_bill"));
                m.setPaymentStatus(rs.getString("payment_status"));
                m.setCreatedAt(rs.getString("created_at"));
                m.setPartsUsed(getPartsByRecordId(m.getRecordId()));
                list.add(m);
            }
        } catch (SQLException e) {
            System.err.println("[MaintenanceDAO] Error fetching maintenance records: " + e.getMessage());
            return new ArrayList<>(DataStore.maintenanceRecords.values());
        }
        return list;
    }

    public MaintenanceRecord getRecordById(int id) {
        if (!DBConnection.isMySqlAvailable()) {
            MaintenanceRecord m = DataStore.maintenanceRecords.get(id);
            if (m != null) populateRecordDetails(m);
            return m;
        }

        String sql = "SELECT m.record_id, m.appointment_id, m.vehicle_id, m.service_date, " +
                     "m.work_performed, m.labour_charges, m.status, m.created_at, " +
                     "v.registration_number, v.model AS vehicle_model, v.vehicle_type, " +
                     "c.name AS customer_name, c.phone AS customer_phone, " +
                     "COALESCE(b.spare_parts_cost, 0.00) AS parts_cost, " +
                     "COALESCE(b.total_amount, m.labour_charges) AS total_bill, " +
                     "COALESCE(b.payment_status, 'Unpaid') AS payment_status " +
                     "FROM maintenance_records m " +
                     "JOIN vehicles v ON m.vehicle_id = v.vehicle_id " +
                     "JOIN customers c ON v.customer_id = c.customer_id " +
                     "LEFT JOIN bills b ON m.record_id = b.record_id " +
                     "WHERE m.record_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MaintenanceRecord m = new MaintenanceRecord(
                        rs.getInt("record_id"),
                        (Integer) rs.getObject("appointment_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("service_date"),
                        rs.getString("work_performed"),
                        rs.getDouble("labour_charges"),
                        rs.getString("status")
                    );
                    m.setRegistrationNumber(rs.getString("registration_number"));
                    m.setVehicleModel(rs.getString("vehicle_model"));
                    m.setVehicleType(rs.getString("vehicle_type"));
                    m.setCustomerName(rs.getString("customer_name"));
                    m.setCustomerPhone(rs.getString("customer_phone"));
                    m.setSparePartsCost(rs.getDouble("parts_cost"));
                    m.setTotalBill(rs.getDouble("total_bill"));
                    m.setPaymentStatus(rs.getString("payment_status"));
                    m.setCreatedAt(rs.getString("created_at"));
                    m.setPartsUsed(getPartsByRecordId(m.getRecordId()));
                    return m;
                }
            }
        } catch (SQLException e) {
            System.err.println("[MaintenanceDAO] Error fetching record " + id + ": " + e.getMessage());
        }
        return null;
    }

    public List<MaintenancePart> getPartsByRecordId(int recordId) {
        if (!DBConnection.isMySqlAvailable()) {
            return DataStore.maintenanceParts.getOrDefault(recordId, new ArrayList<>());
        }

        List<MaintenancePart> parts = new ArrayList<>();
        String sql = "SELECT mp.maint_part_id, mp.record_id, mp.part_id, mp.quantity, mp.unit_price, mp.subtotal, " +
                     "sp.part_name " +
                     "FROM maintenance_parts mp " +
                     "JOIN spare_parts sp ON mp.part_id = sp.part_id " +
                     "WHERE mp.record_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recordId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MaintenancePart p = new MaintenancePart(
                        rs.getInt("maint_part_id"),
                        rs.getInt("record_id"),
                        rs.getInt("part_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("subtotal")
                    );
                    p.setPartName(rs.getString("part_name"));
                    parts.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("[MaintenanceDAO] Error fetching parts for record " + recordId + ": " + e.getMessage());
        }
        return parts;
    }

    public int addRecord(MaintenanceRecord record) {
        if (!DBConnection.isMySqlAvailable()) {
            int newId = DataStore.nextMaintenanceId();
            record.setRecordId(newId);
            record.setCreatedAt("2026-11-19 12:00:00");
            populateRecordDetails(record);
            DataStore.maintenanceRecords.put(newId, record);

            // Add parts if any
            if (record.getPartsUsed() != null) {
                double totalParts = 0.0;
                for (MaintenancePart mp : record.getPartsUsed()) {
                    mp.setRecordId(newId);
                    int mpId = DataStore.nextMaintPartId();
                    mp.setMaintPartId(mpId);
                    SparePart sp = DataStore.spareParts.get(mp.getPartId());
                    if (sp != null) {
                        mp.setPartName(sp.getPartName());
                        sp.setStockQuantity(Math.max(0, sp.getStockQuantity() - mp.getQuantity()));
                    }
                    totalParts += mp.getSubtotal();
                    DataStore.maintenanceParts.computeIfAbsent(newId, k -> new ArrayList<>()).add(mp);
                }
                record.setSparePartsCost(totalParts);
                record.setTotalBill(record.getLabourCharges() + totalParts);
            }
            return newId;
        }

        String sql = "INSERT INTO maintenance_records (appointment_id, vehicle_id, service_date, work_performed, labour_charges, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (record.getAppointmentId() != null && record.getAppointmentId() > 0) {
                ps.setInt(1, record.getAppointmentId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, record.getVehicleId());
            ps.setString(3, record.getServiceDate());
            ps.setString(4, record.getWorkPerformed());
            ps.setDouble(5, record.getLabourCharges());
            ps.setString(6, record.getStatus() != null ? record.getStatus() : "Pending");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int recordId = rs.getInt(1);
                        record.setRecordId(recordId);

                        // Insert parts used
                        if (record.getPartsUsed() != null) {
                            for (MaintenancePart p : record.getPartsUsed()) {
                                p.setRecordId(recordId);
                                addPartUsage(p);
                            }
                        }
                        return recordId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[MaintenanceDAO] Error inserting maintenance record: " + e.getMessage());
        }
        return -1;
    }

    public boolean addPartUsage(MaintenancePart part) {
        if (!DBConnection.isMySqlAvailable()) {
            int mpId = DataStore.nextMaintPartId();
            part.setMaintPartId(mpId);
            SparePart sp = DataStore.spareParts.get(part.getPartId());
            if (sp != null) part.setPartName(sp.getPartName());
            DataStore.maintenanceParts.computeIfAbsent(part.getRecordId(), k -> new ArrayList<>()).add(part);
            return true;
        }

        String sql = "INSERT INTO maintenance_parts (record_id, part_id, quantity, unit_price, subtotal) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, part.getRecordId());
            ps.setInt(2, part.getPartId());
            ps.setInt(3, part.getQuantity());
            ps.setDouble(4, part.getUnitPrice());
            ps.setDouble(5, part.getSubtotal());

            int res = ps.executeUpdate();
            if (res > 0) {
                // Decrement inventory
                new SparePartDAO().updateStock(part.getPartId(), part.getQuantity());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[MaintenanceDAO] Error inserting part usage: " + e.getMessage());
        }
        return false;
    }

    public boolean updateRecord(MaintenanceRecord record) {
        if (!DBConnection.isMySqlAvailable()) {
            if (DataStore.maintenanceRecords.containsKey(record.getRecordId())) {
                populateRecordDetails(record);
                DataStore.maintenanceRecords.put(record.getRecordId(), record);
                return true;
            }
            return false;
        }

        String sql = "UPDATE maintenance_records SET service_date = ?, work_performed = ?, labour_charges = ?, status = ? " +
                     "WHERE record_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, record.getServiceDate());
            ps.setString(2, record.getWorkPerformed());
            ps.setDouble(3, record.getLabourCharges());
            ps.setString(4, record.getStatus());
            ps.setInt(5, record.getRecordId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MaintenanceDAO] Error updating record: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteRecord(int id) {
        if (!DBConnection.isMySqlAvailable()) {
            DataStore.bills.values().removeIf(b -> b.getRecordId() == id);
            DataStore.maintenanceParts.remove(id);
            return DataStore.maintenanceRecords.remove(id) != null;
        }

        String sql = "DELETE FROM maintenance_records WHERE record_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MaintenanceDAO] Error deleting record: " + e.getMessage());
        }
        return false;
    }

    private void populateRecordDetails(MaintenanceRecord m) {
        Vehicle v = DataStore.vehicles.get(m.getVehicleId());
        if (v != null) {
            m.setRegistrationNumber(v.getRegistrationNumber());
            m.setVehicleModel(v.getModel());
            m.setVehicleType(v.getVehicleType());
            Customer c = DataStore.customers.get(v.getCustomerId());
            if (c != null) {
                m.setCustomerName(c.getName());
                m.setCustomerPhone(c.getPhone());
            }
        }
        List<MaintenancePart> parts = DataStore.maintenanceParts.getOrDefault(m.getRecordId(), new ArrayList<>());
        m.setPartsUsed(parts);
        double partsCost = 0.0;
        for (MaintenancePart p : parts) partsCost += p.getSubtotal();
        m.setSparePartsCost(partsCost);
        m.setTotalBill(m.getLabourCharges() + partsCost);

        for (Bill b : DataStore.bills.values()) {
            if (b.getRecordId() == m.getRecordId()) {
                m.setPaymentStatus(b.getPaymentStatus());
                break;
            }
        }
    }
}
