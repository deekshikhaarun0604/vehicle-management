package com.vehicle.dao;

import com.vehicle.config.DBConnection;
import com.vehicle.model.Customer;
import com.vehicle.model.Vehicle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Vehicles using JDBC.
 */
public class VehicleDAO {

    public List<Vehicle> getAllVehicles() {
        if (!DBConnection.isMySqlAvailable()) {
            List<Vehicle> list = new ArrayList<>(DataStore.vehicles.values());
            for (Vehicle v : list) {
                Customer c = DataStore.customers.get(v.getCustomerId());
                if (c != null) v.setCustomerName(c.getName());
            }
            return list;
        }

        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT v.vehicle_id, v.customer_id, v.registration_number, v.model, v.vehicle_type, " +
                     "v.manufacture_year, v.created_at, c.name AS customer_name " +
                     "FROM vehicles v " +
                     "JOIN customers c ON v.customer_id = c.customer_id " +
                     "ORDER BY v.vehicle_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Vehicle v = new Vehicle(
                    rs.getInt("vehicle_id"),
                    rs.getInt("customer_id"),
                    rs.getString("registration_number"),
                    rs.getString("model"),
                    rs.getString("vehicle_type"),
                    rs.getInt("manufacture_year")
                );
                v.setCustomerName(rs.getString("customer_name"));
                v.setCreatedAt(rs.getString("created_at"));
                list.add(v);
            }
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] Error fetching vehicles: " + e.getMessage());
            return new ArrayList<>(DataStore.vehicles.values());
        }
        return list;
    }

    public Vehicle getVehicleById(int id) {
        if (!DBConnection.isMySqlAvailable()) {
            Vehicle v = DataStore.vehicles.get(id);
            if (v != null) {
                Customer c = DataStore.customers.get(v.getCustomerId());
                if (c != null) v.setCustomerName(c.getName());
            }
            return v;
        }

        String sql = "SELECT v.vehicle_id, v.customer_id, v.registration_number, v.model, v.vehicle_type, " +
                     "v.manufacture_year, v.created_at, c.name AS customer_name " +
                     "FROM vehicles v " +
                     "JOIN customers c ON v.customer_id = c.customer_id " +
                     "WHERE v.vehicle_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Vehicle v = new Vehicle(
                        rs.getInt("vehicle_id"),
                        rs.getInt("customer_id"),
                        rs.getString("registration_number"),
                        rs.getString("model"),
                        rs.getString("vehicle_type"),
                        rs.getInt("manufacture_year")
                    );
                    v.setCustomerName(rs.getString("customer_name"));
                    v.setCreatedAt(rs.getString("created_at"));
                    return v;
                }
            }
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] Error fetching vehicle " + id + ": " + e.getMessage());
        }
        return null;
    }

    public boolean addVehicle(Vehicle vehicle) {
        if (!DBConnection.isMySqlAvailable()) {
            int newId = DataStore.nextVehicleId();
            vehicle.setVehicleId(newId);
            vehicle.setCreatedAt("2026-11-19 12:00:00");
            Customer c = DataStore.customers.get(vehicle.getCustomerId());
            if (c != null) vehicle.setCustomerName(c.getName());
            DataStore.vehicles.put(newId, vehicle);
            return true;
        }

        String sql = "INSERT INTO vehicles (customer_id, registration_number, model, vehicle_type, manufacture_year) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, vehicle.getCustomerId());
            ps.setString(2, vehicle.getRegistrationNumber());
            ps.setString(3, vehicle.getModel());
            ps.setString(4, vehicle.getVehicleType());
            ps.setInt(5, vehicle.getManufactureYear());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        vehicle.setVehicleId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] Error inserting vehicle: " + e.getMessage());
        }
        return false;
    }

    public boolean updateVehicle(Vehicle vehicle) {
        if (!DBConnection.isMySqlAvailable()) {
            if (DataStore.vehicles.containsKey(vehicle.getVehicleId())) {
                Customer c = DataStore.customers.get(vehicle.getCustomerId());
                if (c != null) vehicle.setCustomerName(c.getName());
                DataStore.vehicles.put(vehicle.getVehicleId(), vehicle);
                return true;
            }
            return false;
        }

        String sql = "UPDATE vehicles SET customer_id = ?, registration_number = ?, model = ?, " +
                     "vehicle_type = ?, manufacture_year = ? WHERE vehicle_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vehicle.getCustomerId());
            ps.setString(2, vehicle.getRegistrationNumber());
            ps.setString(3, vehicle.getModel());
            ps.setString(4, vehicle.getVehicleType());
            ps.setInt(5, vehicle.getManufactureYear());
            ps.setInt(6, vehicle.getVehicleId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] Error updating vehicle: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteVehicle(int id) {
        if (!DBConnection.isMySqlAvailable()) {
            Vehicle removed = DataStore.vehicles.remove(id);
            if (removed != null) {
                DataStore.appointments.values().removeIf(a -> a.getVehicleId() == id);
                DataStore.maintenanceRecords.values().removeIf(m -> m.getVehicleId() == id);
                return true;
            }
            return false;
        }

        String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] Error deleting vehicle: " + e.getMessage());
        }
        return false;
    }
}
