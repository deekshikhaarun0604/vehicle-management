-- =====================================================================
-- Vehicle Service Management and Maintenance Analysis System
-- Sample Data Insertion Script
-- File: 03_insert_sample_data.sql
-- =====================================================================

USE vehicle_service_db;

-- 1. Insert Customers
INSERT INTO customers (customer_id, name, phone, email, address) VALUES
(1, 'Rahul Sharma', '+91 98765 43210', 'rahul.sharma@example.com', '42 MG Road, Bengaluru, Karnataka'),
(2, 'Priya Patel', '+91 98230 11223', 'priya.patel@example.com', '15 SG Highway, Ahmedabad, Gujarat'),
(3, 'Amitabh Verma', '+91 97110 99887', 'amitabh.verma@example.com', '88 Park Street, Kolkata, West Bengal'),
(4, 'Sneha Iyer', '+91 94440 55667', 'sneha.iyer@example.com', '23 Anna Salai, Chennai, Tamil Nadu'),
(5, 'Vikram Malhotra', '+91 98190 22334', 'vikram.m@example.com', '104 Marine Drive, Mumbai, Maharashtra'),
(6, 'Ananya Deshmukh', '+91 98220 77889', 'ananya.d@example.com', '19 FC Road, Pune, Maharashtra'),
(7, 'Rajesh Koothrappali', '+91 98990 44556', 'rajesh.k@example.com', '7 Connaught Place, New Delhi'),
(8, 'Deepika Reddy', '+91 98480 33445', 'deepika.r@example.com', '56 Banjara Hills, Hyderabad, Telangana'),
(9, 'Karan Johar', '+91 98200 66778', 'karan.johar@example.com', '12 Bandra West, Mumbai, Maharashtra'),
(10, 'Neha Kakkar', '+91 98711 22330', 'neha.kakkar@example.com', '34 Sector 17, Chandigarh, Punjab');

-- 2. Insert Vehicles
INSERT INTO vehicles (vehicle_id, customer_id, registration_number, model, vehicle_type, manufacture_year) VALUES
(1, 1, 'KA-01-AB-1234', 'Honda City i-VTEC', 'Sedan', 2020),
(2, 1, 'KA-03-CD-5678', 'Royal Enfield Classic 350', 'Motorcycle', 2021),
(3, 2, 'GJ-01-EF-9012', 'Hyundai Creta SX', 'SUV', 2022),
(4, 3, 'WB-02-GH-3456', 'Maruti Suzuki Swift VXi', 'Hatchback', 2019),
(5, 4, 'TN-09-IJ-7890', 'Toyota Innova Crysta', 'Van', 2021),
(6, 5, 'MH-01-KL-2345', 'Tata Safari Dark Edition', 'SUV', 2023),
(7, 5, 'MH-02-MN-6789', 'BMW 3 Series 330i', 'Sedan', 2022),
(8, 6, 'MH-12-OP-0123', 'Volkswagen Polo GT TSI', 'Hatchback', 2018),
(9, 7, 'DL-3C-QR-4567', 'Mahindra Scorpio-N', 'SUV', 2023),
(10, 8, 'TS-07-ST-8901', 'Kia Seltos HTX', 'SUV', 2021),
(11, 8, 'TS-08-UV-2345', 'KTM Duke 390', 'Motorcycle', 2022),
(12, 9, 'MH-04-WX-6789', 'Mercedes-Benz GLC 300', 'SUV', 2023),
(13, 10, 'CH-01-YZ-0123', 'Skoda Slavia 1.5 TSI', 'Sedan', 2022),
(14, 2, 'GJ-27-AA-4321', 'Tata Ace Gold', 'Truck', 2020),
(15, 3, 'WB-06-BB-8765', 'Mahindra Bolero Maxi Truck', 'Truck', 2021);

-- 3. Insert Spare Parts
INSERT INTO spare_parts (part_id, part_number, part_name, unit_price, stock_quantity) VALUES
(1, 'SP-ENG-001', 'Synthetic Engine Oil (4L)', 2400.00, 45),
(2, 'SP-FLT-002', 'Premium Oil Filter', 450.00, 80),
(3, 'SP-AIR-003', 'Engine Air Filter', 650.00, 60),
(4, 'SP-BRK-004', 'Front Ceramic Brake Pads (Set)', 1800.00, 35),
(5, 'SP-BRK-005', 'Rear Brake Disc Rotor', 2200.00, 20),
(6, 'SP-SPK-006', 'Iridium Spark Plugs (Pack of 4)', 1400.00, 50),
(7, 'SP-BAT-007', '12V 45Ah Maintenance-Free Battery', 4500.00, 15),
(8, 'SP-CLN-008', 'Premixed Radiator Coolant (2L)', 550.00, 75),
(9, 'SP-WPR-009', 'Aerodynamic Wiper Blade Pair', 850.00, 40),
(10, 'SP-BLT-010', 'Serpentine Alternator Belt', 950.00, 25),
(11, 'SP-LGT-011', 'LED Headlight Bulb Kit (H4)', 1600.00, 30),
(12, 'SP-TRN-012', 'Automatic Transmission Fluid (1L)', 1100.00, 40);

-- 4. Insert Service Appointments
INSERT INTO appointments (appointment_id, customer_id, vehicle_id, appointment_date, appointment_time, service_type, status, notes) VALUES
(1, 1, 1, '2026-11-01', '09:30:00', 'Periodic Maintenance Service', 'Completed', '40,000 km general servicing'),
(2, 2, 3, '2026-11-02', '10:00:00', 'Brake System Overhaul', 'Completed', 'Vibration during hard braking'),
(3, 3, 4, '2026-11-03', '11:15:00', 'Engine Diagnostics & Tune-up', 'Completed', 'Engine check light illuminated'),
(4, 4, 5, '2026-11-04', '14:00:00', 'Periodic Maintenance Service', 'Completed', 'Fleet passenger vehicle routine check'),
(5, 5, 6, '2026-11-06', '10:30:00', 'Suspension & Steering Check', 'Completed', 'Clunking noise over potholes'),
(6, 6, 8, '2026-11-08', '09:00:00', 'Electrical & Battery Inspection', 'Completed', 'Vehicle struggles to crank in morning'),
(7, 7, 9, '2026-11-10', '15:00:00', 'Periodic Maintenance Service', 'Completed', '20,000 km regular service'),
(8, 8, 10, '2026-11-12', '11:00:00', 'Air Conditioning & Coolant Service', 'Completed', 'Inadequate cooling in hot weather'),
(9, 1, 2, '2026-11-15', '16:00:00', 'General Motorcycle Service', 'Completed', 'Chain slack adjustment & oil renewal'),
(10, 9, 12, '2026-11-16', '10:00:00', 'Transmission & Brake Overhaul', 'Completed', 'Luxury SUV 30,000 km major service'),
(11, 10, 13, '2026-11-18', '11:30:00', 'Oil & Filter Change', 'Completed', 'Quick lube service requested'),
(12, 2, 14, '2026-11-19', '09:00:00', 'Commercial Vehicle Inspection', 'In Progress', 'Brake pads replacement and full chassis lube'),
(13, 3, 15, '2026-11-20', '10:00:00', 'Suspension Overhaul', 'Scheduled', 'Rear leaf spring inspection'),
(14, 5, 7, '2026-11-21', '14:30:00', 'High Performance Tune-up', 'Scheduled', 'Track-day prep and fluid flush'),
(15, 8, 11, '2026-11-22', '16:30:00', 'Sprocket & Chain Replacement', 'Scheduled', 'Chain kit replacement requested');

-- 5. Insert Maintenance Records
INSERT INTO maintenance_records (record_id, appointment_id, vehicle_id, service_date, work_performed, labour_charges, status) VALUES
(1, 1, 1, '2026-11-01', 'Replaced engine oil, oil filter, air filter, and cleaned brake calipers', 1500.00, 'Completed'),
(2, 2, 3, '2026-11-02', 'Replaced front ceramic brake pads and resurfaced brake rotors; bleed brake fluid', 1200.00, 'Completed'),
(3, 3, 4, '2026-11-03', 'Scanned ECU diagnostics, replaced 4 spark plugs, cleaned throttle body', 1800.00, 'Completed'),
(4, 4, 5, '2026-11-04', 'Replaced engine oil, oil filter, topped up coolant, replaced wiper blades', 1600.00, 'Completed'),
(5, 5, 6, '2026-11-06', 'Replaced front stabilizer link rod, rear disc rotor, and greased suspension', 2200.00, 'Completed'),
(6, 6, 8, '2026-11-08', 'Replaced 12V 45Ah battery, tested alternator charging output, cleaned terminals', 600.00, 'Completed'),
(7, 7, 9, '2026-11-10', 'Periodic service: oil change, filter replacements, fluid checks and 40-point inspection', 1400.00, 'Completed'),
(8, 8, 10, '2026-11-12', 'Evacuated AC gas, replaced AC filter, flushed radiator and refilled 2L coolant', 1700.00, 'Completed'),
(9, 9, 2, '2026-11-15', 'Motorcycle service: engine oil, spark plug, chain lube and tensioning, drum brake shoe check', 800.00, 'Completed'),
(10, 10, 12, '2026-11-16', 'Replaced automatic transmission fluid, replaced front & rear brake pads, cabin filter', 3500.00, 'Completed'),
(11, 11, 13, '2026-11-18', 'Standard oil change with synthetic oil and OEM filter; tyre rotation performed', 900.00, 'Completed'),
(12, 12, 14, '2026-11-19', 'Commercial truck maintenance: brake pad renewal and chassis lubrication', 1100.00, 'In Progress');

-- 6. Insert Maintenance Parts Used
INSERT INTO maintenance_parts (record_id, part_id, quantity, unit_price, subtotal) VALUES
-- Record 1 (Vehicle 1 - Honda City)
(1, 1, 1, 2400.00, 2400.00), -- Synthetic Engine Oil
(1, 2, 1, 450.00, 450.00),   -- Oil Filter
(1, 3, 1, 650.00, 650.00),   -- Air Filter
-- Record 2 (Vehicle 3 - Creta)
(2, 4, 1, 1800.00, 1800.00), -- Brake Pads
-- Record 3 (Vehicle 4 - Swift)
(3, 6, 1, 1400.00, 1400.00), -- Spark Plugs
-- Record 4 (Vehicle 5 - Innova Crysta)
(4, 1, 1, 2400.00, 2400.00), -- Engine Oil
(4, 2, 1, 450.00, 450.00),   -- Oil Filter
(4, 8, 1, 550.00, 550.00),   -- Coolant
(4, 9, 1, 850.00, 850.00),   -- Wiper Blades
-- Record 5 (Vehicle 6 - Tata Safari)
(5, 5, 1, 2200.00, 2200.00), -- Rear Brake Disc Rotor
-- Record 6 (Vehicle 8 - VW Polo)
(6, 7, 1, 4500.00, 4500.00), -- Battery
-- Record 7 (Vehicle 9 - Scorpio-N)
(7, 1, 1, 2400.00, 2400.00), -- Engine Oil
(7, 2, 1, 450.00, 450.00),   -- Oil Filter
(7, 3, 1, 650.00, 650.00),   -- Air Filter
-- Record 8 (Vehicle 10 - Kia Seltos)
(8, 8, 1, 550.00, 550.00),   -- Coolant
(8, 9, 1, 850.00, 850.00),   -- Wiper Blades
-- Record 9 (Vehicle 2 - Royal Enfield)
(9, 1, 1, 2400.00, 2400.00), -- Engine Oil
(9, 6, 1, 1400.00, 1400.00), -- Spark Plugs
-- Record 10 (Vehicle 12 - Mercedes GLC)
(10, 4, 2, 1800.00, 3600.00), -- Front & Rear Brake Pads
(10, 12, 3, 1100.00, 3300.00), -- ATF Fluid 3 Litres
-- Record 11 (Vehicle 13 - Skoda Slavia)
(11, 1, 1, 2400.00, 2400.00), -- Engine Oil
(11, 2, 1, 450.00, 450.00),   -- Oil Filter
-- Record 12 (Vehicle 14 - Tata Ace)
(12, 4, 1, 1800.00, 1800.00); -- Brake Pads

-- 7. Insert Bills
-- Calculation: Total Bill = Labour Charges + Spare Parts Costs
INSERT INTO bills (bill_id, record_id, labour_charges, spare_parts_cost, total_amount, payment_status, payment_method, billing_date) VALUES
(1, 1, 1500.00, 3500.00, 5000.00, 'Paid', 'Credit Card', '2026-11-01'),
(2, 2, 1200.00, 1800.00, 3000.00, 'Paid', 'UPI', '2026-11-02'),
(3, 3, 1800.00, 1400.00, 3200.00, 'Paid', 'Debit Card', '2026-11-03'),
(4, 4, 1600.00, 4250.00, 5850.00, 'Paid', 'Net Banking', '2026-11-04'),
(5, 5, 2200.00, 2200.00, 4400.00, 'Paid', 'UPI', '2026-11-06'),
(6, 6, 600.00, 4500.00, 5100.00, 'Paid', 'Credit Card', '2026-11-08'),
(7, 7, 1400.00, 3500.00, 4900.00, 'Paid', 'Cash', '2026-11-10'),
(8, 8, 1700.00, 1400.00, 3100.00, 'Paid', 'UPI', '2026-11-12'),
(9, 9, 800.00, 3800.00, 4600.00, 'Paid', 'Cash', '2026-11-15'),
(10, 10, 3500.00, 6900.00, 10400.00, 'Paid', 'Credit Card', '2026-11-16'),
(11, 11, 900.00, 2850.00, 3750.00, 'Unpaid', 'Cash', '2026-11-18');
