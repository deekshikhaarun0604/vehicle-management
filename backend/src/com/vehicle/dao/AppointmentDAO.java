package com.vehicle.dao;

import com.vehicle.config.DBConnection;
import com.vehicle.model.Appointment;
import com.vehicle.model.Customer;
import com.vehicle.model.Vehicle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Appointments using JDBC.
 * Includes conflict detection methods.
 */
public class AppointmentDAO {

    public List<Appointment> getAllAppointments() {
        if (!DBConnection.isMySqlAvailable()) {
            List<Appointment> list = new ArrayList<>(DataStore.appointments.values());
            for (Appointment a : list) {
                populateJoinedFields(a);
            }
            return list;
        }

        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.appointment_id, a.customer_id, a.vehicle_id, a.appointment_date, " +
                     "a.appointment_time, a.service_type, a.status, a.notes, a.created_at, " +
                     "c.name AS customer_name, v.registration_number, v.model AS vehicle_model " +
                     "FROM appointments a " +
                     "JOIN customers c ON a.customer_id = c.customer_id " +
                     "JOIN vehicles v ON a.vehicle_id = v.vehicle_id " +
                     "ORDER BY a.appointment_date ASC, a.appointment_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Appointment a = new Appointment(
                    rs.getInt("appointment_id"),
                    rs.getInt("customer_id"),
                    rs.getInt("vehicle_id"),
                    rs.getString("appointment_date"),
                    rs.getString("appointment_time"),
                    rs.getString("service_type"),
                    rs.getString("status"),
                    rs.getString("notes")
                );
                a.setCustomerName(rs.getString("customer_name"));
                a.setRegistrationNumber(rs.getString("registration_number"));
                a.setVehicleModel(rs.getString("vehicle_model"));
                a.setCreatedAt(rs.getString("created_at"));
                list.add(a);
            }
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] Error fetching appointments: " + e.getMessage());
            return new ArrayList<>(DataStore.appointments.values());
        }
        return list;
    }

    public Appointment getAppointmentById(int id) {
        if (!DBConnection.isMySqlAvailable()) {
            Appointment a = DataStore.appointments.get(id);
            if (a != null) populateJoinedFields(a);
            return a;
        }

        String sql = "SELECT a.appointment_id, a.customer_id, a.vehicle_id, a.appointment_date, " +
                     "a.appointment_time, a.service_type, a.status, a.notes, a.created_at, " +
                     "c.name AS customer_name, v.registration_number, v.model AS vehicle_model " +
                     "FROM appointments a " +
                     "JOIN customers c ON a.customer_id = c.customer_id " +
                     "JOIN vehicles v ON a.vehicle_id = v.vehicle_id " +
                     "WHERE a.appointment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Appointment a = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("appointment_date"),
                        rs.getString("appointment_time"),
                        rs.getString("service_type"),
                        rs.getString("status"),
                        rs.getString("notes")
                    );
                    a.setCustomerName(rs.getString("customer_name"));
                    a.setRegistrationNumber(rs.getString("registration_number"));
                    a.setVehicleModel(rs.getString("vehicle_model"));
                    a.setCreatedAt(rs.getString("created_at"));
                    return a;
                }
            }
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] Error fetching appointment " + id + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Checks if a conflicting appointment already exists for the given vehicle at the given date/time slot.
     */
    public boolean hasSchedulingConflict(int vehicleId, String date, String time, int excludeAppointmentId) {
        if (!DBConnection.isMySqlAvailable()) {
            for (Appointment a : DataStore.appointments.values()) {
                if (a.getAppointmentId() == excludeAppointmentId) continue;
                if (!"Cancelled".equalsIgnoreCase(a.getStatus()) &&
                    a.getVehicleId() == vehicleId &&
                    date.equals(a.getAppointmentDate()) &&
                    time.equals(a.getAppointmentTime())) {
                    return true;
                }
            }
            return false;
        }

        String sql = "SELECT COUNT(*) FROM appointments " +
                     "WHERE vehicle_id = ? AND appointment_date = ? AND appointment_time = ? " +
                     "AND status != 'Cancelled' AND appointment_id != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vehicleId);
            ps.setString(2, date);
            ps.setString(3, time);
            ps.setInt(4, excludeAppointmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] Error checking conflict: " + e.getMessage());
        }
        return false;
    }

    public boolean scheduleAppointment(Appointment appointment) {
        // Enforce conflict checking
        if (hasSchedulingConflict(appointment.getVehicleId(), appointment.getAppointmentDate(), 
                                  appointment.getAppointmentTime(), 0)) {
            System.err.println("[AppointmentDAO] Conflict detected: Vehicle is already booked for this slot.");
            return false;
        }

        if (!DBConnection.isMySqlAvailable()) {
            int newId = DataStore.nextAppointmentId();
            appointment.setAppointmentId(newId);
            appointment.setCreatedAt("2026-11-19 12:00:00");
            populateJoinedFields(appointment);
            DataStore.appointments.put(newId, appointment);
            return true;
        }

        String sql = "INSERT INTO appointments (customer_id, vehicle_id, appointment_date, appointment_time, service_type, status, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, appointment.getCustomerId());
            ps.setInt(2, appointment.getVehicleId());
            ps.setString(3, appointment.getAppointmentDate());
            ps.setString(4, appointment.getAppointmentTime());
            ps.setString(5, appointment.getServiceType());
            ps.setString(6, appointment.getStatus() != null ? appointment.getStatus() : "Scheduled");
            ps.setString(7, appointment.getNotes());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        appointment.setAppointmentId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] Error scheduling appointment: " + e.getMessage());
        }
        return false;
    }

    public boolean updateAppointment(Appointment appointment) {
        if (hasSchedulingConflict(appointment.getVehicleId(), appointment.getAppointmentDate(), 
                                  appointment.getAppointmentTime(), appointment.getAppointmentId())) {
            System.err.println("[AppointmentDAO] Conflict detected on update.");
            return false;
        }

        if (!DBConnection.isMySqlAvailable()) {
            if (DataStore.appointments.containsKey(appointment.getAppointmentId())) {
                populateJoinedFields(appointment);
                DataStore.appointments.put(appointment.getAppointmentId(), appointment);
                return true;
            }
            return false;
        }

        String sql = "UPDATE appointments SET customer_id = ?, vehicle_id = ?, appointment_date = ?, " +
                     "appointment_time = ?, service_type = ?, status = ?, notes = ? WHERE appointment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, appointment.getCustomerId());
            ps.setInt(2, appointment.getVehicleId());
            ps.setString(3, appointment.getAppointmentDate());
            ps.setString(4, appointment.getAppointmentTime());
            ps.setString(5, appointment.getServiceType());
            ps.setString(6, appointment.getStatus());
            ps.setString(7, appointment.getNotes());
            ps.setInt(8, appointment.getAppointmentId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] Error updating appointment: " + e.getMessage());
        }
        return false;
    }

    public boolean updateStatus(int appointmentId, String newStatus) {
        if (!DBConnection.isMySqlAvailable()) {
            Appointment a = DataStore.appointments.get(appointmentId);
            if (a != null) {
                a.setStatus(newStatus);
                return true;
            }
            return false;
        }

        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] Error updating status: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteAppointment(int id) {
        if (!DBConnection.isMySqlAvailable()) {
            return DataStore.appointments.remove(id) != null;
        }

        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] Error deleting appointment: " + e.getMessage());
        }
        return false;
    }

    private void populateJoinedFields(Appointment a) {
        Customer c = DataStore.customers.get(a.getCustomerId());
        if (c != null) a.setCustomerName(c.getName());
        Vehicle v = DataStore.vehicles.get(a.getVehicleId());
        if (v != null) {
            a.setRegistrationNumber(v.getRegistrationNumber());
            a.setVehicleModel(v.getModel());
        }
    }
}
