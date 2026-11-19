package com.vehicle.service;

import com.vehicle.dao.VehicleDAO;
import com.vehicle.model.Vehicle;
import java.util.List;

/**
 * Service handling vehicle management and validation.
 */
public class VehicleService {
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    public List<Vehicle> getAllVehicles() {
        return vehicleDAO.getAllVehicles();
    }

    public Vehicle getVehicleById(int id) {
        if (id <= 0) throw new IllegalArgumentException("Invalid vehicle ID");
        return vehicleDAO.getVehicleById(id);
    }

    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle.getCustomerId() <= 0) {
            throw new IllegalArgumentException("Valid Customer ID is required.");
        }
        if (vehicle.getRegistrationNumber() == null || vehicle.getRegistrationNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number is required.");
        }
        if (vehicle.getModel() == null || vehicle.getModel().trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle model is required.");
        }
        if (vehicle.getManufactureYear() < 1990 || vehicle.getManufactureYear() > 2030) {
            throw new IllegalArgumentException("Manufacture year must be between 1990 and 2030.");
        }
        return vehicleDAO.addVehicle(vehicle);
    }

    public boolean updateVehicle(Vehicle vehicle) {
        if (vehicle.getVehicleId() <= 0) {
            throw new IllegalArgumentException("Invalid vehicle ID for update.");
        }
        return vehicleDAO.updateVehicle(vehicle);
    }

    public boolean deleteVehicle(int id) {
        if (id <= 0) throw new IllegalArgumentException("Invalid vehicle ID");
        return vehicleDAO.deleteVehicle(id);
    }
}
