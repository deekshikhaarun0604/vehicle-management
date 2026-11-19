package com.vehicle.dao;

import com.vehicle.config.DBConnection;
import com.vehicle.model.SparePart;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Spare Parts inventory using JDBC.
 */
public class SparePartDAO {

    public List<SparePart> getAllParts() {
        if (!DBConnection.isMySqlAvailable()) {
            return new ArrayList<>(DataStore.spareParts.values());
        }

        List<SparePart> list = new ArrayList<>();
        String sql = "SELECT part_id, part_number, part_name, unit_price, stock_quantity FROM spare_parts ORDER BY part_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new SparePart(
                    rs.getInt("part_id"),
                    rs.getString("part_number"),
                    rs.getString("part_name"),
                    rs.getDouble("unit_price"),
                    rs.getInt("stock_quantity")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[SparePartDAO] Error fetching spare parts: " + e.getMessage());
            return new ArrayList<>(DataStore.spareParts.values());
        }
        return list;
    }

    public SparePart getPartById(int id) {
        if (!DBConnection.isMySqlAvailable()) {
            return DataStore.spareParts.get(id);
        }

        String sql = "SELECT part_id, part_number, part_name, unit_price, stock_quantity FROM spare_parts WHERE part_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new SparePart(
                        rs.getInt("part_id"),
                        rs.getString("part_number"),
                        rs.getString("part_name"),
                        rs.getDouble("unit_price"),
                        rs.getInt("stock_quantity")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[SparePartDAO] Error fetching part " + id + ": " + e.getMessage());
        }
        return null;
    }

    public boolean addPart(SparePart part) {
        if (!DBConnection.isMySqlAvailable()) {
            int newId = DataStore.nextSparePartId();
            part.setPartId(newId);
            DataStore.spareParts.put(newId, part);
            return true;
        }

        String sql = "INSERT INTO spare_parts (part_number, part_name, unit_price, stock_quantity) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, part.getPartNumber());
            ps.setString(2, part.getPartName());
            ps.setDouble(3, part.getUnitPrice());
            ps.setInt(4, part.getStockQuantity());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        part.setPartId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[SparePartDAO] Error inserting part: " + e.getMessage());
        }
        return false;
    }

    public boolean updateStock(int partId, int quantityToDeduct) {
        if (!DBConnection.isMySqlAvailable()) {
            SparePart sp = DataStore.spareParts.get(partId);
            if (sp != null && sp.getStockQuantity() >= quantityToDeduct) {
                sp.setStockQuantity(sp.getStockQuantity() - quantityToDeduct);
                return true;
            }
            return false;
        }

        String sql = "UPDATE spare_parts SET stock_quantity = stock_quantity - ? WHERE part_id = ? AND stock_quantity >= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantityToDeduct);
            ps.setInt(2, partId);
            ps.setInt(3, quantityToDeduct);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[SparePartDAO] Error updating stock: " + e.getMessage());
        }
        return false;
    }
}
