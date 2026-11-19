# Relational Model and Schema Specification

## 1. Relational Schema Representation
The database schema consists of seven normalized relations in Third Normal Form (3NF). Primary keys are underlined with `[PK]`, and Foreign keys are tagged with `[FK]`.

```
1. CUSTOMERS (
    customer_id [PK],
    name,
    phone [UNIQUE],
    email [UNIQUE],
    address,
    created_at
)

2. VEHICLES (
    vehicle_id [PK],
    customer_id [FK -> CUSTOMERS(customer_id)],
    registration_number [UNIQUE],
    model,
    vehicle_type,
    manufacture_year,
    created_at
)

3. APPOINTMENTS (
    appointment_id [PK],
    customer_id [FK -> CUSTOMERS(customer_id)],
    vehicle_id [FK -> VEHICLES(vehicle_id)],
    appointment_date,
    appointment_time,
    service_type,
    status,
    notes,
    created_at,
    [UNIQUE(vehicle_id, appointment_date, appointment_time)]
)

4. SPARE_PARTS (
    part_id [PK],
    part_number [UNIQUE],
    part_name,
    unit_price,
    stock_quantity
)

5. MAINTENANCE_RECORDS (
    record_id [PK],
    appointment_id [FK -> APPOINTMENTS(appointment_id)],
    vehicle_id [FK -> VEHICLES(vehicle_id)],
    service_date,
    work_performed,
    labour_charges,
    status,
    created_at
)

6. MAINTENANCE_PARTS (
    maint_part_id [PK],
    record_id [FK -> MAINTENANCE_RECORDS(record_id)],
    part_id [FK -> SPARE_PARTS(part_id)],
    quantity,
    unit_price,
    subtotal
)

7. BILLS (
    bill_id [PK],
    record_id [FK -> MAINTENANCE_RECORDS(record_id), UNIQUE],
    labour_charges,
    spare_parts_cost,
    total_amount,
    payment_status,
    payment_method,
    billing_date,
    created_at
)
```

---

## 2. Integrity Constraints

1. **Entity Integrity**:
   - Every table possesses an explicitly defined integer surrogate primary key (`AUTO_INCREMENT`).
   - None of the primary key attributes can contain `NULL` values.

2. **Referential Integrity**:
   - `VEHICLES.customer_id` must reference an existing `CUSTOMERS.customer_id`. Cascading delete removes associated vehicle records.
   - `APPOINTMENTS.customer_id` and `APPOINTMENTS.vehicle_id` must reference valid existing customers and vehicles.
   - `MAINTENANCE_RECORDS.vehicle_id` references `VEHICLES.vehicle_id`.
   - `MAINTENANCE_PARTS.record_id` and `part_id` reference `MAINTENANCE_RECORDS` and `SPARE_PARTS`.
   - `BILLS.record_id` is unique, enforcing that each maintenance job has at most one bill.

3. **Domain Constraints**:
   - `manufacture_year BETWEEN 1990 AND 2030`
   - `labour_charges >= 0.00`
   - `unit_price >= 0.00` and `stock_quantity >= 0`
   - `quantity > 0` and `subtotal = quantity * unit_price`
   - `total_amount = labour_charges + spare_parts_cost`
   - Enumerated values for `vehicle_type`, `status`, `payment_status`, and `payment_method`.
