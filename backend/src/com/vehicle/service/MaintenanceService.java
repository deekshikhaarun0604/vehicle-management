package com.vehicle.service;

import com.vehicle.dao.MaintenanceDAO;
import com.vehicle.dao.SparePartDAO;
import com.vehicle.model.MaintenancePart;
import com.vehicle.model.MaintenanceRecord;
import com.vehicle.model.SparePart;
import java.util.List;

/**
 * Service managing vehicle maintenance records and spare parts consumption.
 */
public class MaintenanceService {
    private final MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
    private final SparePartDAO sparePartDAO = new SparePartDAO();

    public List<MaintenanceRecord> getAllRecords() {
        return maintenanceDAO.getAllRecords();
    }

    public MaintenanceRecord getRecordById(int id) {
        if (id <= 0) throw new IllegalArgumentException("Invalid maintenance record ID");
        return maintenanceDAO.getRecordById(id);
    }

    public List<SparePart> getAllSpareParts() {
        return sparePartDAO.getAllParts();
    }

    public int createMaintenanceRecord(MaintenanceRecord record) {
        if (record.getVehicleId() <= 0) {
            throw new IllegalArgumentException("Vehicle ID is required.");
        }
        if (record.getServiceDate() == null || record.getServiceDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Service date is required.");
        }
        if (record.getWorkPerformed() == null || record.getWorkPerformed().trim().isEmpty()) {
            throw new IllegalArgumentException("Work performed description is required.");
        }
        if (record.getLabourCharges() < 0) {
            throw new IllegalArgumentException("Labour charges cannot be negative.");
        }

        // Calculate parts cost
        double partsCost = 0.0;
        if (record.getPartsUsed() != null) {
            for (MaintenancePart mp : record.getPartsUsed()) {
                if (mp.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Spare part quantity must be greater than zero.");
                }
                mp.setSubtotal(mp.getQuantity() * mp.getUnitPrice());
                partsCost += mp.getSubtotal();
            }
        }
        record.setSparePartsCost(partsCost);
        record.setTotalBill(record.getLabourCharges() + partsCost);

        return maintenanceDAO.addRecord(record);
    }

    public boolean updateMaintenanceRecord(MaintenanceRecord record) {
        if (record.getRecordId() <= 0) {
            throw new IllegalArgumentException("Invalid record ID for update.");
        }
        return maintenanceDAO.updateRecord(record);
    }

    public boolean deleteRecord(int id) {
        if (id <= 0) throw new IllegalArgumentException("Invalid record ID");
        return maintenanceDAO.deleteRecord(id);
    }
}
