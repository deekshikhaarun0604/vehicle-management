-- =====================================================================
-- Vehicle Service Management and Maintenance Analysis System
-- Multi-Table JOIN Queries
-- File: 06_join_queries.sql
-- =====================================================================

USE vehicle_service_db;

-- 1. Complete Service History with Customer and Vehicle Details (Multi-Table JOIN)
SELECT 
    m.record_id,
    m.service_date,
    v.registration_number,
    v.model AS vehicle_model,
    v.vehicle_type,
    c.name AS customer_name,
    c.phone AS customer_phone,
    m.work_performed,
    m.labour_charges,
    COALESCE(b.spare_parts_cost, 0.00) AS parts_cost,
    COALESCE(b.total_amount, m.labour_charges) AS total_bill,
    COALESCE(b.payment_status, 'Not Billed') AS payment_status
FROM maintenance_records m
JOIN vehicles v ON m.vehicle_id = v.vehicle_id
JOIN customers c ON v.customer_id = c.customer_id
LEFT JOIN bills b ON m.record_id = b.record_id
ORDER BY m.service_date DESC;

-- 2. Detailed Bill Invoice with Spare Parts Itemization
SELECT 
    b.bill_id,
    b.billing_date,
    c.name AS customer_name,
    c.phone AS customer_phone,
    c.address AS customer_address,
    v.registration_number,
    v.model AS vehicle_model,
    m.work_performed,
    sp.part_name,
    mp.quantity,
    mp.unit_price,
    mp.subtotal AS item_total,
    b.labour_charges,
    b.spare_parts_cost,
    b.total_amount,
    b.payment_status,
    b.payment_method
FROM bills b
JOIN maintenance_records m ON b.record_id = m.record_id
JOIN vehicles v ON m.vehicle_id = v.vehicle_id
JOIN customers c ON v.customer_id = c.customer_id
LEFT JOIN maintenance_parts mp ON m.record_id = mp.record_id
LEFT JOIN spare_parts sp ON mp.part_id = sp.part_id
WHERE b.bill_id = 1;

-- 3. Customer Vehicle Portfolio (LEFT JOIN to find customers with/without vehicles)
SELECT 
    c.customer_id,
    c.name,
    c.phone,
    COUNT(v.vehicle_id) AS total_vehicles,
    GROUP_CONCAT(CONCAT(v.model, ' (', v.registration_number, ')') SEPARATOR ', ') AS vehicle_list
FROM customers c
LEFT JOIN vehicles v ON c.customer_id = v.customer_id
GROUP BY c.customer_id, c.name, c.phone;

-- 4. Appointment to Maintenance Conversion Pipeline
SELECT 
    a.appointment_id,
    a.appointment_date,
    a.service_type,
    a.status AS appointment_status,
    v.registration_number,
    c.name AS customer_name,
    m.record_id AS maintenance_record_id,
    m.status AS maintenance_status,
    b.bill_id,
    b.total_amount
FROM appointments a
JOIN customers c ON a.customer_id = c.customer_id
JOIN vehicles v ON a.vehicle_id = v.vehicle_id
LEFT JOIN maintenance_records m ON a.appointment_id = m.appointment_id
LEFT JOIN bills b ON m.record_id = b.record_id
ORDER BY a.appointment_date ASC;
