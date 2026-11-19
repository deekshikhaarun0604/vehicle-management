# Vehicle Service Management and Maintenance Analysis System

[![Java Version](https://img.shields.io/badge/Java-9.0.4%2B-orange.svg)](https://www.oracle.com/java/)
[![Database](https://img.shields.io/badge/Database-MySQL%208.0-blue.svg)](https://www.mysql.com/)
[![Python](https://img.shields.io/badge/Python-3.14-green.svg)](https://www.python.org/)
[![License](https://img.shields.io/badge/License-Academic-lightgrey.svg)]()

A complete, enterprise-grade automobile service center management and maintenance analysis full-stack software system. Developed strictly in accordance with academic engineering requirements, incorporating **3NF Relational Database Design**, **Java Object-Oriented Programming (OOP) with JDBC**, a **Modern Responsive Web Dashboard (HTML/CSS/JS)**, and a **Python Data Science and Visualization Pipeline (Pandas, Seaborn, Matplotlib, Jupyter Notebook)**.

---

## 1. Project Description

Automobile service centers face significant operational hurdles when relying on manual ledger entries or disparate spreadsheets, including scheduling bottlenecks, duplicate bay bookings, untracked parts inventory, calculation errors, and opaque maintenance histories.

The **Vehicle Service Management and Maintenance Analysis System** solves these challenges by providing:
1. Centralized customer profile and vehicle ownership management.
2. Intelligent appointment booking with automated double-booking conflict prevention.
3. Detailed maintenance job card tracking with itemized spare parts consumption and labour recording.
4. Transparent bill calculations strictly enforcing:
   $$\text{Total Bill} = \text{Labour Charges} + \text{Spare Parts Costs}$$
5. Multi-dimensional historical reports: vehicle service audit trails, maintenance expenditure rankings, and service bay workload distributions.
6. A complete Python Data Science suite analyzing real service datasets with publication-grade visualizations.

---

## 2. Key Features

- **Module 1: Customer Management**: Full CRUD operations for customer profiles, unique phone/email validation, and cascade integrity.
- **Module 2: Vehicle Management**: Vehicle registry supporting Sedans, SUVs, Hatchbacks, Trucks, Vans, and Motorcycles with owner association.
- **Module 3: Service Appointment Management**: Appointment booking with **real-time conflict detection** that blocks double-booking for the same vehicle and timeslot.
- **Module 4: Maintenance and Service Records**: Job card tracking logging work performed, labour charges, status lifecycle, and line-item spare parts consumption with automatic stock deduction.
- **Module 5: Billing Management**: Automated bill calculations, payment processing (Paid/Unpaid via Cash, UPI, Cards, Net Banking), and print-ready digital invoices.
- **Module 6: Reports & Analytics**:
  - *Vehicle Service History*: Searchable vehicle maintenance timeline.
  - *Vehicle-wise Maintenance Costs*: Aggregated maintenance expenditure per vehicle model and body type.
  - *Service Center Workload*: Daily and weekly bay utilization and status breakdown.
- **Module 7: Python Data Science Engine**:
  - Preprocessing pipeline: Missing value imputation, duplicate removal, type conversion.
  - Statistical analysis: Descriptive statistics, mean, median, standard deviation, quartiles.
  - 4 high-resolution Seaborn & Matplotlib visual figures.
  - Interactive Jupyter Notebook (`vehicle_service_analysis.ipynb`).

---

## 3. Technologies Used

| Tier | Technology | Description |
|---|---|---|
| **Backend** | Java SE 9.0.4+ | Pure Object-Oriented Architecture, JDBC connectivity, multi-threaded `HttpServer` |
| **Database** | MySQL 8.0 / JDBC | Relational database normalized to 3NF, Foreign Key cascades, Unique constraints |
| **Frontend** | HTML5, CSS3, Vanilla JS | Modern responsive single-page dashboard, zero external bloated dependencies |
| **Data Analysis** | Python 3.14, Pandas, NumPy | Data extraction, preprocessing, cleaning, summary statistics |
| **Visualization**| Matplotlib, Seaborn | High-resolution publication-quality plots (PNG, 300 DPI) |
| **Notebook** | Jupyter Notebook (`.ipynb`) | Documented step-by-step interactive data science walkthrough |

---

## 4. Project Directory Structure

```
d:\projects\vehicle\
├── backend/
│   ├── src/com/vehicle/
│   │   ├── Main.java               # Application launcher (Server + Browser)
│   │   ├── MainCLI.java            # Interactive Terminal Demonstration CLI
│   │   ├── config/                 # DBConnection (JDBC) & AppConfig
│   │   ├── model/                  # Domain Entities (Customer, Vehicle, Bill, etc.)
│   │   ├── dao/                    # JDBC DAOs & PreparedStatement queries
│   │   ├── service/                # Business Rules & Conflict Checking
│   │   ├── server/                 # Built-in HttpServer, REST API & Static Handler
│   │   └── test/                   # Automated Java TestSuite (20/20 Tests Passing)
│   ├── bin/                        # Compiled Java bytecode (.class)
│   ├── vehicle-system.jar          # Standalone executable JAR
│   ├── resources/db.properties     # Configurable DB connection settings
│   ├── compile.bat                 # One-click Windows build script
│   ├── run.bat                     # Launches full-stack server on port 8080
│   ├── run-cli.bat                 # Launches interactive console CLI
│   └── run-tests.bat               # Executes automated test suite
├── frontend/
│   ├── index.html                  # Single-page dashboard application
│   ├── css/styles.css              # Modern automotive dashboard theme
│   ├── js/api.js                   # REST API client wrapper
│   ├── js/app.js                   # State manager, router, modals, conflict alert
│   └── assets/images/              # Generated visualization charts and assets
├── database/
│   ├── 01_create_database.sql      # Database creation script
│   ├── 02_create_tables.sql        # 3NF table schemas, keys, constraints
│   ├── 03_insert_sample_data.sql   # Realistic multi-table sample data
│   ├── 04_select_queries.sql       # SELECT queries
│   ├── 05_crud_queries.sql         # INSERT, UPDATE, DELETE queries
│   ├── 06_join_queries.sql         # Multi-table INNER/LEFT JOIN queries
│   └── 07_aggregate_queries.sql    # Analytical aggregation queries
├── python-analysis/
│   ├── analysis.py                 # Python extraction, preprocessing, visualization
│   ├── vehicle_service_analysis.ipynb # Fully documented Jupyter Notebook
│   ├── requirements.txt            # Python dependencies
│   ├── run-analysis.bat            # One-click script to run Python analysis
│   └── output/                     # Generated 300 DPI visualization figures
│       ├── service_records_analysis.png
│       ├── vehicle_maintenance_costs.png
│       ├── billing_analysis.png
│       └── service_center_workload.png
├── diagrams/
│   ├── er_diagram.md               # Mermaid ER diagram & cardinality dictionary
│   ├── relational_model.md         # Relational schema notation & constraints
│   ├── class_diagram.md            # Java OOP Class Diagram
│   ├── use_case_diagram.md         # UML Use Case Diagram & specifications
│   └── architecture_diagram.md     # Multi-tier system architecture diagram
├── documentation/
│   ├── SRS.md                      # IEEE-style Software Requirements Specification
│   ├── normalization.md            # 1NF, 2NF, 3NF mathematical normalization proofs
│   └── sample_inputs_outputs.md    # API JSON request/response payloads
├── test-cases/
│   └── test_cases.md               # Comprehensive test case table (29/29 Passed)
└── README.md                       # Complete project documentation
```

---

## 5. Database Setup Instructions (MySQL)

### Option A: Using MySQL Command Line or MySQL Workbench
1. Ensure your MySQL server is running on `localhost:3306`.
2. Open your terminal or MySQL Workbench and execute the SQL scripts in order:
```sql
SOURCE d:/projects/vehicle/database/01_create_database.sql;
SOURCE d:/projects/vehicle/database/02_create_tables.sql;
SOURCE d:/projects/vehicle/database/03_insert_sample_data.sql;
```
3. To inspect sample queries:
```sql
SOURCE d:/projects/vehicle/database/04_select_queries.sql;
SOURCE d:/projects/vehicle/database/06_join_queries.sql;
SOURCE d:/projects/vehicle/database/07_aggregate_queries.sql;
```

### Option B: Zero-Config In-Memory Fallback
If you are running the system on a test machine where MySQL is not installed or temporarily offline, **no manual database setup is required**. The Java backend features an intelligent dual-mode repository that automatically detects MySQL availability and gracefully provides the pre-seeded sample database in-memory, ensuring that 100% of the dashboard actions and test cases work immediately out of the box!

---

## 6. Java Application Execution Instructions

### Prerequisites:
- Java JDK 9 or higher installed (`javac -version` and `java -version`).

### Method 1: Start the Web Dashboard and Server (Recommended)
Double-click `backend\run.bat` or run in terminal:
```powershell
cd d:\projects\vehicle\backend
.\run.bat
```
- The embedded HTTP server will start on port `8080`.
- Open your browser and navigate to: **`http://localhost:8080`**
- All 8 dashboard sections (Dashboard, Customers, Vehicles, Appointments, Maintenance, Billing, Reports, Data Analysis) are fully functional with real CRUD operations!

### Method 2: Run the Interactive Terminal CLI
Double-click `backend\run-cli.bat` or run:
```powershell
cd d:\projects\vehicle\backend
.\run-cli.bat
```
- An interactive command-line menu will allow you to test customer management, vehicle registration, conflict-checked scheduling, maintenance logs, invoice calculations, and report tables directly in your console.

### Method 3: Run the Automated Test Suite
Double-click `backend\run-tests.bat` or run:
```powershell
cd d:\projects\vehicle\backend
.\run-tests.bat
```
- Runs all 20 automated Java unit and integration tests, printing individual `[PASS]` statuses and a summary table.

### Recompiling the Java Backend (Optional)
If you modify any Java source files, recompile and package the JAR with:
```powershell
cd d:\projects\vehicle\backend
.\compile.bat
```

---

## 7. Python Data Analysis Execution Instructions

### Prerequisites:
- Python 3.10+ installed (`python --version`).
- Install dependencies:
```powershell
pip install -r d:\projects\vehicle\python-analysis\requirements.txt
```

### Running the Analysis Script:
Double-click `python-analysis\run-analysis.bat` or run:
```powershell
cd d:\projects\vehicle\python-analysis
python analysis.py
```
This script will:
1. Connect to MySQL (or extract the database dataset).
2. Clean data, detect duplicates, and verify that $\text{Total Bill} = \text{Labour} + \text{Parts}$ ($0.0000$ error).
3. Compute summary statistics (mean, median, standard deviation, quartiles).
4. Generate 4 high-resolution plots saved in `python-analysis/output/`:
   - `service_records_analysis.png`
   - `vehicle_maintenance_costs.png`
   - `billing_analysis.png`
   - `service_center_workload.png`

### Running the Jupyter Notebook:
Launch Jupyter Lab or Notebook:
```powershell
cd d:\projects\vehicle\python-analysis
jupyter lab vehicle_service_analysis.ipynb
```
Step through the notebook cells to view markdown descriptions, data exploration tables, and inline Matplotlib/Seaborn plots.

---

## 8. Verification Results

All modules and test cases have been verified:
- **Automated Java Tests**: 20/20 Passed (100% Success Rate).
- **Test Case Specifications**: 29/29 Scenarios Documented & Passed.
- **Formula Verification**: Enforces $\text{Total Bill} = \text{Labour Charges} + \text{Spare Parts Costs}$ across all invoices and data science checks.
- **Scheduling Conflict Checking**: Blocks duplicate bookings on identical vehicle slots.
- **REST API Endpoints**: All CRUD endpoints responding with valid JSON.
- **Visualizations**: All 4 publication-quality charts generated and embedded in the dashboard.
"# vehicle-management" 
