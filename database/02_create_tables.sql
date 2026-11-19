-- =====================================================================
-- Vehicle Service Management and Maintenance Analysis System
-- Table Creation Script (3NF Normalized)
-- File: 02_create_tables.sql
-- =====================================================================

USE vehicle_service_db;

-- 1. Customers Table
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS maintenance_parts;
DROP TABLE IF EXISTS maintenance_records;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS spare_parts;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Vehicles Table
CREATE TABLE vehicles (
    vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    registration_number VARCHAR(30) NOT NULL UNIQUE,
    model VARCHAR(100) NOT NULL,
    vehicle_type ENUM('Sedan', 'SUV', 'Hatchback', 'Truck', 'Van', 'Motorcycle') NOT NULL,
    manufacture_year INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicle_customer FOREIGN KEY (customer_id) 
        REFERENCES customers(customer_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_vehicle_year CHECK (manufacture_year BETWEEN 1990 AND 2030)
) ENGINE=InnoDB;

-- 3. Service Appointments Table
CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    service_type VARCHAR(100) NOT NULL,
    status ENUM('Scheduled', 'In Progress', 'Completed', 'Cancelled') DEFAULT 'Scheduled',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointment_customer FOREIGN KEY (customer_id) 
        REFERENCES customers(customer_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_appointment_vehicle FOREIGN KEY (vehicle_id) 
        REFERENCES vehicles(vehicle_id) ON DELETE CASCADE ON UPDATE CASCADE,
    -- Prevent scheduling duplicate appointments for the same vehicle at the same date and time
    CONSTRAINT uq_vehicle_slot UNIQUE (vehicle_id, appointment_date, appointment_time)
) ENGINE=InnoDB;

-- 4. Spare Parts Inventory Table
CREATE TABLE spare_parts (
    part_id INT AUTO_INCREMENT PRIMARY KEY,
    part_number VARCHAR(50) NOT NULL UNIQUE,
    part_name VARCHAR(100) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    CONSTRAINT chk_unit_price CHECK (unit_price >= 0.00),
    CONSTRAINT chk_stock CHECK (stock_quantity >= 0)
) ENGINE=InnoDB;

-- 5. Maintenance and Service Records Table
CREATE TABLE maintenance_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NULL,
    vehicle_id INT NOT NULL,
    service_date DATE NOT NULL,
    work_performed TEXT NOT NULL,
    labour_charges DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status ENUM('Pending', 'In Progress', 'Completed') DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_maintenance_appointment FOREIGN KEY (appointment_id) 
        REFERENCES appointments(appointment_id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_maintenance_vehicle FOREIGN KEY (vehicle_id) 
        REFERENCES vehicles(vehicle_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_labour_charges CHECK (labour_charges >= 0.00)
) ENGINE=InnoDB;

-- 6. Maintenance Parts Junction Table (Records which spare parts were used in a maintenance service)
CREATE TABLE maintenance_parts (
    maint_part_id INT AUTO_INCREMENT PRIMARY KEY,
    record_id INT NOT NULL,
    part_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_mp_record FOREIGN KEY (record_id) 
        REFERENCES maintenance_records(record_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_mp_part FOREIGN KEY (part_id) 
        REFERENCES spare_parts(part_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_mp_quantity CHECK (quantity > 0)
) ENGINE=InnoDB;

-- 7. Bills Table
CREATE TABLE bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    record_id INT NOT NULL UNIQUE,
    labour_charges DECIMAL(10, 2) NOT NULL,
    spare_parts_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_status ENUM('Unpaid', 'Paid', 'Partially Paid') DEFAULT 'Unpaid',
    payment_method ENUM('Cash', 'Credit Card', 'Debit Card', 'UPI', 'Net Banking') DEFAULT 'Cash',
    billing_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bill_record FOREIGN KEY (record_id) 
        REFERENCES maintenance_records(record_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_total_amount CHECK (total_amount = labour_charges + spare_parts_cost)
) ENGINE=InnoDB;

-- Indexes for performance on frequently joined/searched columns
CREATE INDEX idx_vehicle_customer ON vehicles(customer_id);
CREATE INDEX idx_appointment_date ON appointments(appointment_date);
CREATE INDEX idx_maintenance_vehicle ON maintenance_records(vehicle_id);
CREATE INDEX idx_maintenance_date ON maintenance_records(service_date);
CREATE INDEX idx_bill_status ON bills(payment_status);
