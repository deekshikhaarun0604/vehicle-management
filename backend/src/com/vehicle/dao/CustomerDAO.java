package com.vehicle.dao;

import com.vehicle.config.DBConnection;
import com.vehicle.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Customers using JDBC.
 */
public class CustomerDAO {

    public List<Customer> getAllCustomers() {
        if (!DBConnection.isMySqlAvailable()) {
            return new ArrayList<>(DataStore.customers.values());
        }

        List<Customer> list = new ArrayList<>();
        String sql = "SELECT customer_id, name, phone, email, address, created_at FROM customers ORDER BY customer_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Customer c = new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("created_at")
                );
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error fetching customers: " + e.getMessage());
            return new ArrayList<>(DataStore.customers.values());
        }
        return list;
    }

    public Customer getCustomerById(int id) {
        if (!DBConnection.isMySqlAvailable()) {
            return DataStore.customers.get(id);
        }

        String sql = "SELECT customer_id, name, phone, email, address, created_at FROM customers WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error fetching customer " + id + ": " + e.getMessage());
            return DataStore.customers.get(id);
        }
        return null;
    }

    public boolean addCustomer(Customer customer) {
        if (!DBConnection.isMySqlAvailable()) {
            int newId = DataStore.nextCustomerId();
            customer.setCustomerId(newId);
            customer.setCreatedAt("2026-11-19 12:00:00");
            DataStore.customers.put(newId, customer);
            return true;
        }

        String sql = "INSERT INTO customers (name, phone, email, address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getAddress());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        customer.setCustomerId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error inserting customer: " + e.getMessage());
        }
        return false;
    }

    public boolean updateCustomer(Customer customer) {
        if (!DBConnection.isMySqlAvailable()) {
            if (DataStore.customers.containsKey(customer.getCustomerId())) {
                DataStore.customers.put(customer.getCustomerId(), customer);
                return true;
            }
            return false;
        }

        String sql = "UPDATE customers SET name = ?, phone = ?, email = ?, address = ? WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getAddress());
            ps.setInt(5, customer.getCustomerId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error updating customer: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteCustomer(int id) {
        if (!DBConnection.isMySqlAvailable()) {
            Customer removed = DataStore.customers.remove(id);
            if (removed != null) {
                // Cascade delete vehicles and appointments
                DataStore.vehicles.values().removeIf(v -> v.getCustomerId() == id);
                DataStore.appointments.values().removeIf(a -> a.getCustomerId() == id);
                return true;
            }
            return false;
        }

        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error deleting customer: " + e.getMessage());
        }
        return false;
    }
}
