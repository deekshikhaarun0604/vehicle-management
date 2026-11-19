package com.vehicle.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintenance and Service Record entity.
 */
public class MaintenanceRecord {
    private int recordId;
    private Integer appointmentId; // Nullable
    private int vehicleId;
    private String serviceDate;
    private String workPerformed;
    private double labourCharges;
    private String status; // Pending, In Progress, Completed
    private String createdAt;

    // Joined helper fields
    private String registrationNumber;
    private String vehicleModel;
    private String vehicleType;
    private String customerName;
    private String customerPhone;
    private double sparePartsCost;
    private double totalBill;
    private String paymentStatus;

    // Associated spare parts list
    private List<MaintenancePart> partsUsed = new ArrayList<>();

    public MaintenanceRecord() {}

    public MaintenanceRecord(int recordId, Integer appointmentId, int vehicleId, String serviceDate, 
                             String workPerformed, double labourCharges, String status) {
        this.recordId = recordId;
        this.appointmentId = appointmentId;
        this.vehicleId = vehicleId;
        this.serviceDate = serviceDate;
        this.workPerformed = workPerformed;
        this.labourCharges = labourCharges;
        this.status = status;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getWorkPerformed() {
        return workPerformed;
    }

    public void setWorkPerformed(String workPerformed) {
        this.workPerformed = workPerformed;
    }

    public double getLabourCharges() {
        return labourCharges;
    }

    public void setLabourCharges(double labourCharges) {
        this.labourCharges = labourCharges;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public double getSparePartsCost() {
        return sparePartsCost;
    }

    public void setSparePartsCost(double sparePartsCost) {
        this.sparePartsCost = sparePartsCost;
    }

    public double getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(double totalBill) {
        this.totalBill = totalBill;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public List<MaintenancePart> getPartsUsed() {
        return partsUsed;
    }

    public void setPartsUsed(List<MaintenancePart> partsUsed) {
        this.partsUsed = partsUsed;
    }

    public void addPartUsed(MaintenancePart part) {
        this.partsUsed.add(part);
    }

    @Override
    public String toString() {
        return "MaintenanceRecord [ID=" + recordId + ", Vehicle=" + vehicleId + ", Date=" + serviceDate + 
               ", Labour=" + labourCharges + ", Status=" + status + "]";
    }
}
