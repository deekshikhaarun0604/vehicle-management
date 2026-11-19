# Software Requirements Specification (SRS)
## Vehicle Service Management and Maintenance Analysis System

---

## 1. Introduction

### 1.1 Purpose
This Software Requirements Specification (SRS) details the functional and non-functional requirements for the **Vehicle Service Management and Maintenance Analysis System**. The document serves as the formal specification for software developers, testers, and academic evaluators.

### 1.2 Scope
The system is an automobile service management suite designed to replace manual record maintenance. Key capabilities include:
- Complete customer profile management.
- Vehicle registration and owner linkage.
- Service appointment scheduling with automated conflict detection.
- Maintenance job card tracking with detailed spare parts consumption and labour recording.
- Billing calculations enforcing the business formula: $\text{Total Bill} = \text{Labour Charges} + \text{Spare Parts Costs}$.
- Historical reporting on service records, vehicle-wise maintenance expenditures, and service center workload.
- Python-based data exploration, preprocessing, summary statistics, and visualization using Matplotlib and Seaborn.

### 1.3 Definitions, Acronyms, and Abbreviations
- **CRUD**: Create, Read, Update, Delete
- **DBMS**: Database Management System
- **ER**: Entity-Relationship
- **JDBC**: Java Database Connectivity
- **OOP**: Object-Oriented Programming
- **3NF**: Third Normal Form

---

## 2. Overall Description

### 2.1 Product Perspective
The system operates as a client-server application. The frontend communicates with a lightweight Java HTTP server exposing RESTful endpoints. The Java backend communicates with a MySQL relational database via JDBC. An integrated Python analytics pipeline queries the database to produce exploratory data analyses and graphical visualizations.

### 2.2 User Characteristics
- **Service Advisor**: Enters new customers, registers vehicles, and books service appointments.
- **Service Technician / Mechanic**: Logs maintenance activities, selects parts used, and enters labour charges.
- **Cashier / Accountant**: Reviews invoices, accepts payments, and updates bill statuses.
- **Service Manager**: Reviews workload metrics, vehicle cost histories, and analytical charts.

### 2.3 General Constraints
- Backend must be implemented in Java using Object-Oriented Programming and JDBC.
- Database must be MySQL with normalized tables up to 3NF.
- Frontend must be implemented in standard HTML, CSS, and JavaScript.
- Data analysis must be executed in Python using Pandas, NumPy, Matplotlib, and Seaborn.

---

## 3. Specific System Requirements

### 3.1 Module 1: Customer Management
- **FR-1.1**: The system shall allow creation of new customers with unique IDs, names, phone numbers, email addresses, and postal addresses.
- **FR-1.2**: The system shall validate that phone numbers and email addresses are unique and formatted correctly.
- **FR-1.3**: The system shall display all customer records in an interactive, searchable table.
- **FR-1.4**: The system shall permit updating customer phone, email, and address.
- **FR-1.5**: The system shall allow deleting customer records with cascade deletion of associated vehicles and appointments.

### 3.2 Module 2: Vehicle Management
- **FR-2.1**: The system shall record vehicle ID, registration number, model, vehicle type (Sedan, SUV, Hatchback, Truck, Van, Motorcycle), and manufacture year.
- **FR-2.2**: The system shall associate each vehicle with a valid registered customer.
- **FR-2.3**: The system shall enforce uniqueness of vehicle registration numbers.
- **FR-2.4**: The system shall support viewing, updating, and deleting vehicle records.

### 3.3 Module 3: Service Appointment Management
- **FR-3.1**: The system shall schedule service appointments by capturing appointment date, time, requested service type, and notes.
- **FR-3.2**: The system shall associate appointments with a specific customer and vehicle.
- **FR-3.3**: The system shall implement automated conflict checking:
  - Disallow booking the same vehicle for multiple appointments on the same date and time slot.
- **FR-3.4**: The system shall display appointment statuses (`Scheduled`, `In Progress`, `Completed`, `Cancelled`) and permit status updates.

### 3.4 Module 4: Maintenance and Service Records
- **FR-4.1**: The system shall record maintenance jobs linked to vehicles and optional appointments.
- **FR-4.2**: The system shall record detailed work performed, labour charges, and maintenance status.
- **FR-4.3**: The system shall support linking one or more spare parts with quantities and unit prices to a maintenance record.
- **FR-4.4**: The system shall maintain an inventory of spare parts and update stock quantities accordingly.

### 3.5 Module 5: Billing Management
- **FR-5.1**: The system shall compute total spare parts cost: $\text{Spare Parts Cost} = \sum (\text{Quantity} \times \text{Unit Price})$.
- **FR-5.2**: The system shall calculate the total bill using:
  $$\text{Total Bill} = \text{Labour Charges} + \text{Spare Parts Costs}$$
- **FR-5.3**: The system shall store billing records with bill ID, reference maintenance ID, labour charges, parts cost, total amount, billing date, payment status, and payment method.
- **FR-5.4**: The system shall render printable and viewable invoice summaries.

### 3.6 Module 6: Reports
- **FR-6.1**: The system shall generate comprehensive **Service History** reports searchable by vehicle registration number.
- **FR-6.2**: The system shall generate **Vehicle-wise Maintenance Costs** aggregated across all past services.
- **FR-6.3**: The system shall generate **Service Center Workload** reports detailing appointment distributions by date, day of week, and status.

### 3.7 Module 7: Python Data Analysis and Visualization
- **FR-7.1**: The Python subsystem shall connect to the database and retrieve service, parts, and billing datasets.
- **FR-7.2**: The pipeline shall perform data preprocessing: missing value imputation, duplicate removal, and type conversions.
- **FR-7.3**: The pipeline shall compute descriptive summary statistics (mean, median, standard deviation, quartiles).
- **FR-7.4**: The pipeline shall generate high-resolution Matplotlib/Seaborn visualizations:
  1. Service records distribution and monthly trends.
  2. Vehicle-wise maintenance expenditure and cost by vehicle type.
  3. Labour charges vs. Spare parts costs breakdown.
  4. Service center appointment workload distribution.

---

## 4. Non-Functional Requirements
- **Performance**: API responses for CRUD operations shall complete within 200ms under standard loads.
- **Security & Integrity**: Parameterized SQL queries (`PreparedStatement`) shall prevent SQL injection vulnerabilities.
- **Usability**: Responsive, intuitive dashboard UI with clear action feedback and error notifications.
- **Portability**: Standard Java SE 9+ and Python 3.10+ compatibility across Windows, Linux, and macOS.
