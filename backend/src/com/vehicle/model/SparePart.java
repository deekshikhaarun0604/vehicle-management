package com.vehicle.model;

/**
 * Spare Part inventory entity.
 */
public class SparePart {
    private int partId;
    private String partNumber;
    private String partName;
    private double unitPrice;
    private int stockQuantity;

    public SparePart() {}

    public SparePart(int partId, String partNumber, String partName, double unitPrice, int stockQuantity) {
        this.partId = partId;
        this.partNumber = partNumber;
        this.partName = partName;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @Override
    public String toString() {
        return "SparePart [ID=" + partId + ", Number=" + partNumber + ", Name=" + partName + ", Price=" + unitPrice + "]";
    }
}
