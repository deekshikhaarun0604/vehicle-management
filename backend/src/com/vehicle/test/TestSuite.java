package com.vehicle.test;

import com.vehicle.model.*;
import com.vehicle.service.*;
import java.util.List;
import java.util.Map;

/**
 * Automated Test Suite for Vehicle Service Management System.
 * Tests Customer, Vehicle, Conflict Checking, Maintenance, Billing calculation, and Reports.
 */
public class TestSuite {
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    private static final CustomerService customerService = new CustomerService();
    private static final VehicleService vehicleService = new VehicleService();
    private static final AppointmentService appointmentService = new AppointmentService();
    private static final MaintenanceService maintenanceService = new MaintenanceService();
    private static final BillingService billingService = new BillingService();
    private static final ReportService reportService = new ReportService();

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("  VEHICLE SERVICE MANAGEMENT SYSTEM - AUTOMATED VERIFICATION SUITE       ");
        System.out.println("==========================================================================");
        System.out.printf("%-8s | %-45s | %-8s%n", "TEST ID", "TEST SCENARIO", "STATUS");
        System.out.println("--------------------------------------------------------------------------");

        // Module 1: Customer Tests
        testCustomerOperations();

        // Module 2: Vehicle Tests
        testVehicleOperations();

        // Module 3: Appointment & Conflict Detection Tests
        testAppointmentScheduling();

        // Module 4: Maintenance Records Tests
        testMaintenanceOperations();

        // Module 5: Billing Calculation Tests (Total = Labour + Parts)
        testBillingCalculations();

        // Module 6: Report Generation Tests
        testReportGeneration();

        System.out.println("==========================================================================");
        System.out.printf(" RESULTS: Total: %d | Passed: %d | Failed: %d | Success Rate: %.1f%%%n", 
            totalTests, passedTests, failedTests, (passedTests * 100.0 / totalTests));
        System.out.println("==========================================================================");
    }

    private static void recordResult(String testId, String scenario, boolean passed, String detail) {
        totalTests++;
        if (passed) {
            passedTests++;
            System.out.printf("%-8s | %-45s | [PASS]%n", testId, scenario);
        } else {
            failedTests++;
            System.out.printf("%-8s | %-45s | [FAIL] (%s)%n", testId, scenario, detail);
        }
    }

    private static void testCustomerOperations() {
        // TC-01: List initial customers
        List<Customer> list = customerService.getAllCustomers();
        recordResult("TC-01", "Retrieve all customer records", list != null && !list.isEmpty(), "Count: " + (list != null ? list.size() : 0));

        // TC-02: Add new customer
        Customer c = new Customer(0, "Test User", "+91 99999 88888", "test.user@auto.com", "100 Test St, City");
        boolean added = customerService.addCustomer(c);
        recordResult("TC-02", "Add new valid customer", added && c.getCustomerId() > 0, "ID: " + c.getCustomerId());

        // TC-03: Retrieve customer by ID
        Customer found = customerService.getCustomerById(c.getCustomerId());
        recordResult("TC-03", "Fetch customer by ID", found != null && "Test User".equals(found.getName()), "Name mismatch");

        // TC-04: Validate customer name requirement
        boolean failedName = false;
        try {
            customerService.addCustomer(new Customer(0, "", "+91 90000 00000", "a@b.com", "Addr"));
        } catch (IllegalArgumentException e) {
            failedName = true;
        }
        recordResult("TC-04", "Enforce non-empty customer name", failedName, "Validation bypassed");
    }

    private static void testVehicleOperations() {
        // TC-05: List vehicles
        List<Vehicle> list = vehicleService.getAllVehicles();
        recordResult("TC-05", "Retrieve all vehicle records", list != null && !list.isEmpty(), "Count: " + (list != null ? list.size() : 0));

        // TC-06: Add new vehicle
        Vehicle v = new Vehicle(0, 1, "KA-99-ZZ-0001", "Test Sedan GT", "Sedan", 2022);
        boolean added = vehicleService.addVehicle(v);
        recordResult("TC-06", "Register new vehicle linked to customer", added && v.getVehicleId() > 0, "ID: " + v.getVehicleId());

        // TC-07: Validate manufacture year constraint
        boolean failedYear = false;
        try {
            vehicleService.addVehicle(new Vehicle(0, 1, "KA-99-ZZ-0002", "Old Car", "Sedan", 1980));
        } catch (IllegalArgumentException e) {
            failedYear = true;
        }
        recordResult("TC-07", "Enforce manufacture year boundary", failedYear, "Year boundary bypassed");
    }

    private static void testAppointmentScheduling() {
        // TC-08: List appointments
        List<Appointment> list = appointmentService.getAllAppointments();
        recordResult("TC-08", "Retrieve all appointment records", list != null && !list.isEmpty(), "Count: " + (list != null ? list.size() : 0));

        // TC-09: Schedule new appointment
        Appointment a = new Appointment(0, 1, 1, "2026-12-15", "10:00:00", "Regular Checkup", "Scheduled", "First slot");
        boolean ok = appointmentService.scheduleAppointment(a);
        recordResult("TC-09", "Schedule non-conflicting appointment", ok, "Booking failed");

        // TC-10: Conflict Detection - Double booking prevention
        boolean conflictCaught = false;
        try {
            Appointment duplicate = new Appointment(0, 1, 1, "2026-12-15", "10:00:00", "Duplicate Slot", "Scheduled", "Should fail");
            appointmentService.scheduleAppointment(duplicate);
        } catch (IllegalStateException e) {
            conflictCaught = true;
        }
        recordResult("TC-10", "Prevent appointment scheduling conflict", conflictCaught, "Conflict not caught");
    }

    private static void testMaintenanceOperations() {
        // TC-11: List maintenance records
        List<MaintenanceRecord> list = maintenanceService.getAllRecords();
        recordResult("TC-11", "Retrieve all maintenance records", list != null && !list.isEmpty(), "Count: " + (list != null ? list.size() : 0));

        // TC-12: Create maintenance record with parts used
        MaintenanceRecord mr = new MaintenanceRecord(0, 1, 1, "2026-12-15", "Test Brake Service", 1000.00, "Completed");
        mr.addPartUsed(new MaintenancePart(0, 4, 1, 1800.00)); // Brake pads (1800)
        int recId = maintenanceService.createMaintenanceRecord(mr);
        recordResult("TC-12", "Create maintenance job with spare parts", recId > 0, "Creation failed");
    }

    private static void testBillingCalculations() {
        // TC-13: List bills
        List<Bill> list = billingService.getAllBills();
        recordResult("TC-13", "Retrieve all billing records", list != null && !list.isEmpty(), "Count: " + (list != null ? list.size() : 0));

        // TC-14: Enforce calculation formula: Total Bill = Labour Charges + Spare Parts Costs
        Bill bill1 = billingService.getBillById(1);
        boolean formulaMatches = false;
        if (bill1 != null) {
            double expected = bill1.getLabourCharges() + bill1.getSparePartsCost();
            formulaMatches = Math.abs(expected - bill1.getTotalAmount()) < 0.01;
        }
        recordResult("TC-14", "Enforce Total = Labour + Parts formula", formulaMatches, "Formula mismatch");

        // TC-15: Auto-generate bill for maintenance record
        Bill generated = billingService.generateBillForRecord(1, "UPI", "Paid");
        recordResult("TC-15", "Generate bill from maintenance job", generated != null, "Generation failed");

        // TC-16: Update bill payment status
        boolean updated = billingService.updatePaymentStatus(1, "Paid", "Credit Card");
        recordResult("TC-16", "Update payment status & method", updated, "Update failed");
    }

    private static void testReportGeneration() {
        // TC-17: Service history report
        List<Map<String, Object>> history = reportService.getServiceHistory(null);
        recordResult("TC-17", "Generate service history report", history != null && !history.isEmpty(), "Empty history");

        // TC-18: Vehicle-wise maintenance costs report
        List<Map<String, Object>> vehicleCosts = reportService.getVehicleWiseMaintenanceCosts();
        recordResult("TC-18", "Generate vehicle-wise maintenance costs", vehicleCosts != null && !vehicleCosts.isEmpty(), "Empty costs");

        // TC-19: Service center workload report
        Map<String, Object> workload = reportService.getServiceCenterWorkload();
        recordResult("TC-19", "Generate service center workload summary", workload != null && workload.containsKey("statusDistribution"), "Missing workload keys");

        // TC-20: Dashboard KPIs computation
        Map<String, Object> stats = reportService.getDashboardStats();
        boolean validStats = stats != null && stats.containsKey("totalCustomers") && stats.containsKey("totalRevenue");
        recordResult("TC-20", "Compute real-time dashboard KPIs", validStats, "Missing KPI metrics");
    }
}
