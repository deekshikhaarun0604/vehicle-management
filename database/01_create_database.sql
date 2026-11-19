-- =====================================================================
-- Vehicle Service Management and Maintenance Analysis System
-- Database Creation Script
-- File: 01_create_database.sql
-- =====================================================================

CREATE DATABASE IF NOT EXISTS vehicle_service_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE vehicle_service_db;

SELECT 'Database vehicle_service_db created and selected successfully.' AS Status;
