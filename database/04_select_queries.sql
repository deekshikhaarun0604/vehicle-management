-- =====================================================================
-- Vehicle Service Management and Maintenance Analysis System
-- Core SELECT Queries
-- File: 04_select_queries.sql
-- =====================================================================

USE vehicle_service_db;

-- 1. View all customer records sorted by name
SELECT customer_id, name, phone, email, address, created_at 
FROM customers 
ORDER BY name ASC;

-- 2. View all vehicles with owner information
SELECT v.vehicle_id, v.registration_number, v.model, v.vehicle_type, v.manufacture_year, c.name AS owner_name, c.phone AS owner_phone
FROM vehicles v
JOIN customers c ON v.customer_id = c.customer_id
ORDER BY v.vehicle_id ASC;

-- 3. View upcoming service appointments (Scheduled or In Progress)
SELECT a.appointment_id, a.appointment_date, a.appointment_time, a.service_type, a.status,
       c.name AS customer_name, v.registration_number, v.model
FROM appointments a
JOIN customers c ON a.customer_id = c.customer_id
JOIN vehicles v ON a.vehicle_id = v.vehicle_id
WHERE a.status IN ('Scheduled', 'In Progress')
ORDER BY a.appointment_date ASC, a.appointment_time ASC;

-- 4. View spare parts inventory with low stock warning (stock < 30)
SELECT part_id, part_number, part_name, unit_price, stock_quantity,
       CASE 
           WHEN stock_quantity < 20 THEN 'CRITICAL LOW'
           WHEN stock_quantity < 35 THEN 'REORDER SOON'
           ELSE 'ADEQUATE'
       END AS stock_status
FROM spare_parts
ORDER BY stock_quantity ASC;

-- 5. View completed maintenance records with labour charges
SELECT record_id, service_date, work_performed, labour_charges, status
FROM maintenance_records
WHERE status = 'Completed'
ORDER BY service_date DESC;

-- 6. View all unpaid bills
SELECT b.bill_id, b.billing_date, b.total_amount, b.payment_status,
       c.name AS customer_name, c.phone AS customer_phone, v.registration_number
FROM bills b
JOIN maintenance_records m ON b.record_id = m.record_id
JOIN vehicles v ON m.vehicle_id = v.vehicle_id
JOIN customers c ON v.customer_id = c.customer_id
WHERE b.payment_status = 'Unpaid';
