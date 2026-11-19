-- =====================================================================
-- Vehicle Service Management and Maintenance Analysis System
-- Aggregate and Analytics Queries
-- File: 07_aggregate_queries.sql
-- =====================================================================

USE vehicle_service_db;

-- 1. Vehicle-Wise Maintenance Costs (Total Expenditure per Vehicle)
SELECT 
    v.vehicle_id,
    v.registration_number,
    v.model,
    v.vehicle_type,
    c.name AS owner_name,
    COUNT(m.record_id) AS total_services,
    SUM(m.labour_charges) AS total_labour_cost,
    SUM(COALESCE(b.spare_parts_cost, 0.00)) AS total_parts_cost,
    SUM(COALESCE(b.total_amount, m.labour_charges)) AS total_maintenance_expenditure,
    AVG(COALESCE(b.total_amount, m.labour_charges)) AS average_cost_per_service
FROM vehicles v
JOIN customers c ON v.customer_id = c.customer_id
LEFT JOIN maintenance_records m ON v.vehicle_id = m.vehicle_id
LEFT JOIN bills b ON m.record_id = b.record_id
GROUP BY v.vehicle_id, v.registration_number, v.model, v.vehicle_type, c.name
ORDER BY total_maintenance_expenditure DESC;

-- 2. Service Center Workload: Appointment Distribution by Status
SELECT 
    status,
    COUNT(*) AS appointment_count,
    ROUND((COUNT(*) * 100.0 / (SELECT COUNT(*) FROM appointments)), 2) AS percentage_of_total
FROM appointments
GROUP BY status
ORDER BY appointment_count DESC;

-- 3. Service Center Workload: Daily / Weekly Appointment Distribution
SELECT 
    appointment_date,
    DAYNAME(appointment_date) AS day_of_week,
    COUNT(*) AS daily_appointments,
    SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) AS completed_count,
    SUM(CASE WHEN status = 'In Progress' THEN 1 ELSE 0 END) AS in_progress_count,
    SUM(CASE WHEN status = 'Scheduled' THEN 1 ELSE 0 END) AS scheduled_count
FROM appointments
GROUP BY appointment_date, DAYNAME(appointment_date)
ORDER BY appointment_date ASC;

-- 4. Financial & Billing Summary: Revenue Breakdown
SELECT 
    COUNT(b.bill_id) AS total_bills_generated,
    SUM(b.labour_charges) AS gross_labour_revenue,
    SUM(b.spare_parts_cost) AS gross_parts_revenue,
    SUM(b.total_amount) AS gross_total_revenue,
    AVG(b.total_amount) AS average_bill_amount,
    SUM(CASE WHEN b.payment_status = 'Paid' THEN b.total_amount ELSE 0.00 END) AS collected_revenue,
    SUM(CASE WHEN b.payment_status = 'Unpaid' THEN b.total_amount ELSE 0.00 END) AS outstanding_dues
FROM bills b;

-- 5. Maintenance Cost Breakdown by Vehicle Type
SELECT 
    v.vehicle_type,
    COUNT(DISTINCT v.vehicle_id) AS vehicle_count,
    COUNT(m.record_id) AS total_services_performed,
    ROUND(AVG(m.labour_charges), 2) AS avg_labour_charges,
    ROUND(AVG(b.spare_parts_cost), 2) AS avg_parts_cost,
    ROUND(AVG(b.total_amount), 2) AS avg_total_bill,
    SUM(b.total_amount) AS total_type_revenue
FROM vehicles v
JOIN maintenance_records m ON v.vehicle_id = m.vehicle_id
JOIN bills b ON m.record_id = b.record_id
GROUP BY v.vehicle_type
ORDER BY total_type_revenue DESC;

-- 6. Most Frequently Used Spare Parts (Top Demand & Part Revenue)
SELECT 
    sp.part_id,
    sp.part_name,
    sp.part_number,
    sp.unit_price,
    SUM(mp.quantity) AS total_units_consumed,
    SUM(mp.subtotal) AS total_part_revenue,
    sp.stock_quantity AS current_stock_remaining
FROM spare_parts sp
JOIN maintenance_parts mp ON sp.part_id = mp.part_id
GROUP BY sp.part_id, sp.part_name, sp.part_number, sp.unit_price, sp.stock_quantity
ORDER BY total_units_consumed DESC, total_part_revenue DESC;
