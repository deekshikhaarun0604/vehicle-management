package com.vehicle;

import com.vehicle.model.*;
import com.vehicle.service.*;
import java.util.*;

/**
 * Interactive Console/CLI application for terminal demonstrations.
 */
public class MainCLI {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CustomerService customerService = new CustomerService();
    private static final VehicleService vehicleService = new VehicleService();
    private static final AppointmentService appointmentService = new AppointmentService();
    private static final MaintenanceService maintenanceService = new MaintenanceService();
    private static final BillingService billingService = new BillingService();
    private static final ReportService reportService = new ReportService();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=======================================================");
            System.out.println("  VEHICLE SERVICE MANAGEMENT & MAINTENANCE ANALYSIS");
            System.out.println("=======================================================");
            System.out.println(" 1. Customer Management");
            System.out.println(" 2. Vehicle Management");
            System.out.println(" 3. Service Appointment Management (Conflict Checked)");
            System.out.println(" 4. Maintenance & Service Records");
            System.out.println(" 5. Billing Management (Total = Labour + Parts)");
            System.out.println(" 6. View Reports (History, Vehicle Costs, Workload)");
            System.out.println(" 0. Exit");
            System.out.println("=======================================================");
            System.out.print(" Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": handleCustomers(); break;
                case "2": handleVehicles(); break;
                case "3": handleAppointments(); break;
                case "4": handleMaintenance(); break;
                case "5": handleBilling(); break;
                case "6": handleReports(); break;
                case "0":
                    System.out.println("Exiting application. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid selection. Please try again.");
            }
        }
    }

    private static void handleCustomers() {
        System.out.println("\n--- CUSTOMER MANAGEMENT ---");
        System.out.println("1. List All Customers");
        System.out.println("2. Add New Customer");
        System.out.println("3. Delete Customer");
        System.out.print("Choice: ");
        String c = scanner.nextLine().trim();

        if ("1".equals(c)) {
            List<Customer> list = customerService.getAllCustomers();
            System.out.printf("%-4s | %-20s | %-16s | %-25s%n", "ID", "Name", "Phone", "Email");
            System.out.println("---------------------------------------------------------------------");
            for (Customer cust : list) {
                System.out.printf("%-4d | %-20s | %-16s | %-25s%n", 
                    cust.getCustomerId(), cust.getName(), cust.getPhone(), cust.getEmail());
            }
        } else if ("2".equals(c)) {
            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Phone: ");
            String phone = scanner.nextLine().trim();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter Address: ");
            String addr = scanner.nextLine().trim();

            Customer cust = new Customer(0, name, phone, email, addr);
            if (customerService.addCustomer(cust)) {
                System.out.println("[SUCCESS] Customer added with ID: " + cust.getCustomerId());
            } else {
                System.out.println("[ERROR] Failed to add customer.");
            }
        } else if ("3".equals(c)) {
            System.out.print("Enter Customer ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            if (customerService.deleteCustomer(id)) {
                System.out.println("[SUCCESS] Customer deleted.");
            } else {
                System.out.println("[ERROR] Customer ID not found.");
            }
        }
    }

    private static void handleVehicles() {
        System.out.println("\n--- VEHICLE MANAGEMENT ---");
        System.out.println("1. List All Vehicles");
        System.out.println("2. Add New Vehicle");
        System.out.print("Choice: ");
        String c = scanner.nextLine().trim();

        if ("1".equals(c)) {
            List<Vehicle> list = vehicleService.getAllVehicles();
            System.out.printf("%-4s | %-15s | %-25s | %-12s | %-18s%n", "ID", "Reg Number", "Model", "Type", "Owner");
            System.out.println("-----------------------------------------------------------------------------------");
            for (Vehicle v : list) {
                System.out.printf("%-4d | %-15s | %-25s | %-12s | %-18s%n", 
                    v.getVehicleId(), v.getRegistrationNumber(), v.getModel(), v.getVehicleType(), v.getCustomerName());
            }
        } else if ("2".equals(c)) {
            System.out.print("Enter Customer ID: ");
            int custId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter Registration Number: ");
            String reg = scanner.nextLine().trim();
            System.out.print("Enter Vehicle Model: ");
            String model = scanner.nextLine().trim();
            System.out.print("Enter Vehicle Type (Sedan/SUV/Hatchback/Truck/Motorcycle): ");
            String type = scanner.nextLine().trim();
            System.out.print("Enter Manufacture Year (1990-2030): ");
            int year = Integer.parseInt(scanner.nextLine().trim());

            Vehicle v = new Vehicle(0, custId, reg, model, type, year);
            if (vehicleService.addVehicle(v)) {
                System.out.println("[SUCCESS] Vehicle registered with ID: " + v.getVehicleId());
            } else {
                System.out.println("[ERROR] Failed to register vehicle.");
            }
        }
    }

    private static void handleAppointments() {
        System.out.println("\n--- APPOINTMENT SCHEDULING ---");
        System.out.println("1. List All Appointments");
        System.out.println("2. Schedule New Appointment (with Conflict Detection)");
        System.out.print("Choice: ");
        String c = scanner.nextLine().trim();

        if ("1".equals(c)) {
            List<Appointment> list = appointmentService.getAllAppointments();
            System.out.printf("%-4s | %-12s | %-10s | %-15s | %-25s | %-12s%n", "ID", "Date", "Time", "Reg No", "Service Type", "Status");
            System.out.println("--------------------------------------------------------------------------------------------");
            for (Appointment a : list) {
                System.out.printf("%-4d | %-12s | %-10s | %-15s | %-25s | %-12s%n", 
                    a.getAppointmentId(), a.getAppointmentDate(), a.getAppointmentTime(), 
                    a.getRegistrationNumber(), a.getServiceType(), a.getStatus());
            }
        } else if ("2".equals(c)) {
            System.out.print("Enter Customer ID: ");
            int custId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter Vehicle ID: ");
            int vehId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
            String date = scanner.nextLine().trim();
            System.out.print("Enter Appointment Time (HH:MM:SS): ");
            String time = scanner.nextLine().trim();
            System.out.print("Enter Service Type: ");
            String type = scanner.nextLine().trim();
            System.out.print("Enter Notes: ");
            String notes = scanner.nextLine().trim();

            Appointment appt = new Appointment(0, custId, vehId, date, time, type, "Scheduled", notes);
            try {
                if (appointmentService.scheduleAppointment(appt)) {
                    System.out.println("[SUCCESS] Appointment scheduled with ID: " + appt.getAppointmentId());
                } else {
                    System.out.println("[ERROR] Failed to schedule appointment.");
                }
            } catch (Exception e) {
                System.out.println("[CONFLICT DETECTED] " + e.getMessage());
            }
        }
    }

    private static void handleMaintenance() {
        System.out.println("\n--- MAINTENANCE & SERVICE RECORDS ---");
        List<MaintenanceRecord> list = maintenanceService.getAllRecords();
        System.out.printf("%-4s | %-12s | %-15s | %-35s | %-10s | %-10s%n", 
            "ID", "Date", "Reg No", "Work Performed", "Labour", "Status");
        System.out.println("--------------------------------------------------------------------------------------------");
        for (MaintenanceRecord m : list) {
            String work = m.getWorkPerformed().length() > 33 ? m.getWorkPerformed().substring(0, 32) + "..." : m.getWorkPerformed();
            System.out.printf("%-4d | %-12s | %-15s | %-35s | ₹%-9.2f | %-10s%n", 
                m.getRecordId(), m.getServiceDate(), m.getRegistrationNumber(), work, m.getLabourCharges(), m.getStatus());
        }
    }

    private static void handleBilling() {
        System.out.println("\n--- BILLING MANAGEMENT ---");
        System.out.println("1. List All Bills");
        System.out.println("2. View Bill Details by ID");
        System.out.print("Choice: ");
        String c = scanner.nextLine().trim();

        if ("1".equals(c)) {
            List<Bill> list = billingService.getAllBills();
            System.out.printf("%-4s | %-12s | %-15s | %-10s | %-10s | %-12s | %-10s%n", 
                "Bill", "Date", "Reg No", "Labour", "Parts", "Total Bill", "Status");
            System.out.println("---------------------------------------------------------------------------------------");
            for (Bill b : list) {
                System.out.printf("%-4d | %-12s | %-15s | ₹%-9.2f | ₹%-9.2f | ₹%-11.2f | %-10s%n", 
                    b.getBillId(), b.getBillingDate(), b.getRegistrationNumber(), 
                    b.getLabourCharges(), b.getSparePartsCost(), b.getTotalAmount(), b.getPaymentStatus());
            }
        } else if ("2".equals(c)) {
            System.out.print("Enter Bill ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            Bill b = billingService.getBillById(id);
            if (b != null) {
                System.out.println("\n=======================================================");
                System.out.println("                   SERVICE INVOICE                     ");
                System.out.println("=======================================================");
                System.out.println(" Invoice ID:       BILL-" + b.getBillId());
                System.out.println(" Billing Date:     " + b.getBillingDate());
                System.out.println(" Customer:         " + b.getCustomerName() + " (" + b.getCustomerPhone() + ")");
                System.out.println(" Vehicle:          " + b.getVehicleModel() + " [" + b.getRegistrationNumber() + "]");
                System.out.println(" Work Done:        " + b.getWorkPerformed());
                System.out.println("-------------------------------------------------------");
                System.out.printf(" Labour Charges:   ₹%10.2f%n", b.getLabourCharges());
                System.out.printf(" Spare Parts Cost: ₹%10.2f%n", b.getSparePartsCost());
                System.out.println("-------------------------------------------------------");
                System.out.printf(" TOTAL AMOUNT:     ₹%10.2f (Labour + Parts)%n", b.getTotalAmount());
                System.out.println(" Payment Status:   " + b.getPaymentStatus() + " via " + b.getPaymentMethod());
                System.out.println("=======================================================");
            } else {
                System.out.println("[ERROR] Bill not found.");
            }
        }
    }

    private static void handleReports() {
        System.out.println("\n--- REPORTS & ANALYTICS ---");
        System.out.println("1. Vehicle-Wise Maintenance Costs");
        System.out.println("2. Service Center Workload");
        System.out.print("Choice: ");
        String c = scanner.nextLine().trim();

        if ("1".equals(c)) {
            List<Map<String, Object>> list = reportService.getVehicleWiseMaintenanceCosts();
            System.out.printf("%-15s | %-22s | %-10s | %-10s | %-12s%n", "Reg No", "Model", "Services", "Labour", "Total Cost");
            System.out.println("-----------------------------------------------------------------------------------");
            for (Map<String, Object> row : list) {
                System.out.printf("%-15s | %-22s | %-10d | ₹%-9.2f | ₹%-11.2f%n", 
                    row.get("registrationNumber"), row.get("model"), 
                    row.get("totalServices"), row.get("totalLabourCost"), row.get("totalMaintenanceExpenditure"));
            }
        } else if ("2".equals(c)) {
            Map<String, Object> workload = reportService.getServiceCenterWorkload();
            System.out.println("\nAppointment Status Distribution:");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> statusDist = (List<Map<String, Object>>) workload.get("statusDistribution");
            for (Map<String, Object> item : statusDist) {
                System.out.printf(" - %-12s: %d appointments%n", item.get("status"), item.get("count"));
            }
        }
    }
}
