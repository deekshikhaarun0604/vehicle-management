/**
 * AutoCare Pro - API Client
 * Wraps REST HTTP calls to the Java backend endpoints.
 */
const API_BASE = '/api';

const api = {
    async request(endpoint, options = {}) {
        const url = `${API_BASE}${endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        };

        try {
            const response = await fetch(url, { ...options, headers });
            const data = await response.json();
            if (!response.ok) {
                throw new Error(data.error || `HTTP error! status: ${response.status}`);
            }
            return data;
        } catch (error) {
            console.error(`API Error [${endpoint}]:`, error);
            throw error;
        }
    },

    // Customers
    getCustomers() { return this.request('/customers'); },
    createCustomer(payload) { return this.request('/customers', { method: 'POST', body: JSON.stringify(payload) }); },
    updateCustomer(payload) { return this.request('/customers', { method: 'PUT', body: JSON.stringify(payload) }); },
    deleteCustomer(id) { return this.request(`/customers?id=${id}`, { method: 'DELETE' }); },

    // Vehicles
    getVehicles() { return this.request('/vehicles'); },
    createVehicle(payload) { return this.request('/vehicles', { method: 'POST', body: JSON.stringify(payload) }); },
    updateVehicle(payload) { return this.request('/vehicles', { method: 'PUT', body: JSON.stringify(payload) }); },
    deleteVehicle(id) { return this.request(`/vehicles?id=${id}`, { method: 'DELETE' }); },

    // Appointments
    getAppointments() { return this.request('/appointments'); },
    scheduleAppointment(payload) { return this.request('/appointments', { method: 'POST', body: JSON.stringify(payload) }); },
    updateAppointmentStatus(id, newStatus) { 
        return this.request('/appointments', { method: 'PUT', body: JSON.stringify({ appointmentId: String(id), newStatus }) }); 
    },
    deleteAppointment(id) { return this.request(`/appointments?id=${id}`, { method: 'DELETE' }); },

    // Spare Parts
    getSpareParts() { return this.request('/spare-parts'); },

    // Maintenance
    getMaintenanceRecords() { return this.request('/maintenance'); },
    createMaintenanceRecord(payload) { return this.request('/maintenance', { method: 'POST', body: JSON.stringify(payload) }); },
    deleteMaintenanceRecord(id) { return this.request(`/maintenance?id=${id}`, { method: 'DELETE' }); },

    // Billing
    getBills() { return this.request('/bills'); },
    getBillById(id) { return this.request(`/bills?id=${id}`); },
    createBill(recordId, paymentMethod = 'Cash', paymentStatus = 'Paid') {
        return this.request('/bills', { method: 'POST', body: JSON.stringify({ recordId: String(recordId), paymentMethod, paymentStatus }) });
    },
    updatePaymentStatus(billId, paymentStatus, paymentMethod) {
        return this.request('/bills', { method: 'PUT', body: JSON.stringify({ billId: String(billId), paymentStatus, paymentMethod }) });
    },

    // Reports & Dashboard
    getDashboardStats() { return this.request('/dashboard/stats'); },
    getServiceHistory(vehicleId = null) {
        const query = vehicleId ? `?vehicleId=${vehicleId}` : '';
        return this.request(`/reports/history${query}`);
    },
    getVehicleCosts() { return this.request('/reports/vehicle-costs'); },
    getWorkload() { return this.request('/reports/workload'); }
};
