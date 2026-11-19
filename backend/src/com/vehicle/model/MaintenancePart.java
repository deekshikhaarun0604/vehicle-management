package com.vehicle.model;

/**
 * MaintenancePart entity representing a line item in a maintenance job.
 */
public class MaintenancePart {
    private int maintPartId;
    private int recordId;
    private int partId;
    private String partName; // Joined helper field
    private int quantity;
    private double unitPrice;
    private double subtotal;

    public MaintenancePart() {}

    public MaintenancePart(int maintPartId, int recordId, int partId, int quantity, double unitPrice, double subtotal) {
        this.maintPartId = maintPartId;
        this.recordId = recordId;
        this.partId = partId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public MaintenancePart(int recordId, int partId, int quantity, double unitPrice) {
        this.recordId = recordId;
        this.partId = partId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = quantity * unitPrice;
    }

    public int getMaintPartId() {
        return maintPartId;
    }

    public void setMaintPartId(int maintPartId) {
        this.maintPartId = maintPartId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = this.quantity * this.unitPrice;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.subtotal = this.quantity * this.unitPrice;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "MaintenancePart [Record=" + recordId + ", Part=" + partName + ", Qty=" + quantity + ", Subtotal=" + subtotal + "]";
    }
}
