package com.vehicle.service;

import com.vehicle.dao.ReportDAO;
import java.util.List;
import java.util.Map;

/**
 * Service generating business reports and dashboard analytics.
 */
public class ReportService {
    private final ReportDAO reportDAO = new ReportDAO();

    public List<Map<String, Object>> getServiceHistory(Integer vehicleId) {
        return reportDAO.getServiceHistory(vehicleId);
    }

    public List<Map<String, Object>> getVehicleWiseMaintenanceCosts() {
        return reportDAO.getVehicleWiseMaintenanceCosts();
    }

    public Map<String, Object> getServiceCenterWorkload() {
        return reportDAO.getServiceCenterWorkload();
    }

    public Map<String, Object> getDashboardStats() {
        return reportDAO.getDashboardStats();
    }
}
