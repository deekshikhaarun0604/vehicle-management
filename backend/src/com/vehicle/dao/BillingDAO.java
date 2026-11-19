package com.vehicle.dao;

import com.vehicle.config.DBConnection;
import com.vehicle.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Bills and Invoicing using JDBC.
 * Enforces business rule: Total Bill = Labour Charges + Spare Parts Costs.
 */
public class BillingDAO {

    public List<Bill> getAllBills() {
        if (!DBConnection.isMySqlAvailable()) {
            List<Bill> list = new ArrayList<>(DataStore.bills.values());
            for (Bill b : list) {
                populateBillDetails(b);
            }
            return list;
        }

        List<Bill> list = new ArrayList<>();
        String sql = "SELECT b.bill_id, b.record_id, b.labour_charges, b.spare_parts_cost, b.total_amount, " +
                     "b.payment_status, b.payment_method, b.billing_date, b.created_at, " +
                     "v.registration_number, v.model AS vehicle_model, " +
                     "c.name AS customer_name, c.phone AS customer_phone, c.address AS customer_address, " +
                     "m.work_performed " +
                     "FROM bills b " +
                     "JOIN maintenance_records m ON b.record_id = m.record_id " +
                     "JOIN vehicles v ON m.vehicle_id = v.vehicle_id " +
                     "JOIN customers c ON v.customer_id = c.customer_id " +
                     "ORDER BY b.bill_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Bill b = new Bill(
                    rs.getInt("bill_id"),
                    rs.getInt("record_id"),
                    rs.getDouble("labour_charges"),
                    rs.getDouble("spare_parts_cost"),
                    rs.getDouble("total_amount"),
                    rs.getString("payment_status"),
                    rs.getString("payment_method"),
                    rs.getString("billing_date")
                );
                b.setRegistrationNumber(rs.getString("registration_number"));
                b.setVehicleModel(rs.getString("vehicle_model"));
                b.setCustomerName(rs.getString("customer_name"));
                b.setCustomerPhone(rs.getString("customer_phone"));
                b.setCustomerAddress(rs.getString("customer_address"));
                b.setWorkPerformed(rs.getString("work_performed"));
                b.setCreatedAt(rs.getString("created_at"));
                b.setItemizedParts(new MaintenanceDAO().getPartsByRecordId(b.getRecordId()));
                list.add(b);
            }
        } catch (SQLException e) {
            System.err.println("[BillingDAO] Error fetching bills: " + e.getMessage());
            return new ArrayList<>(DataStore.bills.values());
        }
        return list;
    }

    public Bill getBillById(int id) {
        if (!DBConnection.isMySqlAvailable()) {
            Bill b = DataStore.bills.get(id);
            if (b != null) populateBillDetails(b);
            return b;
        }

        String sql = "SELECT b.bill_id, b.record_id, b.labour_charges, b.spare_parts_cost, b.total_amount, " +
                     "b.payment_status, b.payment_method, b.billing_date, b.created_at, " +
                     "v.registration_number, v.model AS vehicle_model, " +
                     "c.name AS customer_name, c.phone AS customer_phone, c.address AS customer_address, " +
                     "m.work_performed " +
                     "FROM bills b " +
                     "JOIN maintenance_records m ON b.record_id = m.record_id " +
                     "JOIN vehicles v ON m.vehicle_id = v.vehicle_id " +
                     "JOIN customers c ON v.customer_id = c.customer_id " +
                     "WHERE b.bill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Bill b = new Bill(
                        rs.getInt("bill_id"),
                        rs.getInt("record_id"),
                        rs.getDouble("labour_charges"),
                        rs.getDouble("spare_parts_cost"),
                        rs.getDouble("total_amount"),
                        rs.getString("payment_status"),
                        rs.getString("payment_method"),
                        rs.getString("billing_date")
                    );
                    b.setRegistrationNumber(rs.getString("registration_number"));
                    b.setVehicleModel(rs.getString("vehicle_model"));
                    b.setCustomerName(rs.getString("customer_name"));
                    b.setCustomerPhone(rs.getString("customer_phone"));
                    b.setCustomerAddress(rs.getString("customer_address"));
                    b.setWorkPerformed(rs.getString("work_performed"));
                    b.setCreatedAt(rs.getString("created_at"));
                    b.setItemizedParts(new MaintenanceDAO().getPartsByRecordId(b.getRecordId()));
                    return b;
                }
            }
        } catch (SQLException e) {
            System.err.println("[BillingDAO] Error fetching bill " + id + ": " + e.getMessage());
        }
        return null;
    }

    public Bill getBillByRecordId(int recordId) {
        if (!DBConnection.isMySqlAvailable()) {
            for (Bill b : DataStore.bills.values()) {
                if (b.getRecordId() == recordId) {
                    populateBillDetails(b);
                    return b;
                }
            }
            return null;
        }

        String sql = "SELECT bill_id FROM bills WHERE record_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recordId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return getBillById(rs.getInt("bill_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("[BillingDAO] Error fetching bill by record " + recordId + ": " + e.getMessage());
        }
        return null;
    }

    public boolean createBill(Bill bill) {
        // Enforce formula: Total Bill = Labour Charges + Spare Parts Costs
        bill.recalculateTotal();

        if (!DBConnection.isMySqlAvailable()) {
            int newId = DataStore.nextBillId();
            bill.setBillId(newId);
            bill.setCreatedAt("2026-11-19 12:00:00");
            populateBillDetails(bill);
            DataStore.bills.put(newId, bill);

            // Update maintenance record payment status
            MaintenanceRecord mr = DataStore.maintenanceRecords.get(bill.getRecordId());
            if (mr != null) {
                mr.setPaymentStatus(bill.getPaymentStatus());
            }
            return true;
        }

        String sql = "INSERT INTO bills (record_id, labour_charges, spare_parts_cost, total_amount, payment_status, payment_method, billing_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, bill.getRecordId());
            ps.setDouble(2, bill.getLabourCharges());
            ps.setDouble(3, bill.getSparePartsCost());
            ps.setDouble(4, bill.getTotalAmount());
            ps.setString(5, bill.getPaymentStatus() != null ? bill.getPaymentStatus() : "Unpaid");
            ps.setString(6, bill.getPaymentMethod() != null ? bill.getPaymentMethod() : "Cash");
            ps.setString(7, bill.getBillingDate() != null ? bill.getBillingDate() : "2026-11-19");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        bill.setBillId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[BillingDAO] Error creating bill: " + e.getMessage());
        }
        return false;
    }

    public boolean updatePaymentStatus(int billId, String paymentStatus, String paymentMethod) {
        if (!DBConnection.isMySqlAvailable()) {
            Bill b = DataStore.bills.get(billId);
            if (b != null) {
                b.setPaymentStatus(paymentStatus);
                b.setPaymentMethod(paymentMethod);
                MaintenanceRecord mr = DataStore.maintenanceRecords.get(b.getRecordId());
                if (mr != null) mr.setPaymentStatus(paymentStatus);
                return true;
            }
            return false;
        }

        String sql = "UPDATE bills SET payment_status = ?, payment_method = ? WHERE bill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, paymentStatus);
            ps.setString(2, paymentMethod);
            ps.setInt(3, billId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BillingDAO] Error updating payment status: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteBill(int id) {
        if (!DBConnection.isMySqlAvailable()) {
            return DataStore.bills.remove(id) != null;
        }

        String sql = "DELETE FROM bills WHERE bill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BillingDAO] Error deleting bill: " + e.getMessage());
        }
        return false;
    }

    private void populateBillDetails(Bill b) {
        MaintenanceRecord mr = DataStore.maintenanceRecords.get(b.getRecordId());
        if (mr != null) {
            b.setRegistrationNumber(mr.getRegistrationNumber());
            b.setVehicleModel(mr.getVehicleModel());
            b.setCustomerName(mr.getCustomerName());
            b.setCustomerPhone(mr.getCustomerPhone());
            b.setWorkPerformed(mr.getWorkPerformed());
            b.setItemizedParts(DataStore.maintenanceParts.getOrDefault(b.getRecordId(), new ArrayList<>()));
            Customer c = DataStore.customers.get(DataStore.vehicles.get(mr.getVehicleId()).getCustomerId());
            if (c != null) b.setCustomerAddress(c.getAddress());
        }
    }
}
