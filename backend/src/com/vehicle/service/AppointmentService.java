package com.vehicle.service;

import com.vehicle.dao.AppointmentDAO;
import com.vehicle.model.Appointment;
import java.util.List;

/**
 * Service managing appointment scheduling, validation, and automated conflict detection.
 */
public class AppointmentService {
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.getAllAppointments();
    }

    public Appointment getAppointmentById(int id) {
        if (id <= 0) throw new IllegalArgumentException("Invalid appointment ID");
        return appointmentDAO.getAppointmentById(id);
    }

    public boolean scheduleAppointment(Appointment appointment) {
        if (appointment.getCustomerId() <= 0) {
            throw new IllegalArgumentException("Customer ID is required.");
        }
        if (appointment.getVehicleId() <= 0) {
            throw new IllegalArgumentException("Vehicle ID is required.");
        }
        if (appointment.getAppointmentDate() == null || appointment.getAppointmentDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment date is required.");
        }
        if (appointment.getAppointmentTime() == null || appointment.getAppointmentTime().trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment time is required.");
        }

        // Check for conflict
        if (appointmentDAO.hasSchedulingConflict(appointment.getVehicleId(), 
                                                 appointment.getAppointmentDate(), 
                                                 appointment.getAppointmentTime(), 0)) {
            throw new IllegalStateException("Scheduling Conflict: Vehicle already has an active appointment at this date and time.");
        }

        return appointmentDAO.scheduleAppointment(appointment);
    }

    public boolean updateAppointment(Appointment appointment) {
        if (appointment.getAppointmentId() <= 0) {
            throw new IllegalArgumentException("Invalid appointment ID for update.");
        }

        if (appointmentDAO.hasSchedulingConflict(appointment.getVehicleId(), 
                                                 appointment.getAppointmentDate(), 
                                                 appointment.getAppointmentTime(), 
                                                 appointment.getAppointmentId())) {
            throw new IllegalStateException("Scheduling Conflict: Vehicle already has an active appointment at the requested slot.");
        }

        return appointmentDAO.updateAppointment(appointment);
    }

    public boolean updateStatus(int appointmentId, String newStatus) {
        if (appointmentId <= 0) throw new IllegalArgumentException("Invalid appointment ID");
        return appointmentDAO.updateStatus(appointmentId, newStatus);
    }

    public boolean deleteAppointment(int id) {
        if (id <= 0) throw new IllegalArgumentException("Invalid appointment ID");
        return appointmentDAO.deleteAppointment(id);
    }
}
