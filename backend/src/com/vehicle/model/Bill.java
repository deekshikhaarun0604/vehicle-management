package com.vehicle.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Bill entity representing an invoice.
 * Formula: Total Bill = Labour Charges + Spare Parts Costs
 */
public class Bill {
    private int billId;
    private int recordId;
    private double labourCharges;
    private double sparePartsCost;
    private double totalAmount;
    private String paymentStatus; // Paid, Unpaid, Partially Paid
    private String paymentMethod; // Cash, Credit Card, Debit Card, UPI, Net Banking
    private String billingDate;
    private String createdAt;

    // Joined helper fields
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String registrationNumber;
    private String vehicleModel;
    private String workPerformed;
    private List<MaintenancePart> itemizedParts = new ArrayList<>();

    public Bill() {}

    public Bill(int billId, int recordId, double labourCharges, double sparePartsCost, 
                String paymentStatus, String paymentMethod, String billingDate) {
        this.billId = billId;
        this.recordId = recordId;
        this.labourCharges = labourCharges;
        this.sparePartsCost = sparePartsCost;
        this.totalAmount = labourCharges + sparePartsCost; // Strictly enforce formula
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.billingDate = billingDate;
    }

    public Bill(int billId, int recordId, double labourCharges, double sparePartsCost, 
                double totalAmount, String paymentStatus, String paymentMethod, String billingDate) {
        this.billId = billId;
        this.recordId = recordId;
        this.labourCharges = labourCharges;
        this.sparePartsCost = sparePartsCost;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.billingDate = billingDate;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public double getLabourCharges() {
        return labourCharges;
    }

    public void setLabourCharges(double labourCharges) {
        this.labourCharges = labourCharges;
        recalculateTotal();
    }

    public double getSparePartsCost() {
        return sparePartsCost;
    }

    public void setSparePartsCost(double sparePartsCost) {
        this.sparePartsCost = sparePartsCost;
        recalculateTotal();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void recalculateTotal() {
        this.totalAmount = this.labourCharges + this.sparePartsCost;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(String billingDate) {
        this.billingDate = billingDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
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

    public String getWorkPerformed() {
        return workPerformed;
    }

    public void setWorkPerformed(String workPerformed) {
        this.workPerformed = workPerformed;
    }

    public List<MaintenancePart> getItemizedParts() {
        return itemizedParts;
    }

    public void setItemizedParts(List<MaintenancePart> itemizedParts) {
        this.itemizedParts = itemizedParts;
    }

    @Override
    public String toString() {
        return "Bill [ID=" + billId + ", Record=" + recordId + ", Labour=" + labourCharges + 
               ", Parts=" + sparePartsCost + ", Total=" + totalAmount + ", Status=" + paymentStatus + "]";
    }
}
