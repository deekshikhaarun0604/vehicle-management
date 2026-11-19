# Sample Inputs and Outputs

This document records realistic sample inputs and outputs for all system modules and REST API endpoints.

---

## 1. Customer Management

### Sample Input (Create Customer)
```json
{
  "name": "Arjun Singhania",
  "phone": "+91 98330 44556",
  "email": "arjun.s@example.com",
  "address": "77 Palm Beach Road, Navi Mumbai, Maharashtra"
}
```

### Sample Output (HTTP 201 Created)
```json
{
  "success": true,
  "message": "Customer created successfully",
  "data": {
    "customerId": 11,
    "name": "Arjun Singhania",
    "phone": "+91 98330 44556",
    "email": "arjun.s@example.com",
    "address": "77 Palm Beach Road, Navi Mumbai, Maharashtra"
  }
}
```

---

## 2. Vehicle Management

### Sample Input (Create Vehicle)
```json
{
  "customerId": 11,
  "registrationNumber": "MH-43-AK-7788",
  "model": "Hyundai Tucson 2.0 AWD",
  "vehicleType": "SUV",
  "manufactureYear": 2023
}
```

### Sample Output (HTTP 201 Created)
```json
{
  "success": true,
  "message": "Vehicle added successfully",
  "data": {
    "vehicleId": 16,
    "customerId": 11,
    "registrationNumber": "MH-43-AK-7788",
    "model": "Hyundai Tucson 2.0 AWD",
    "vehicleType": "SUV",
    "manufactureYear": 2023
  }
}
```

---

## 3. Appointment Scheduling (Conflict Prevention)

### Sample Input (Schedule Appointment)
```json
{
  "customerId": 11,
  "vehicleId": 16,
  "appointmentDate": "2026-11-25",
  "appointmentTime": "10:30:00",
  "serviceType": "General Inspection & Oil Service",
  "notes": "First free service after 10,000 km run"
}
```

### Sample Output: Success (HTTP 201 Created)
```json
{
  "success": true,
  "message": "Appointment scheduled successfully",
  "data": {
    "appointmentId": 16,
    "status": "Scheduled"
  }
}
```

### Sample Output: Conflict Detected (HTTP 409 Conflict)
```json
{
  "success": false,
  "error": "Scheduling conflict: Vehicle MH-43-AK-7788 already has an active appointment on 2026-11-25 at 10:30:00"
}
```

---

## 4. Maintenance Record & Parts Usage

### Sample Input (Create Maintenance Job)
```json
{
  "appointmentId": 16,
  "vehicleId": 16,
  "serviceDate": "2026-11-25",
  "workPerformed": "Replaced synthetic engine oil, replaced oil filter, inspected all brakes",
  "labourCharges": 1200.00,
  "status": "Completed",
  "parts": [
    { "partId": 1, "quantity": 1, "unitPrice": 2400.00 },
    { "partId": 2, "quantity": 1, "unitPrice": 450.00 }
  ]
}
```

### Sample Output (HTTP 201 Created)
```json
{
  "success": true,
  "message": "Maintenance record created with 2 spare parts allocated",
  "data": {
    "recordId": 13,
    "labourCharges": 1200.00,
    "sparePartsCost": 2850.00
  }
}
```

---

## 5. Billing Management

### Bill Calculation Formula:
$$\text{Total Bill} = \text{Labour Charges} + \text{Spare Parts Costs}$$
$$\text{Total Bill} = 1200.00 + 2850.00 = 4050.00$$

### Sample Output (HTTP 200 OK - Invoice Details)
```json
{
  "billId": 12,
  "recordId": 13,
  "customerName": "Arjun Singhania",
  "vehicleRegNo": "MH-43-AK-7788",
  "vehicleModel": "Hyundai Tucson 2.0 AWD",
  "billingDate": "2026-11-25",
  "labourCharges": 1200.00,
  "sparePartsCost": 2850.00,
  "totalAmount": 4050.00,
  "paymentStatus": "Paid",
  "paymentMethod": "UPI",
  "itemizedParts": [
    { "partName": "Synthetic Engine Oil (4L)", "quantity": 1, "unitPrice": 2400.00, "subtotal": 2400.00 },
    { "partName": "Premium Oil Filter", "quantity": 1, "unitPrice": 450.00, "subtotal": 450.00 }
  ]
}
```

---

## 6. Reports

### Sample Output: Vehicle Maintenance Costs Summary
```json
[
  {
    "vehicleId": 12,
    "registrationNumber": "MH-04-WX-6789",
    "model": "Mercedes-Benz GLC 300",
    "vehicleType": "SUV",
    "ownerName": "Karan Johar",
    "totalServices": 1,
    "totalLabourCost": 3500.00,
    "totalPartsCost": 6900.00,
    "totalMaintenanceExpenditure": 10400.00
  },
  {
    "vehicleId": 5,
    "registrationNumber": "TN-09-IJ-7890",
    "model": "Toyota Innova Crysta",
    "vehicleType": "Van",
    "ownerName": "Sneha Iyer",
    "totalServices": 1,
    "totalLabourCost": 1600.00,
    "totalPartsCost": 4250.00,
    "totalMaintenanceExpenditure": 5850.00
  }
]
```
