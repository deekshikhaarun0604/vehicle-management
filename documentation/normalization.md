# Database Normalization Documentation

## 1. Introduction
Normalization is the process of structuring a relational database in order to reduce data redundancy and improve data integrity. In this project, the **Vehicle Service Management and Maintenance Analysis System** database was designed and verified through the stages of **1NF**, **2NF**, and **3NF**.

---

## 2. Unnormalized Form (UNF) Scenario
In an unnormalized legacy maintenance spreadsheet, a single record often combines customer, vehicle, service appointment, parts used, and billing details:

```
ServiceSheet(
    CustomerName, CustomerPhone, CustomerAddress, 
    VehicleRegNo, VehicleModel, VehicleType, 
    AppointmentDate, ServiceType, WorkPerformed, 
    PartNames[list], PartPrices[list], PartQtys[list], 
    LabourCost, TotalBill
)
```

### Anomalies in UNF:
1. **Insertion Anomaly**: Cannot register a newly purchased spare part or a new customer before they book an appointment.
2. **Deletion Anomaly**: If a service record is deleted, the customer's vehicle information or spare part catalog entry is inadvertently destroyed.
3. **Update Anomaly**: If a customer's phone number or address changes, multiple historical service entries must be updated, risking inconsistency.
4. **Repeating Groups**: Parts used are stored as comma-separated values or array rows, violating atomicity.

---

## 3. First Normal Form (1NF)
> **Definition**: A relation is in 1NF if and only if all underlying domains contain only atomic (indivisible) values, and there are no repeating groups.

### Transformations Applied:
1. Decomposed repeating spare parts entries into an independent junction entity (`maintenance_parts`).
2. Separated compound addresses and full customer attributes into atomic columns (`name`, `phone`, `email`, `address`).
3. Assigned unique surrogate Primary Keys to all relations (`customer_id`, `vehicle_id`, `appointment_id`, etc.).

**Outcome**: Every attribute contains only a single atomic value. No multi-valued attributes or nested arrays remain.

---

## 4. Second Normal Form (2NF)
> **Definition**: A relation is in 2NF if it is in 1NF and every non-prime attribute is fully functionally dependent on the entire primary key (no partial functional dependencies).

### Partial Dependency Analysis:
In the junction table mapping maintenance to spare parts (`maintenance_parts`), the composite key concept might be: `(record_id, part_id)`.
- If `part_name` or `unit_price` were kept inside `maintenance_parts`, they would be functionally dependent only on `part_id`, not on `(record_id, part_id)`.
- **Solution**:
  - The master catalog of parts is moved to its own relation: `spare_parts(part_id [PK], part_name, part_number, unit_price, stock_quantity)`.
  - The junction relation `maintenance_parts` stores only the snapshot transaction: `(maint_part_id [PK], record_id [FK], part_id [FK], quantity, unit_price, subtotal)`.

**Outcome**: All non-key attributes are fully dependent on the whole candidate key of each relation.

---

## 5. Third Normal Form (3NF)
> **Definition**: A relation is in 3NF if it is in 2NF and no non-prime attribute is transitively dependent on any candidate key (no $X \rightarrow Y \rightarrow Z$ where $X$ is key and $Y$ is non-key).

### Transitive Dependency Elimination:
1. **Customer & Vehicle**:
   - In a non-3NF design, `maintenance_records` might contain `customer_name` and `customer_phone`.
   - Transitive path: `record_id -> vehicle_id -> customer_id -> customer_name`.
   - **Solution**: `maintenance_records` contains only `vehicle_id`. `vehicles` contains `customer_id`. `customers` holds customer profile data.
2. **Billing & Spare Parts**:
   - Bill calculation uses `spare_parts_cost` and `labour_charges`. The actual part price resides in `spare_parts` or `maintenance_parts`, and `total_amount` is calculated as `labour_charges + spare_parts_cost`.
   - Separate `bills` relation stores finalized accounting transactions with a unique constraint on `record_id`.

**Outcome**: Every relation satisfies 3NF: *Every non-key attribute must provide a fact about the key, the whole key, and nothing but the key.*

---

## 6. Normalization Summary Matrix

| Relation | Primary Key | Foreign Keys | Normal Form | Functional Dependencies Resolved |
|---|---|---|---|---|
| `customers` | `customer_id` | None | 3NF | Customer identity independent of vehicle ownership |
| `vehicles` | `vehicle_id` | `customer_id` | 3NF | Vehicles associated with owners without duplicating owner profile |
| `appointments` | `appointment_id` | `customer_id`, `vehicle_id` | 3NF | Slot booking isolated from service execution |
| `spare_parts` | `part_id` | None | 3NF | Master inventory decoupled from service logs |
| `maintenance_records` | `record_id` | `appointment_id`, `vehicle_id` | 3NF | Service details independent of invoice/payment transactions |
| `maintenance_parts` | `maint_part_id` | `record_id`, `part_id` | 3NF | Resolves M:N relationship with transaction snapshots |
| `bills` | `bill_id` | `record_id` | 3NF | Financial invoices separated from technical maintenance logs |
