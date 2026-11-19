package com.vehicle.dao;

import com.vehicle.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe In-Memory repository pre-seeded with sample database data.
 * Used when MySQL server is offline or in local testing environments.
 */
public class DataStore {
    public static final Map<Integer, Customer> customers = new ConcurrentHashMap<>();
    public static final Map<Integer, Vehicle> vehicles = new ConcurrentHashMap<>();
    public static final Map<Integer, Appointment> appointments = new ConcurrentHashMap<>();
    public static final Map<Integer, SparePart> spareParts = new ConcurrentHashMap<>();
    public static final Map<Integer, MaintenanceRecord> maintenanceRecords = new ConcurrentHashMap<>();
    public static final Map<Integer, List<MaintenancePart>> maintenanceParts = new ConcurrentHashMap<>();
    public static final Map<Integer, Bill> bills = new ConcurrentHashMap<>();

    private static final AtomicInteger customerSeq = new AtomicInteger(10);
    private static final AtomicInteger vehicleSeq = new AtomicInteger(15);
    private static final AtomicInteger appointmentSeq = new AtomicInteger(15);
    private static final AtomicInteger sparePartSeq = new AtomicInteger(12);
    private static final AtomicInteger maintenanceSeq = new AtomicInteger(12);
    private static final AtomicInteger billSeq = new AtomicInteger(11);
    private static final AtomicInteger maintPartSeq = new AtomicInteger(25);

    static {
        seedInitialData();
    }

    public static synchronized void seedInitialData() {
        customers.clear();
        vehicles.clear();
        appointments.clear();
        spareParts.clear();
        maintenanceRecords.clear();
        maintenanceParts.clear();
        bills.clear();

        // 1. Seed Customers
        addCust(1, "Rahul Sharma", "+91 98765 43210", "rahul.sharma@example.com", "42 MG Road, Bengaluru, Karnataka");
        addCust(2, "Priya Patel", "+91 98230 11223", "priya.patel@example.com", "15 SG Highway, Ahmedabad, Gujarat");
        addCust(3, "Amitabh Verma", "+91 97110 99887", "amitabh.verma@example.com", "88 Park Street, Kolkata, West Bengal");
        addCust(4, "Sneha Iyer", "+91 94440 55667", "sneha.iyer@example.com", "23 Anna Salai, Chennai, Tamil Nadu");
        addCust(5, "Vikram Malhotra", "+91 98190 22334", "vikram.m@example.com", "104 Marine Drive, Mumbai, Maharashtra");
        addCust(6, "Ananya Deshmukh", "+91 98220 77889", "ananya.d@example.com", "19 FC Road, Pune, Maharashtra");
        addCust(7, "Rajesh Koothrappali", "+91 98990 44556", "rajesh.k@example.com", "7 Connaught Place, New Delhi");
        addCust(8, "Deepika Reddy", "+91 98480 33445", "deepika.r@example.com", "56 Banjara Hills, Hyderabad, Telangana");
        addCust(9, "Karan Johar", "+91 98200 66778", "karan.johar@example.com", "12 Bandra West, Mumbai, Maharashtra");
        addCust(10, "Neha Kakkar", "+91 98711 22330", "neha.kakkar@example.com", "34 Sector 17, Chandigarh, Punjab");

        // 2. Seed Vehicles
        addVeh(1, 1, "KA-01-AB-1234", "Honda City i-VTEC", "Sedan", 2020);
        addVeh(2, 1, "KA-03-CD-5678", "Royal Enfield Classic 350", "Motorcycle", 2021);
        addVeh(3, 2, "GJ-01-EF-9012", "Hyundai Creta SX", "SUV", 2022);
        addVeh(4, 3, "WB-02-GH-3456", "Maruti Suzuki Swift VXi", "Hatchback", 2019);
        addVeh(5, 4, "TN-09-IJ-7890", "Toyota Innova Crysta", "Van", 2021);
        addVeh(6, 5, "MH-01-KL-2345", "Tata Safari Dark Edition", "SUV", 2023);
        addVeh(7, 5, "MH-02-MN-6789", "BMW 3 Series 330i", "Sedan", 2022);
        addVeh(8, 6, "MH-12-OP-0123", "Volkswagen Polo GT TSI", "Hatchback", 2018);
        addVeh(9, 7, "DL-3C-QR-4567", "Mahindra Scorpio-N", "SUV", 2023);
        addVeh(10, 8, "TS-07-ST-8901", "Kia Seltos HTX", "SUV", 2021);
        addVeh(11, 8, "TS-08-UV-2345", "KTM Duke 390", "Motorcycle", 2022);
        addVeh(12, 9, "MH-04-WX-6789", "Mercedes-Benz GLC 300", "SUV", 2023);
        addVeh(13, 10, "CH-01-YZ-0123", "Skoda Slavia 1.5 TSI", "Sedan", 2022);
        addVeh(14, 2, "GJ-27-AA-4321", "Tata Ace Gold", "Truck", 2020);
        addVeh(15, 3, "WB-06-BB-8765", "Mahindra Bolero Maxi Truck", "Truck", 2021);

        // 3. Seed Spare Parts
        addPart(1, "SP-ENG-001", "Synthetic Engine Oil (4L)", 2400.00, 45);
        addPart(2, "SP-FLT-002", "Premium Oil Filter", 450.00, 80);
        addPart(3, "SP-AIR-003", "Engine Air Filter", 650.00, 60);
        addPart(4, "SP-BRK-004", "Front Ceramic Brake Pads (Set)", 1800.00, 35);
        addPart(5, "SP-BRK-005", "Rear Brake Disc Rotor", 2200.00, 20);
        addPart(6, "SP-SPK-006", "Iridium Spark Plugs (Pack of 4)", 1400.00, 50);
        addPart(7, "SP-BAT-007", "12V 45Ah Maintenance-Free Battery", 4500.00, 15);
        addPart(8, "SP-CLN-008", "Premixed Radiator Coolant (2L)", 550.00, 75);
        addPart(9, "SP-WPR-009", "Aerodynamic Wiper Blade Pair", 850.00, 40);
        addPart(10, "SP-BLT-010", "Serpentine Alternator Belt", 950.00, 25);
        addPart(11, "SP-LGT-011", "LED Headlight Bulb Kit (H4)", 1600.00, 30);
        addPart(12, "SP-TRN-012", "Automatic Transmission Fluid (1L)", 1100.00, 40);

        // 4. Seed Appointments
        addAppt(1, 1, 1, "2026-11-01", "09:30:00", "Periodic Maintenance Service", "Completed", "40,000 km general servicing");
        addAppt(2, 2, 3, "2026-11-02", "10:00:00", "Brake System Overhaul", "Completed", "Vibration during hard braking");
        addAppt(3, 3, 4, "2026-11-03", "11:15:00", "Engine Diagnostics & Tune-up", "Completed", "Engine check light illuminated");
        addAppt(4, 4, 5, "2026-11-04", "14:00:00", "Periodic Maintenance Service", "Completed", "Fleet passenger vehicle routine check");
        addAppt(5, 5, 6, "2026-11-06", "10:30:00", "Suspension & Steering Check", "Completed", "Clunking noise over potholes");
        addAppt(6, 6, 8, "2026-11-08", "09:00:00", "Electrical & Battery Inspection", "Completed", "Vehicle struggles to crank in morning");
        addAppt(7, 7, 9, "2026-11-10", "15:00:00", "Periodic Maintenance Service", "Completed", "20,000 km regular service");
        addAppt(8, 8, 10, "2026-11-12", "11:00:00", "Air Conditioning & Coolant Service", "Completed", "Inadequate cooling in hot weather");
        addAppt(9, 1, 2, "2026-11-15", "16:00:00", "General Motorcycle Service", "Completed", "Chain slack adjustment & oil renewal");
        addAppt(10, 9, 12, "2026-11-16", "10:00:00", "Transmission & Brake Overhaul", "Completed", "Luxury SUV 30,000 km major service");
        addAppt(11, 10, 13, "2026-11-18", "11:30:00", "Oil & Filter Change", "Completed", "Quick lube service requested");
        addAppt(12, 2, 14, "2026-11-19", "09:00:00", "Commercial Vehicle Inspection", "In Progress", "Brake pads replacement and full chassis lube");
        addAppt(13, 3, 15, "2026-11-20", "10:00:00", "Suspension Overhaul", "Scheduled", "Rear leaf spring inspection");
        addAppt(14, 5, 7, "2026-11-21", "14:30:00", "High Performance Tune-up", "Scheduled", "Track-day prep and fluid flush");
        addAppt(15, 8, 11, "2026-11-22", "16:30:00", "Sprocket & Chain Replacement", "Scheduled", "Chain kit replacement requested");

        // 5. Seed Maintenance Records & Parts
        addMaint(1, 1, 1, "2026-11-01", "Replaced engine oil, oil filter, air filter, and cleaned brake calipers", 1500.00, "Completed");
        addMaintPart(1, 1, 1, 2400.00);
        addMaintPart(1, 2, 1, 450.00);
        addMaintPart(1, 3, 1, 650.00);

        addMaint(2, 2, 3, "2026-11-02", "Replaced front ceramic brake pads and resurfaced brake rotors; bleed brake fluid", 1200.00, "Completed");
        addMaintPart(2, 4, 1, 1800.00);

        addMaint(3, 3, 4, "2026-11-03", "Scanned ECU diagnostics, replaced 4 spark plugs, cleaned throttle body", 1800.00, "Completed");
        addMaintPart(3, 6, 1, 1400.00);

        addMaint(4, 4, 5, "2026-11-04", "Replaced engine oil, oil filter, topped up coolant, replaced wiper blades", 1600.00, "Completed");
        addMaintPart(4, 1, 1, 2400.00);
        addMaintPart(4, 2, 1, 450.00);
        addMaintPart(4, 8, 1, 550.00);
        addMaintPart(4, 9, 1, 850.00);

        addMaint(5, 5, 6, "2026-11-06", "Replaced front stabilizer link rod, rear disc rotor, and greased suspension", 2200.00, "Completed");
        addMaintPart(5, 5, 1, 2200.00);

        addMaint(6, 6, 8, "2026-11-08", "Replaced 12V 45Ah battery, tested alternator charging output, cleaned terminals", 600.00, "Completed");
        addMaintPart(6, 7, 1, 4500.00);

        addMaint(7, 7, 9, "2026-11-10", "Periodic service: oil change, filter replacements, fluid checks and 40-point inspection", 1400.00, "Completed");
        addMaintPart(7, 1, 1, 2400.00);
        addMaintPart(7, 2, 1, 450.00);
        addMaintPart(7, 3, 1, 650.00);

        addMaint(8, 8, 10, "2026-11-12", "Evacuated AC gas, replaced AC filter, flushed radiator and refilled 2L coolant", 1700.00, "Completed");
        addMaintPart(8, 8, 1, 550.00);
        addMaintPart(8, 9, 1, 850.00);

        addMaint(9, 9, 2, "2026-11-15", "Motorcycle service: engine oil, spark plug, chain lube and tensioning, drum brake shoe check", 800.00, "Completed");
        addMaintPart(9, 1, 1, 2400.00);
        addMaintPart(9, 6, 1, 1400.00);

        addMaint(10, 10, 12, "2026-11-16", "Replaced automatic transmission fluid, replaced front & rear brake pads, cabin filter", 3500.00, "Completed");
        addMaintPart(10, 4, 2, 1800.00);
        addMaintPart(10, 12, 3, 1100.00);

        addMaint(11, 11, 13, "2026-11-18", "Standard oil change with synthetic oil and OEM filter; tyre rotation performed", 900.00, "Completed");
        addMaintPart(11, 1, 1, 2400.00);
        addMaintPart(11, 2, 1, 450.00);

        addMaint(12, 12, 14, "2026-11-19", "Commercial truck maintenance: brake pad renewal and chassis lubrication", 1100.00, "In Progress");
        addMaintPart(12, 4, 1, 1800.00);

        // 6. Seed Bills (Total = Labour + Parts)
        addBill(1, 1, 1500.00, 3500.00, "Paid", "Credit Card", "2026-11-01");
        addBill(2, 2, 1200.00, 1800.00, "Paid", "UPI", "2026-11-02");
        addBill(3, 3, 1800.00, 1400.00, "Paid", "Debit Card", "2026-11-03");
        addBill(4, 4, 1600.00, 4250.00, "Paid", "Net Banking", "2026-11-04");
        addBill(5, 5, 2200.00, 2200.00, "Paid", "UPI", "2026-11-06");
        addBill(6, 6, 600.00, 4500.00, "Paid", "Credit Card", "2026-11-08");
        addBill(7, 7, 1400.00, 3500.00, "Paid", "Cash", "2026-11-10");
        addBill(8, 8, 1700.00, 1400.00, "Paid", "UPI", "2026-11-12");
        addBill(9, 9, 800.00, 3800.00, "Paid", "Cash", "2026-11-15");
        addBill(10, 10, 3500.00, 6900.00, "Paid", "Credit Card", "2026-11-16");
        addBill(11, 11, 900.00, 2850.00, "Unpaid", "Cash", "2026-11-18");
    }

    private static void addCust(int id, String name, String phone, String email, String addr) {
        Customer c = new Customer(id, name, phone, email, addr, "2026-10-15 10:00:00");
        customers.put(id, c);
    }

    private static void addVeh(int id, int custId, String reg, String model, String type, int year) {
        Vehicle v = new Vehicle(id, custId, reg, model, type, year);
        Customer c = customers.get(custId);
        if (c != null) v.setCustomerName(c.getName());
        vehicles.put(id, v);
    }

    private static void addPart(int id, String num, String name, double price, int qty) {
        spareParts.put(id, new SparePart(id, num, name, price, qty));
    }

    private static void addAppt(int id, int custId, int vehId, String date, String time, String type, String status, String notes) {
        Appointment a = new Appointment(id, custId, vehId, date, time, type, status, notes);
        Customer c = customers.get(custId);
        if (c != null) a.setCustomerName(c.getName());
        Vehicle v = vehicles.get(vehId);
        if (v != null) {
            a.setRegistrationNumber(v.getRegistrationNumber());
            a.setVehicleModel(v.getModel());
        }
        appointments.put(id, a);
    }

    private static void addMaint(int id, Integer apptId, int vehId, String date, String work, double labour, String status) {
        MaintenanceRecord m = new MaintenanceRecord(id, apptId, vehId, date, work, labour, status);
        Vehicle v = vehicles.get(vehId);
        if (v != null) {
            m.setRegistrationNumber(v.getRegistrationNumber());
            m.setVehicleModel(v.getModel());
            m.setVehicleType(v.getVehicleType());
            Customer c = customers.get(v.getCustomerId());
            if (c != null) {
                m.setCustomerName(c.getName());
                m.setCustomerPhone(c.getPhone());
            }
        }
        maintenanceRecords.put(id, m);
    }

    private static void addMaintPart(int recordId, int partId, int qty, double unitPrice) {
        int id = maintPartSeq.incrementAndGet();
        MaintenancePart mp = new MaintenancePart(id, recordId, partId, qty, unitPrice, qty * unitPrice);
        SparePart sp = spareParts.get(partId);
        if (sp != null) mp.setPartName(sp.getPartName());
        maintenanceParts.computeIfAbsent(recordId, k -> new ArrayList<>()).add(mp);

        MaintenanceRecord mr = maintenanceRecords.get(recordId);
        if (mr != null) {
            mr.addPartUsed(mp);
            mr.setSparePartsCost(mr.getSparePartsCost() + mp.getSubtotal());
            mr.setTotalBill(mr.getLabourCharges() + mr.getSparePartsCost());
        }
    }

    private static void addBill(int id, int recordId, double labour, double parts, String status, String method, String date) {
        Bill b = new Bill(id, recordId, labour, parts, status, method, date);
        MaintenanceRecord mr = maintenanceRecords.get(recordId);
        if (mr != null) {
            mr.setPaymentStatus(status);
            b.setRegistrationNumber(mr.getRegistrationNumber());
            b.setVehicleModel(mr.getVehicleModel());
            b.setCustomerName(mr.getCustomerName());
            b.setCustomerPhone(mr.getCustomerPhone());
            b.setWorkPerformed(mr.getWorkPerformed());
            b.setItemizedParts(maintenanceParts.getOrDefault(recordId, new ArrayList<>()));
        }
        bills.put(id, b);
    }

    public static int nextCustomerId() { return customerSeq.incrementAndGet(); }
    public static int nextVehicleId() { return vehicleSeq.incrementAndGet(); }
    public static int nextAppointmentId() { return appointmentSeq.incrementAndGet(); }
    public static int nextSparePartId() { return sparePartSeq.incrementAndGet(); }
    public static int nextMaintenanceId() { return maintenanceSeq.incrementAndGet(); }
    public static int nextBillId() { return billSeq.incrementAndGet(); }
    public static int nextMaintPartId() { return maintPartSeq.incrementAndGet(); }
}
