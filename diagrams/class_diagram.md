# Java OOP Class Diagram

## 1. Overview
The Java backend is designed with clean Object-Oriented principles (Encapsulation, High Cohesion, Separation of Concerns) partitioned across four clear architectural layers:
1. **Model / Domain Layer**: Encapsulated POJOs with getters, setters, and constructors.
2. **Data Access Object (DAO) Layer**: JDBC-based persistence with `PreparedStatement`.
3. **Service Layer**: Business rules, validation, scheduling conflict prevention, and bill calculations.
4. **Server & Controller Layer**: Embedded HTTP server (`com.sun.net.httpserver.HttpServer`) handling REST API requests and JSON serialization.

---

## 2. Mermaid Class Diagram

```mermaid
classDiagram
    class Customer {
        -int customerId
        -String name
        -String phone
        -String email
        -String address
        +getCustomerId() int
        +getName() String
        +setName(String)
        +getPhone() String
        +getEmail() String
        +getAddress() String
    }

    class Vehicle {
        -int vehicleId
        -int customerId
        -String registrationNumber
        -String model
        -String vehicleType
        -int manufactureYear
        +getVehicleId() int
        +getRegistrationNumber() String
        +getModel() String
        +getVehicleType() String
        +getManufactureYear() int
    }

    class Appointment {
        -int appointmentId
        -int customerId
        -int vehicleId
        -String appointmentDate
        -String appointmentTime
        -String serviceType
        -String status
        -String notes
        +getAppointmentId() int
        +getAppointmentDate() String
        +getStatus() String
    }

    class SparePart {
        -int partId
        -String partNumber
        -String partName
        -double unitPrice
        -int stockQuantity
        +getPartId() int
        +getPartName() String
        +getUnitPrice() double
        +getStockQuantity() int
    }

    class MaintenanceRecord {
        -int recordId
        -int appointmentId
        -int vehicleId
        -String serviceDate
        -String workPerformed
        -double labourCharges
        -String status
        -List~MaintenancePart~ partsUsed
        +getRecordId() int
        +getLabourCharges() double
        +getPartsUsed() List~MaintenancePart~
    }

    class MaintenancePart {
        -int maintPartId
        -int recordId
        -int partId
        -String partName
        -int quantity
        -double unitPrice
        -double subtotal
        +getSubtotal() double
    }

    class Bill {
        -int billId
        -int recordId
        -double labourCharges
        -double sparePartsCost
        -double totalAmount
        -String paymentStatus
        -String paymentMethod
        -String billingDate
        +calculateTotal() double
        +getTotalAmount() double
    }

    class DBConnection {
        -static Connection connection
        +static getConnection() Connection
        +static closeConnection()
    }

    class CustomerDAO {
        +addCustomer(Customer) boolean
        +getAllCustomers() List~Customer~
        +getCustomerById(int) Customer
        +updateCustomer(Customer) boolean
        +deleteCustomer(int) boolean
    }

    class VehicleDAO {
        +addVehicle(Vehicle) boolean
        +getAllVehicles() List~Vehicle~
        +getVehicleById(int) Vehicle
        +getVehiclesByCustomer(int) List~Vehicle~
        +updateVehicle(Vehicle) boolean
        +deleteVehicle(int) boolean
    }

    class AppointmentDAO {
        +scheduleAppointment(Appointment) boolean
        +getAllAppointments() List~Appointment~
        +hasSchedulingConflict(int, String, String) boolean
        +updateStatus(int, String) boolean
    }

    class MaintenanceDAO {
        +addRecord(MaintenanceRecord) int
        +addPartUsage(MaintenancePart) boolean
        +getAllRecords() List~MaintenanceRecord~
        +getRecordById(int) MaintenanceRecord
        +updateRecord(MaintenanceRecord) boolean
    }

    class BillingDAO {
        +createBill(Bill) boolean
        +getAllBills() List~Bill~
        +getBillByRecordId(int) Bill
        +updatePaymentStatus(int, String, String) boolean
    }

    class BillingService {
        -BillingDAO billingDAO
        -MaintenanceDAO maintenanceDAO
        +calculateAndGenerateBill(int, double, String) Bill
    }

    class AppointmentService {
        -AppointmentDAO appointmentDAO
        +bookAppointment(Appointment) boolean
    }

    Customer "1" *-- "0..*" Vehicle
    Vehicle "1" *-- "0..*" Appointment
    Appointment "0..1" *-- "0..1" MaintenanceRecord
    Vehicle "1" *-- "0..*" MaintenanceRecord
    MaintenanceRecord "1" *-- "0..*" MaintenancePart
    SparePart "1" *-- "0..*" MaintenancePart
    MaintenanceRecord "1" *-- "1" Bill
    
    CustomerDAO ..> DBConnection
    VehicleDAO ..> DBConnection
    AppointmentDAO ..> DBConnection
    MaintenanceDAO ..> DBConnection
    BillingDAO ..> DBConnection
    
    AppointmentService --> AppointmentDAO
    BillingService --> BillingDAO
    BillingService --> MaintenanceDAO
```
