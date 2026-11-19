# Comprehensive Test Cases Specification
## Vehicle Service Management and Maintenance Analysis System

This document outlines the test case specifications, validation scenarios, inputs, expected results, actual results, and Pass/Fail statuses across all system modules.

---

## 1. Test Summary Matrix

| Module ID | Module Name | Total Tests | Passed | Failed | Pass Rate |
|---|---|---|---|---|---|
| MOD-01 | Customer Management | 5 | 5 | 0 | 100% |
| MOD-02 | Vehicle Management | 4 | 4 | 0 | 100% |
| MOD-03 | Service Appointment Management (Conflict Prevention) | 4 | 4 | 0 | 100% |
| MOD-04 | Maintenance & Spare Parts Records | 4 | 4 | 0 | 100% |
| MOD-05 | Billing Management ($\text{Total} = \text{Labour} + \text{Parts}$) | 4 | 4 | 0 | 100% |
| MOD-06 | Reports & Analytics | 4 | 4 | 0 | 100% |
| MOD-07 | Python Data Science & Visualization | 4 | 4 | 0 | 100% |
| **Total** | **Full System Verification** | **29** | **29** | **0** | **100%** |

---

## 2. Detailed Test Cases

### Module 1: Customer Management
| Test Case ID | Test Scenario | Input | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **TC-CUST-01** | Retrieve all customer profiles | `GET /api/customers` | Returns HTTP 200 with JSON array of registered customers. | Array with 10+ customers returned successfully. | **PASS** |
| **TC-CUST-02** | Add valid customer profile | Name: "Rohan Gupta", Phone: "+91 99887 66554", Email: "rohan@example.com", Address: "12 Lake Rd" | Returns HTTP 201 Created; customer is assigned unique `customerId`. | Customer created with ID 11; persisted in storage. | **PASS** |
| **TC-CUST-03** | Fetch single customer by ID | `GET /api/customers?id=1` | Returns HTTP 200 with customer object matching ID 1 ("Rahul Sharma"). | Correct record matching ID 1 returned. | **PASS** |
| **TC-CUST-04** | Enforce non-empty name validation | Name: `""`, Phone: "+91 98765 00000" | Rejection with `IllegalArgumentException` / HTTP 400. | System rejects request with validation error. | **PASS** |
| **TC-CUST-05** | Update customer contact information | Phone: "+91 99887 00000", Address: "55 Residency Road" | Returns HTTP 200 OK; modified fields updated in DB. | Customer record updated with new phone and address. | **PASS** |

---

### Module 2: Vehicle Management
| Test Case ID | Test Scenario | Input | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **TC-VEH-01** | Retrieve all vehicle records | `GET /api/vehicles` | Returns HTTP 200 with vehicle list including customer owner names. | Returns all 15 vehicles with populated owner names. | **PASS** |
| **TC-VEH-02** | Add new vehicle linked to customer | Customer ID: 1, Reg: "KA-05-XY-9999", Model: "Hyundai Verna", Type: "Sedan", Year: 2023 | Returns HTTP 201; vehicle associated with Customer 1. | Vehicle registered with ID 16 and linked to Rahul Sharma. | **PASS** |
| **TC-VEH-03** | Enforce manufacture year constraint | Manufacture Year: 1980 | Rejection with `IllegalArgumentException` (Must be 1990-2030). | Request rejected with boundary validation message. | **PASS** |
| **TC-VEH-04** | Update vehicle specifications | Reg: "KA-01-AB-1234", Model: "Honda City ZX Facelift" | Returns HTTP 200 OK; updated record retrieved. | Vehicle model updated successfully. | **PASS** |

---

### Module 3: Service Appointment Management & Conflict Checking
| Test Case ID | Test Scenario | Input | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **TC-APPT-01** | Retrieve all service appointments | `GET /api/appointments` | Returns HTTP 200 with chronological list of appointments. | Appointments returned with date, time, vehicle, and status. | **PASS** |
| **TC-APPT-02** | Schedule non-conflicting appointment | Vehicle: 1, Customer: 1, Date: "2026-12-15", Time: "10:00:00", Type: "General Service" | Returns HTTP 201; appointment saved with status `Scheduled`. | Appointment booked successfully with ID 16. | **PASS** |
| **TC-APPT-03** | **Conflict Detection**: Prevent double-booking same vehicle & timeslot | Vehicle: 1, Date: "2026-12-15", Time: "10:00:00" (Identical slot) | Rejection with HTTP 409 Conflict: "Scheduling Conflict detected". | System blocks booking; displays alert to user. | **PASS** |
| **TC-APPT-04** | Update appointment lifecycle status | Appointment ID: 1, Status: `Completed` | Returns HTTP 200 OK; status updated in database. | Appointment marked `Completed`. | **PASS** |

---

### Module 4: Maintenance Records & Spare Parts
| Test Case ID | Test Scenario | Input | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **TC-MAINT-01** | Retrieve all maintenance records | `GET /api/maintenance` | Returns HTTP 200 with list of completed and in-progress jobs. | 12 maintenance records retrieved with full details. | **PASS** |
| **TC-MAINT-02** | Log service job with spare parts used | Vehicle: 1, Date: "2026-12-15", Work: "Brake service", Labour: 1000.00, Part: Brake Pads (1800.00) | Record created; Spare parts cost calculated as 1800.00; Total bill computed as 2800.00. | Record created; spare parts deducted from stock. | **PASS** |
| **TC-MAINT-03** | Fetch spare parts catalog and inventory | `GET /api/spare-parts` | Returns 12 spare parts with part numbers, unit prices, and stock. | Spare parts catalog returned accurately. | **PASS** |
| **TC-MAINT-04** | Validate positive labour charges | Labour charges: `-500.00` | Rejection with `IllegalArgumentException`. | System rejects negative charges. | **PASS** |

---

### Module 5: Billing Management
| Test Case ID | Test Scenario | Input | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **TC-BILL-01** | Retrieve all billing records | `GET /api/bills` | Returns HTTP 200 with list of bills. | Returns all invoices with payment statuses. | **PASS** |
| **TC-BILL-02** | **Formula Verification**: $\text{Total} = \text{Labour} + \text{Parts}$ | Bill ID 1: Labour = 1500.00, Parts = 3500.00 | Total Amount must equal exactly $1500 + 3500 = 5000.00$. | Total Amount = 5000.00. Exact mathematical match. | **PASS** |
| **TC-BILL-03** | Auto-generate bill from maintenance job | Record ID 12 (Labour = 1100.00, Parts = 1800.00) | Bill generated with total = 2900.00; Status `Unpaid`. | Bill created with Total = 2900.00. | **PASS** |
| **TC-BILL-04** | Settle bill payment | Bill ID 11: Mark `Paid` via `UPI` | Returns HTTP 200 OK; Status changed from `Unpaid` to `Paid`. | Payment status updated to Paid via UPI. | **PASS** |

---

### Module 6: Reports & Workload Analytics
| Test Case ID | Test Scenario | Input | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **TC-REP-01** | Vehicle service history report | `GET /api/reports/history?vehicleId=1` | Returns complete timeline of services and invoices for Vehicle 1. | Returns past service history with labour, parts, and bills. | **PASS** |
| **TC-REP-02** | Vehicle-wise maintenance costs | `GET /api/reports/vehicle-costs` | Returns aggregated table of total maintenance costs per vehicle sorted descending. | Top spending vehicles (Mercedes GLC, Safari, Innova) ranked accurately. | **PASS** |
| **TC-REP-03** | Service center workload metrics | `GET /api/reports/workload` | Returns appointment count breakdown by status and daily workload schedule. | Status distribution and daily schedule computed correctly. | **PASS** |
| **TC-REP-04** | Real-time dashboard KPI summary | `GET /api/dashboard/stats` | Returns real-time counts of customers, vehicles, appointments, and gross revenue. | Accurate metrics matching database state. | **PASS** |

---

### Module 7: Python Data Science & Visualization
| Test Case ID | Test Scenario | Input | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **TC-PY-01** | Database retrieval and preprocessing | Execute `analysis.py` | Extracts dataset, verifies 0 duplicates, 0 unhandled nulls, converts datatypes. | Data extracted, preprocessed, and verified cleanly. | **PASS** |
| **TC-PY-02** | Summary statistics calculation | Execute `compute_summary_statistics()` | Calculates mean, median, standard deviation, quartiles for costs. | Descriptive stats table and revenue totals printed. | **PASS** |
| **TC-PY-03** | Visualizations 1 & 2 generation | Matplotlib / Seaborn plotting | Generates `service_records_analysis.png` and `vehicle_maintenance_costs.png`. | High-res PNG files saved in `output/` directory. | **PASS** |
| **TC-PY-04** | Visualizations 3 & 4 generation | Matplotlib / Seaborn plotting | Generates `billing_analysis.png` and `service_center_workload.png`. | High-res PNG files saved in `output/` directory. | **PASS** |
