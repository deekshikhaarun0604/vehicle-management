-- =====================================================================
-- Vehicle Service Management and Maintenance Analysis System
-- CRUD Operations Script (Create, Read, Update, Delete)
-- File: 05_crud_queries.sql
-- =====================================================================

USE vehicle_service_db;

-- -------------------------------------------------------------
-- 1. CUSTOMER CRUD OPERATIONS
-- -------------------------------------------------------------
-- CREATE
INSERT INTO customers (name, phone, email, address) 
VALUES ('Rohan Gupta', '+91 99887 66554', 'rohan.gupta@example.com', '12 Lake Road, Bengaluru');

-- READ
SELECT * FROM customers WHERE customer_id = 1;
SELECT * FROM customers WHERE phone = '+91 98765 43210';

-- UPDATE
UPDATE customers 
SET phone = '+91 99887 00000', address = '55 Residency Road, Bengaluru' 
WHERE customer_id = 1;

-- DELETE
-- Note: Deleting a customer cascades to their vehicles, appointments, etc.
-- DELETE FROM customers WHERE customer_id = 10;


-- -------------------------------------------------------------
-- 2. VEHICLE CRUD OPERATIONS
-- -------------------------------------------------------------
-- CREATE
INSERT INTO vehicles (customer_id, registration_number, model, vehicle_type, manufacture_year)
VALUES (1, 'KA-05-XY-9999', 'Hyundai Verna 1.5 Turbo', 'Sedan', 2023);

-- READ
SELECT * FROM vehicles WHERE registration_number = 'KA-01-AB-1234';
SELECT * FROM vehicles WHERE customer_id = 1;

-- UPDATE
UPDATE vehicles 
SET model = 'Honda City ZX i-VTEC (Facelift)', manufacture_year = 2021 
WHERE registration_number = 'KA-01-AB-1234';

-- DELETE
-- DELETE FROM vehicles WHERE vehicle_id = 15;


-- -------------------------------------------------------------
-- 3. APPOINTMENT CRUD OPERATIONS
-- -------------------------------------------------------------
-- CREATE (with conflict checking: ensure no matching vehicle_id at same date/time)
INSERT INTO appointments (customer_id, vehicle_id, appointment_date, appointment_time, service_type, status, notes)
VALUES (1, 1, '2026-12-01', '10:00:00', 'Winter Inspection', 'Scheduled', 'Check coolant and battery');

-- READ
SELECT * FROM appointments WHERE appointment_date = '2026-11-01';
SELECT * FROM appointments WHERE status = 'Scheduled';

-- UPDATE
UPDATE appointments 
SET appointment_date = '2026-12-02', appointment_time = '11:00:00', status = 'In Progress' 
WHERE appointment_id = 13;

-- DELETE
-- DELETE FROM appointments WHERE appointment_id = 15;


-- -------------------------------------------------------------
-- 4. MAINTENANCE RECORD & PARTS CRUD OPERATIONS
-- -------------------------------------------------------------
-- CREATE Record
INSERT INTO maintenance_records (appointment_id, vehicle_id, service_date, work_performed, labour_charges, status)
VALUES (13, 15, '2026-11-20', 'Leaf spring bushing replacement & wheel alignment', 1500.00, 'Pending');

-- CREATE Part Usage
INSERT INTO maintenance_parts (record_id, part_id, quantity, unit_price, subtotal)
VALUES (LAST_INSERT_ID(), 10, 1, 950.00, 950.00);

-- READ
SELECT * FROM maintenance_records WHERE vehicle_id = 1;

-- UPDATE
UPDATE maintenance_records 
SET labour_charges = 1600.00, status = 'Completed' 
WHERE record_id = 12;

-- DELETE
-- DELETE FROM maintenance_records WHERE record_id = 12;


-- -------------------------------------------------------------
-- 5. BILLING CRUD OPERATIONS
-- -------------------------------------------------------------
-- CREATE Bill (Formula: total_amount = labour_charges + spare_parts_cost)
INSERT INTO bills (record_id, labour_charges, spare_parts_cost, total_amount, payment_status, payment_method, billing_date)
VALUES (12, 1100.00, 1800.00, 2900.00, 'Unpaid', 'Cash', '2026-11-19');

-- READ
SELECT * FROM bills WHERE bill_id = 1;

-- UPDATE Payment Status
UPDATE bills 
SET payment_status = 'Paid', payment_method = 'UPI' 
WHERE bill_id = 11;

-- DELETE
-- DELETE FROM bills WHERE bill_id = 11;
