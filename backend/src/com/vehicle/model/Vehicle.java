package com.vehicle.model;

/**
 * Vehicle entity representing an automobile registered in the service center.
 */
public class Vehicle {
    private int vehicleId;
    private int customerId;
    private String registrationNumber;
    private String model;
    private String vehicleType; // Sedan, SUV, Hatchback, Truck, Van, Motorcycle
    private int manufactureYear;
    private String customerName; // Joined helper field
    private String createdAt;

    public Vehicle() {}

    public Vehicle(int vehicleId, int customerId, String registrationNumber, String model, String vehicleType, int manufactureYear) {
        this.vehicleId = vehicleId;
        this.customerId = customerId;
        this.registrationNumber = registrationNumber;
        this.model = model;
        this.vehicleType = vehicleType;
        this.manufactureYear = manufactureYear;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(int manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Vehicle [ID=" + vehicleId + ", Reg=" + registrationNumber + ", Model=" + model + ", Type=" + vehicleType + "]";
    }
}
