# Entity-Relationship (ER) Diagram

## 1. Overview
The **Vehicle Service Management and Maintenance Analysis System** database model is designed following Third Normal Form (3NF) relational principles to eliminate data redundancy, maintain referential integrity, and accurately model the business operations of an automobile service center.

---

## 2. Mermaid Entity-Relationship Diagram

```mermaid
erDiagram
    CUSTOMERS ||--o{ VEHICLES : "owns"
    CUSTOMERS ||--o{ APPOINTMENTS : "books"
    VEHICLES ||--o{ APPOINTMENTS : "scheduled_for"
    VEHICLES ||--o{ MAINTENANCE_RECORDS : "undergoes"
    APPOINTMENTS ||--o| MAINTENANCE_RECORDS : "generates"
    MAINTENANCE_RECORDS ||--o{ MAINTENANCE_PARTS : "consumes"
    SPARE_PARTS ||--o{ MAINTENANCE_PARTS : "supplied_to"
    MAINTENANCE_RECORDS ||--|| BILLS : "billed_as"

    CUSTOMERS {
        int customer_id PK
        string name
        string phone UK
        string email UK
        string address
        timestamp created_at
    }

    VEHICLES {
        int vehicle_id PK
        int customer_id FK
        string registration_number UK
        string model
        string vehicle_type
        int manufacture_year
        timestamp created_at
    }

    APPOINTMENTS {
        int appointment_id PK
        int customer_id FK
        int vehicle_id FK
        date appointment_date
        time appointment_time
        string service_type
        string status
        text notes
        timestamp created_at
    }

    SPARE_PARTS {
        int part_id PK
        string part_number UK
        string part_name
        decimal unit_price
        int stock_quantity
    }

    MAINTENANCE_RECORDS {
        int record_id PK
        int appointment_id FK
        int vehicle_id FK
        date service_date
        text work_performed
        decimal labour_charges
        string status
        timestamp created_at
    }

    MAINTENANCE_PARTS {
        int maint_part_id PK
        int record_id FK
        int part_id FK
        int quantity
        decimal unit_price
        decimal subtotal
    }

    BILLS {
        int bill_id PK
        int record_id FK,UK
        decimal labour_charges
        decimal spare_parts_cost
        decimal total_amount
        string payment_status
        string payment_method
        date billing_date
        timestamp created_at
    }
```

---

## 3. Relationship Cardinalities and Constraints

| Relationship | Entities | Cardinality | Enforced Constraints |
|---|---|---|---|
| Customer owns Vehicles | `customers` &rarr; `vehicles` | 1 : N (One-to-Many) | `ON DELETE CASCADE`: Removing customer removes their vehicles. |
| Customer schedules Appointments | `customers` &rarr; `appointments` | 1 : N (One-to-Many) | `ON DELETE CASCADE` |
| Vehicle undergoes Appointments | `vehicles` &rarr; `appointments` | 1 : N (One-to-Many) | `uq_vehicle_slot (vehicle_id, appointment_date, appointment_time)` prevents double-booking. |
| Vehicle undergoes Maintenance | `vehicles` &rarr; `maintenance_records` | 1 : N (One-to-Many) | Foreign key link tracks service history per vehicle. |
| Appointment generates Maintenance | `appointments` &rarr; `maintenance_records` | 1 : 1 or 1 : 0 (Optional One-to-One) | `appointment_id` is nullable (walk-in jobs allowed), `ON DELETE SET NULL`. |
| Maintenance consumes Parts | `maintenance_records` &rarr; `maintenance_parts` &larr; `spare_parts` | M : N (Many-to-Many via Junction Table) | `quantity > 0`, `subtotal = quantity * unit_price`. `ON DELETE RESTRICT` on parts prevents deleting parts in active bills. |
| Maintenance generates Bill | `maintenance_records` &rarr; `bills` | 1 : 1 (One-to-One) | `record_id` is unique in `bills`. `chk_total_amount: total_amount = labour_charges + spare_parts_cost`. |
