package com.vehicle.service;

import com.vehicle.dao.BillingDAO;
import com.vehicle.dao.MaintenanceDAO;
import com.vehicle.model.Bill;
import com.vehicle.model.MaintenancePart;
import com.vehicle.model.MaintenanceRecord;
import java.util.List;

/**
 * Service managing billing calculations and invoices.
 * Strictly enforces: Total Bill = Labour Charges + Spare Parts Costs.
 */
public class BillingService {
    private final BillingDAO billingDAO = new BillingDAO();
    private final MaintenanceDAO maintenanceDAO = new MaintenanceDAO();

    public List<Bill> getAllBills() {
        return billingDAO.getAllBills();
    }

    public Bill getBillById(int id) {
        if (id <= 0) throw new IllegalArgumentException("Invalid bill ID");
        return billingDAO.getBillById(id);
    }

    public Bill getBillByRecordId(int recordId) {
        if (recordId <= 0) throw new IllegalArgumentException("Invalid maintenance record ID");
        return billingDAO.getBillByRecordId(recordId);
    }

    /**
     * Generates a bill from a maintenance record, calculating:
     * Spare Parts Costs = SUM(quantity * unit_price)
     * Total Bill = Labour Charges + Spare Parts Costs
     */
    public Bill generateBillForRecord(int recordId, String paymentMethod, String paymentStatus) {
        MaintenanceRecord record = maintenanceDAO.getRecordById(recordId);
        if (record == null) {
            throw new IllegalArgumentException("Maintenance record not found for ID: " + recordId);
        }

        // Check if bill already exists
        Bill existing = billingDAO.getBillByRecordId(recordId);
        if (existing != null) {
            return existing;
        }

        // Calculate Spare Parts Cost from maintenance parts
        List<MaintenancePart> parts = maintenanceDAO.getPartsByRecordId(recordId);
        double sparePartsCost = 0.0;
        for (MaintenancePart p : parts) {
            sparePartsCost += p.getSubtotal();
        }

        double labourCharges = record.getLabourCharges();
        // Formula: Total Bill = Labour Charges + Spare Parts Costs
        double totalAmount = labourCharges + sparePartsCost;

        String billingDate = record.getServiceDate();
        Bill bill = new Bill(0, recordId, labourCharges, sparePartsCost, totalAmount, 
                             paymentStatus != null ? paymentStatus : "Unpaid", 
                             paymentMethod != null ? paymentMethod : "Cash", 
                             billingDate);

        boolean created = billingDAO.createBill(bill);
        if (created) {
            return bill;
        }
        return null;
    }

    public boolean createCustomBill(Bill bill) {
        if (bill.getRecordId() <= 0) {
            throw new IllegalArgumentException("Valid Maintenance Record ID is required.");
        }
        // Force calculation: Total Bill = Labour Charges + Spare Parts Costs
        bill.recalculateTotal();
        return billingDAO.createBill(bill);
    }

    public boolean updatePaymentStatus(int billId, String paymentStatus, String paymentMethod) {
        if (billId <= 0) throw new IllegalArgumentException("Invalid bill ID");
        return billingDAO.updatePaymentStatus(billId, paymentStatus, paymentMethod);
    }

    public boolean deleteBill(int id) {
        if (id <= 0) throw new IllegalArgumentException("Invalid bill ID");
        return billingDAO.deleteBill(id);
    }
}
