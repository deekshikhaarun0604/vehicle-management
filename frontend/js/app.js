/**
 * AutoCare Pro - Main Application Controller
 * Handles routing, CRUD UI interactions, conflict checking alerts, and report generation.
 */

// Application State
const state = {
    customers: [],
    vehicles: [],
    appointments: [],
    spareParts: [],
    maintenance: [],
    bills: []
};

// Initialize Application
document.addEventListener('DOMContentLoaded', () => {
    initRouter();
    loadDashboard();
    setupEventListeners();
});

// Router & Section Switching
function initRouter() {
    window.addEventListener('hashchange', handleRoute);
    handleRoute();
}

function handleRoute() {
    const hash = window.location.hash.substring(1) || 'dashboard';
    const validSections = ['dashboard', 'customers', 'vehicles', 'appointments', 'maintenance', 'billing', 'reports', 'analysis'];
    const activeSection = validSections.includes(hash) ? hash : 'dashboard';

    // Update Nav
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.toggle('active', item.dataset.section === activeSection);
    });

    // Update View
    document.querySelectorAll('.content-section').forEach(sec => {
        sec.classList.toggle('active', sec.id === `section-${activeSection}`);
    });

    // Update Header Title
    const titles = {
        dashboard: 'Dashboard Overview',
        customers: 'Customer Management',
        vehicles: 'Vehicle Management',
        appointments: 'Service Appointment Management',
        maintenance: 'Maintenance & Service Records',
        billing: 'Billing & Invoices',
        reports: 'Analytical Reports',
        analysis: 'Data Science & Visualizations'
    };
    document.getElementById('page-title').textContent = titles[activeSection] || 'Dashboard';

    // Refresh Data for the specific section
    switch (activeSection) {
        case 'dashboard': loadDashboard(); break;
        case 'customers': loadCustomers(); break;
        case 'vehicles': loadVehicles(); break;
        case 'appointments': loadAppointments(); break;
        case 'maintenance': loadMaintenance(); break;
        case 'billing': loadBills(); break;
        case 'reports': loadReports(); break;
        case 'analysis': break;
    }
}

// Event Listeners for Filters
function setupEventListeners() {
    document.getElementById('customer-search')?.addEventListener('input', e => filterCustomers(e.target.value));
    document.getElementById('vehicle-search')?.addEventListener('input', e => filterVehicles(e.target.value));
    document.getElementById('appointment-status-filter')?.addEventListener('change', e => filterAppointments(e.target.value));
    document.getElementById('billing-filter')?.addEventListener('change', e => filterBills(e.target.value));

    // Close modal on click outside
    document.querySelectorAll('.modal-backdrop').forEach(modal => {
        modal.addEventListener('click', e => {
            if (e.target === modal) closeModal(modal.id);
        });
    });
}

// ==================== DASHBOARD MODULE ====================
async function loadDashboard() {
    try {
        const stats = await api.getDashboardStats();
        document.getElementById('stat-customers').textContent = stats.totalCustomers || 0;
        document.getElementById('stat-vehicles').textContent = stats.totalVehicles || 0;
        document.getElementById('stat-appointments').textContent = stats.totalAppointments || 0;
        document.getElementById('stat-services').textContent = stats.totalServices || 0;
        document.getElementById('stat-revenue').textContent = `₹${formatNumber(stats.totalRevenue || 0)}`;
        document.getElementById('stat-unpaid').textContent = `${stats.unpaidBills || 0} Unpaid Invoices`;

        // Load mini tables
        const [appts, maint] = await Promise.all([api.getAppointments(), api.getMaintenanceRecords()]);
        renderDashboardAppointments(appts.slice(0, 5));
        renderDashboardMaintenance(maint.slice(0, 5));
    } catch (e) {
        showToast('Error loading dashboard stats', 'error');
    }
}

function renderDashboardAppointments(list) {
    const tbody = document.querySelector('#dash-appointments-table tbody');
    tbody.innerHTML = list.map(a => `
        <tr>
            <td><strong>${a.appointmentDate}</strong> <small style="color:#64748b;">${a.appointmentTime}</small></td>
            <td>${a.vehicleModel} <span class="badge badge-type">${a.registrationNumber}</span></td>
            <td>${a.customerName}</td>
            <td>${a.serviceType}</td>
            <td><span class="badge badge-${a.status.toLowerCase().replace(' ', '-')}">${a.status}</span></td>
        </tr>
    `).join('') || '<tr><td colspan="5" class="text-center">No upcoming appointments.</td></tr>';
}

function renderDashboardMaintenance(list) {
    const tbody = document.querySelector('#dash-maintenance-table tbody');
    tbody.innerHTML = list.map(m => `
        <tr>
            <td>#JOB-${m.recordId}</td>
            <td>${m.registrationNumber} (${m.vehicleModel})</td>
            <td>${truncate(m.workPerformed, 40)}</td>
            <td><strong>₹${formatNumber(m.labourCharges)}</strong></td>
            <td><span class="badge badge-${m.status.toLowerCase().replace(' ', '-')}">${m.status}</span></td>
        </tr>
    `).join('') || '<tr><td colspan="5" class="text-center">No active service records.</td></tr>';
}

// ==================== CUSTOMER MODULE ====================
async function loadCustomers() {
    try {
        state.customers = await api.getCustomers();
        renderCustomers(state.customers);
        populateCustomerDropdowns();
    } catch (e) {
        showToast('Failed to load customers', 'error');
    }
}

function renderCustomers(list) {
    const tbody = document.querySelector('#customers-table tbody');
    tbody.innerHTML = list.map(c => `
        <tr>
            <td>${c.customerId}</td>
            <td><strong>${c.name}</strong></td>
            <td>${c.phone}</td>
            <td>${c.email}</td>
            <td>${c.address}</td>
            <td><small style="color:#64748b;">${c.createdAt || 'N/A'}</small></td>
            <td>
                <button class="btn btn-secondary btn-sm" onclick="editCustomer(${c.customerId})">Edit</button>
                <button class="btn btn-danger btn-sm" onclick="deleteCustomer(${c.customerId})">Delete</button>
            </td>
        </tr>
    `).join('') || '<tr><td colspan="7" class="text-center">No customers found.</td></tr>';
}

function filterCustomers(query) {
    const q = query.toLowerCase();
    const filtered = state.customers.filter(c => 
        c.name.toLowerCase().includes(q) || 
        c.phone.includes(q) || 
        c.email.toLowerCase().includes(q)
    );
    renderCustomers(filtered);
}

async function handleCustomerSubmit(e) {
    e.preventDefault();
    const id = document.getElementById('cust-id').value;
    const payload = {
        customerId: id,
        name: document.getElementById('cust-name').value.trim(),
        phone: document.getElementById('cust-phone').value.trim(),
        email: document.getElementById('cust-email').value.trim(),
        address: document.getElementById('cust-address').value.trim()
    };

    try {
        if (id) {
            await api.updateCustomer(payload);
            showToast('Customer updated successfully!', 'success');
        } else {
            await api.createCustomer(payload);
            showToast('Customer registered successfully!', 'success');
        }
        closeModal('customer-modal');
        loadCustomers();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

function editCustomer(id) {
    const c = state.customers.find(item => item.customerId === id);
    if (!c) return;
    document.getElementById('cust-id').value = c.customerId;
    document.getElementById('cust-name').value = c.name;
    document.getElementById('cust-phone').value = c.phone;
    document.getElementById('cust-email').value = c.email;
    document.getElementById('cust-address').value = c.address;
    document.getElementById('customer-modal-title').textContent = 'Edit Customer';
    openModal('customer-modal');
}

async function deleteCustomer(id) {
    if (!confirm('Are you sure you want to delete this customer? All their registered vehicles and appointments will also be deleted.')) return;
    try {
        await api.deleteCustomer(id);
        showToast('Customer deleted successfully', 'success');
        loadCustomers();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

// ==================== VEHICLE MODULE ====================
async function loadVehicles() {
    try {
        const [vehicles, customers] = await Promise.all([api.getVehicles(), api.getCustomers()]);
        state.vehicles = vehicles;
        state.customers = customers;
        renderVehicles(state.vehicles);
        populateCustomerDropdowns();
        populateVehicleDropdowns();
    } catch (e) {
        showToast('Failed to load vehicles', 'error');
    }
}

function renderVehicles(list) {
    const tbody = document.querySelector('#vehicles-table tbody');
    tbody.innerHTML = list.map(v => `
        <tr>
            <td>${v.vehicleId}</td>
            <td><strong>${v.registrationNumber}</strong></td>
            <td>${v.model}</td>
            <td><span class="badge badge-type">${v.vehicleType}</span></td>
            <td>${v.manufactureYear}</td>
            <td>${v.customerName || 'N/A'}</td>
            <td>
                <button class="btn btn-secondary btn-sm" onclick="editVehicle(${v.vehicleId})">Edit</button>
                <button class="btn btn-danger btn-sm" onclick="deleteVehicle(${v.vehicleId})">Delete</button>
            </td>
        </tr>
    `).join('') || '<tr><td colspan="7" class="text-center">No vehicles found.</td></tr>';
}

function filterVehicles(query) {
    const q = query.toLowerCase();
    const filtered = state.vehicles.filter(v => 
        v.registrationNumber.toLowerCase().includes(q) || 
        v.model.toLowerCase().includes(q) || 
        (v.customerName && v.customerName.toLowerCase().includes(q))
    );
    renderVehicles(filtered);
}

async function handleVehicleSubmit(e) {
    e.preventDefault();
    const id = document.getElementById('veh-id').value;
    const payload = {
        vehicleId: id,
        customerId: document.getElementById('veh-customer-id').value,
        registrationNumber: document.getElementById('veh-reg').value.trim(),
        model: document.getElementById('veh-model').value.trim(),
        vehicleType: document.getElementById('veh-type').value,
        manufactureYear: document.getElementById('veh-year').value
    };

    try {
        if (id) {
            await api.updateVehicle(payload);
            showToast('Vehicle updated successfully!', 'success');
        } else {
            await api.createVehicle(payload);
            showToast('Vehicle registered successfully!', 'success');
        }
        closeModal('vehicle-modal');
        loadVehicles();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

function editVehicle(id) {
    const v = state.vehicles.find(item => item.vehicleId === id);
    if (!v) return;
    document.getElementById('veh-id').value = v.vehicleId;
    document.getElementById('veh-customer-id').value = v.customerId;
    document.getElementById('veh-reg').value = v.registrationNumber;
    document.getElementById('veh-model').value = v.model;
    document.getElementById('veh-type').value = v.vehicleType;
    document.getElementById('veh-year').value = v.manufactureYear;
    document.getElementById('vehicle-modal-title').textContent = 'Edit Vehicle';
    openModal('vehicle-modal');
}

async function deleteVehicle(id) {
    if (!confirm('Are you sure you want to delete this vehicle?')) return;
    try {
        await api.deleteVehicle(id);
        showToast('Vehicle removed successfully', 'success');
        loadVehicles();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

// ==================== APPOINTMENT MODULE (CONFLICT CHECKING) ====================
async function loadAppointments() {
    try {
        const [appts, custs, vehs] = await Promise.all([
            api.getAppointments(),
            api.getCustomers(),
            api.getVehicles()
        ]);
        state.appointments = appts;
        state.customers = custs;
        state.vehicles = vehs;
        renderAppointments(state.appointments);
        populateCustomerDropdowns();
    } catch (e) {
        showToast('Failed to load appointments', 'error');
    }
}

function renderAppointments(list) {
    const tbody = document.querySelector('#appointments-table tbody');
    tbody.innerHTML = list.map(a => `
        <tr>
            <td>${a.appointmentId}</td>
            <td><strong>${a.appointmentDate}</strong> <small style="color:#64748b;">${a.appointmentTime}</small></td>
            <td><strong>${a.registrationNumber}</strong> <br><small style="color:#64748b;">${a.vehicleModel}</small></td>
            <td>${a.customerName}</td>
            <td>${a.serviceType}</td>
            <td>
                <select class="form-control" style="font-size:0.8rem; padding:4px 8px; width:auto;" onchange="updateAppointmentStatus(${a.appointmentId}, this.value)">
                    <option value="Scheduled" ${a.status === 'Scheduled' ? 'selected' : ''}>Scheduled</option>
                    <option value="In Progress" ${a.status === 'In Progress' ? 'selected' : ''}>In Progress</option>
                    <option value="Completed" ${a.status === 'Completed' ? 'selected' : ''}>Completed</option>
                    <option value="Cancelled" ${a.status === 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                </select>
            </td>
            <td>${a.notes || '—'}</td>
            <td>
                <button class="btn btn-danger btn-sm" onclick="deleteAppointment(${a.appointmentId})">Delete</button>
            </td>
        </tr>
    `).join('') || '<tr><td colspan="8" class="text-center">No appointments found.</td></tr>';
}

function filterAppointments(status) {
    if (status === 'ALL') {
        renderAppointments(state.appointments);
    } else {
        renderAppointments(state.appointments.filter(a => a.status === status));
    }
}

function filterVehiclesForCustomer(customerId) {
    const vehSelect = document.getElementById('appt-vehicle-id');
    vehSelect.innerHTML = '<option value="">Select Vehicle...</option>';
    if (!customerId) return;
    const custVehicles = state.vehicles.filter(v => v.customerId == customerId);
    custVehicles.forEach(v => {
        const opt = document.createElement('option');
        opt.value = v.vehicleId;
        opt.textContent = `${v.model} (${v.registrationNumber})`;
        vehSelect.appendChild(opt);
    });
}

async function handleAppointmentSubmit(e) {
    e.preventDefault();
    const payload = {
        customerId: document.getElementById('appt-customer-id').value,
        vehicleId: document.getElementById('appt-vehicle-id').value,
        appointmentDate: document.getElementById('appt-date').value,
        appointmentTime: document.getElementById('appt-time').value,
        serviceType: document.getElementById('appt-service-type').value,
        notes: document.getElementById('appt-notes').value.trim()
    };

    try {
        await api.scheduleAppointment(payload);
        showToast('Appointment booked successfully!', 'success');
        closeModal('appointment-modal');
        loadAppointments();
    } catch (err) {
        // Displays Conflict Warning
        alert(`SCHEDULING CONFLICT: ${err.message}`);
        showToast(err.message, 'error');
    }
}

async function updateAppointmentStatus(id, newStatus) {
    try {
        await api.updateAppointmentStatus(id, newStatus);
        showToast(`Appointment status updated to ${newStatus}`, 'success');
        loadAppointments();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

async function deleteAppointment(id) {
    if (!confirm('Are you sure you want to delete this appointment?')) return;
    try {
        await api.deleteAppointment(id);
        showToast('Appointment removed', 'success');
        loadAppointments();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

// ==================== MAINTENANCE MODULE ====================
async function loadMaintenance() {
    try {
        const [records, parts, vehicles] = await Promise.all([
            api.getMaintenanceRecords(),
            api.getSpareParts(),
            api.getVehicles()
        ]);
        state.maintenance = records;
        state.spareParts = parts;
        state.vehicles = vehicles;
        renderMaintenance(state.maintenance);
        populatePartsDropdown();
        populateVehicleDropdowns();
    } catch (e) {
        showToast('Failed to load maintenance records', 'error');
    }
}

function renderMaintenance(list) {
    const tbody = document.querySelector('#maintenance-table tbody');
    tbody.innerHTML = list.map(m => `
        <tr>
            <td><strong>#JOB-${m.recordId}</strong></td>
            <td>${m.serviceDate}</td>
            <td><strong>${m.registrationNumber}</strong> <br><small style="color:#64748b;">${m.vehicleModel}</small></td>
            <td>${m.customerName}</td>
            <td>${m.workPerformed}</td>
            <td>₹${formatNumber(m.labourCharges)}</td>
            <td>₹${formatNumber(m.sparePartsCost)}</td>
            <td><span class="badge badge-${m.status.toLowerCase().replace(' ', '-')}">${m.status}</span></td>
            <td>
                <button class="btn btn-primary btn-sm" onclick="viewBillByRecord(${m.recordId})">View Bill</button>
                <button class="btn btn-danger btn-sm" onclick="deleteMaintenance(${m.recordId})">Delete</button>
            </td>
        </tr>
    `).join('') || '<tr><td colspan="9" class="text-center">No maintenance jobs logged.</td></tr>';
}

function updatePartPriceDisplay() {
    calcDraftBill();
}

function calcDraftBill() {
    const labour = parseFloat(document.getElementById('maint-labour').value) || 0;
    const partSelect = document.getElementById('maint-part-select');
    const selectedPartId = partSelect.value;
    const qty = parseInt(document.getElementById('maint-part-qty').value) || 0;

    let partsCost = 0;
    if (selectedPartId) {
        const part = state.spareParts.find(p => p.partId == selectedPartId);
        if (part) {
            partsCost = part.unitPrice * qty;
        }
    }

    const total = labour + partsCost;
    document.getElementById('preview-labour').textContent = formatNumber(labour);
    document.getElementById('preview-parts').textContent = formatNumber(partsCost);
    document.getElementById('preview-total').textContent = formatNumber(total);
}

async function handleMaintenanceSubmit(e) {
    e.preventDefault();
    const payload = {
        vehicleId: document.getElementById('maint-vehicle-id').value,
        serviceDate: document.getElementById('maint-date').value,
        workPerformed: document.getElementById('maint-work').value.trim(),
        labourCharges: document.getElementById('maint-labour').value,
        status: document.getElementById('maint-status').value,
        partId: document.getElementById('maint-part-select').value,
        quantity: document.getElementById('maint-part-qty').value
    };

    try {
        await api.createMaintenanceRecord(payload);
        showToast('Service job saved and invoice generated!', 'success');
        closeModal('maintenance-modal');
        loadMaintenance();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function deleteMaintenance(id) {
    if (!confirm('Are you sure you want to delete this maintenance record and its associated bill?')) return;
    try {
        await api.deleteMaintenanceRecord(id);
        showToast('Service record deleted', 'success');
        loadMaintenance();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

// ==================== BILLING MODULE ====================
async function loadBills() {
    try {
        state.bills = await api.getBills();
        renderBills(state.bills);
    } catch (e) {
        showToast('Failed to load billing records', 'error');
    }
}

function renderBills(list) {
    const tbody = document.querySelector('#billing-table tbody');
    tbody.innerHTML = list.map(b => `
        <tr>
            <td><strong>BILL-${b.billId}</strong></td>
            <td>${b.billingDate}</td>
            <td><strong>${b.registrationNumber}</strong> <br><small style="color:#64748b;">${b.vehicleModel}</small></td>
            <td>${b.customerName}</td>
            <td>₹${formatNumber(b.labourCharges)}</td>
            <td>₹${formatNumber(b.sparePartsCost)}</td>
            <td><strong style="color:var(--primary); font-size:1rem;">₹${formatNumber(b.totalAmount)}</strong></td>
            <td>
                <span class="badge badge-${b.paymentStatus.toLowerCase()}">${b.paymentStatus}</span>
            </td>
            <td>${b.paymentMethod}</td>
            <td>
                <button class="btn btn-secondary btn-sm" onclick="showInvoice(${b.billId})">Invoice</button>
                ${b.paymentStatus !== 'Paid' ? `
                    <button class="btn btn-primary btn-sm" onclick="markBillPaid(${b.billId})">Mark Paid</button>
                ` : ''}
            </td>
        </tr>
    `).join('') || '<tr><td colspan="10" class="text-center">No billing records found.</td></tr>';
}

function filterBills(status) {
    if (status === 'ALL') {
        renderBills(state.bills);
    } else {
        renderBills(state.bills.filter(b => b.paymentStatus === status));
    }
}

async function markBillPaid(billId) {
    const method = prompt('Select Payment Method (Cash, UPI, Credit Card, Debit Card, Net Banking):', 'UPI');
    if (!method) return;
    try {
        await api.updatePaymentStatus(billId, 'Paid', method);
        showToast('Bill marked as Paid!', 'success');
        loadBills();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

async function viewBillByRecord(recordId) {
    try {
        const bill = await api.createBill(recordId);
        showInvoice(bill.billId || bill.data?.billId);
    } catch (e) {
        showToast('Failed to fetch invoice', 'error');
    }
}

async function showInvoice(billId) {
    try {
        const b = await api.getBillById(billId);
        const container = document.getElementById('invoice-content');

        let partsRows = '';
        if (b.itemizedParts && b.itemizedParts.length > 0) {
            partsRows = b.itemizedParts.map(p => `
                <tr>
                    <td>${p.partName || 'Spare Part'}</td>
                    <td class="text-center">${p.quantity}</td>
                    <td class="text-right">₹${formatNumber(p.unitPrice)}</td>
                    <td class="text-right"><strong>₹${formatNumber(p.subtotal)}</strong></td>
                </tr>
            `).join('');
        } else {
            partsRows = '<tr><td colspan="4" style="color:#64748b;">No external spare parts consumed (Labour Only)</td></tr>';
        }

        container.innerHTML = `
            <div class="invoice-header">
                <div>
                    <h2 style="color:var(--primary); font-size:1.6rem; font-weight:800;">AUTOCARE PRO SERVICE CENTER</h2>
                    <p style="color:#64748b; font-size:0.85rem;">Authorized Multi-Brand Automobile Maintenance Hub<br>42 Industrial Area, Bengaluru | Contact: +91 80 2345 6789</p>
                </div>
                <div style="text-align:right;">
                    <h3 style="font-size:1.2rem; font-weight:800;">INVOICE</h3>
                    <p><strong>Invoice #:</strong> BILL-${b.billId}</p>
                    <p><strong>Date:</strong> ${b.billingDate}</p>
                    <p><strong>Status:</strong> <span class="badge badge-${b.paymentStatus.toLowerCase()}">${b.paymentStatus}</span></p>
                </div>
            </div>

            <div style="display:grid; grid-template-columns:1fr 1fr; gap:20px; margin-bottom:24px; padding-bottom:18px; border-bottom:1px solid #e2e8f0;">
                <div>
                    <small style="text-transform:uppercase; color:#64748b; font-weight:700;">Customer Details</small>
                    <h4 style="margin:4px 0 2px 0;">${b.customerName}</h4>
                    <p style="font-size:0.85rem; color:#475569;">${b.customerPhone}</p>
                    <p style="font-size:0.85rem; color:#475569;">${b.customerAddress || 'Customer Address'}</p>
                </div>
                <div>
                    <small style="text-transform:uppercase; color:#64748b; font-weight:700;">Vehicle Information</small>
                    <h4 style="margin:4px 0 2px 0;">${b.vehicleModel}</h4>
                    <p style="font-size:0.85rem; color:#475569;"><strong>Reg No:</strong> ${b.registrationNumber}</p>
                    <p style="font-size:0.85rem; color:#475569;"><strong>Service Job Card:</strong> #JOB-${b.recordId}</p>
                </div>
            </div>

            <div style="margin-bottom:20px;">
                <h4 style="font-size:0.95rem; margin-bottom:6px;">Service Work Performed:</h4>
                <p style="background:#f8fafc; padding:10px 14px; border-radius:6px; font-size:0.88rem; border:1px solid #e2e8f0;">
                    ${b.workPerformed || 'Periodic Service & Maintenance Check'}
                </p>
            </div>

            <table class="data-table" style="margin-bottom:20px;">
                <thead>
                    <tr>
                        <th>Spare Parts Used</th>
                        <th style="text-align:center;">Qty</th>
                        <th style="text-align:right;">Unit Price</th>
                        <th style="text-align:right;">Subtotal</th>
                    </tr>
                </thead>
                <tbody>
                    ${partsRows}
                </tbody>
            </table>

            <div class="invoice-summary">
                <table class="invoice-total-table">
                    <tr>
                        <td>Labour Charges:</td>
                        <td style="text-align:right;"><strong>₹${formatNumber(b.labourCharges)}</strong></td>
                    </tr>
                    <tr>
                        <td>Spare Parts Total:</td>
                        <td style="text-align:right;"><strong>₹${formatNumber(b.sparePartsCost)}</strong></td>
                    </tr>
                    <tr class="invoice-total-row">
                        <td>TOTAL AMOUNT:</td>
                        <td style="text-align:right;">₹${formatNumber(b.totalAmount)}</td>
                    </tr>
                </table>
            </div>

            <div style="margin-top:20px; font-size:0.8rem; color:#94a3b8; text-align:center; border-top:1px dashed #cbd5e1; padding-top:14px;">
                Formula Enforced: <strong>Total Bill = Labour Charges + Spare Parts Costs</strong> (${b.labourCharges} + ${b.sparePartsCost} = ${b.totalAmount})
                <br>Thank you for choosing AutoCare Pro!
            </div>
        `;

        openModal('invoice-modal');
    } catch (e) {
        showToast('Error opening invoice', 'error');
    }
}

// ==================== REPORTS MODULE ====================
async function loadReports() {
    loadServiceHistoryReport();
    loadVehicleCostsReport();
    loadWorkloadReport();
}

async function loadServiceHistoryReport() {
    const vehId = document.getElementById('history-vehicle-filter').value;
    try {
        const history = await api.getServiceHistory(vehId || null);
        const tbody = document.querySelector('#report-history-table tbody');
        tbody.innerHTML = history.map(h => `
            <tr>
                <td>${h.serviceDate}</td>
                <td><strong>${h.registrationNumber}</strong> (${h.model})</td>
                <td>${h.customerName}</td>
                <td>${h.workPerformed}</td>
                <td>₹${formatNumber(h.labourCharges)}</td>
                <td>₹${formatNumber(h.sparePartsCost)}</td>
                <td><strong>₹${formatNumber(h.totalBill)}</strong></td>
                <td><span class="badge badge-${h.paymentStatus.toLowerCase()}">${h.paymentStatus}</span></td>
            </tr>
        `).join('') || '<tr><td colspan="8" class="text-center">No service history found.</td></tr>';
    } catch (e) {
        showToast('Error loading service history report', 'error');
    }
}

async function loadVehicleCostsReport() {
    try {
        const costs = await api.getVehicleCosts();
        const tbody = document.querySelector('#report-costs-table tbody');
        tbody.innerHTML = costs.map(c => `
            <tr>
                <td><strong>${c.registrationNumber}</strong></td>
                <td>${c.model}</td>
                <td><span class="badge badge-type">${c.vehicleType}</span></td>
                <td>${c.ownerName}</td>
                <td style="text-align:center;">${c.totalServices}</td>
                <td>₹${formatNumber(c.totalLabourCost)}</td>
                <td>₹${formatNumber(c.totalPartsCost)}</td>
                <td><strong style="color:var(--primary); font-size:0.95rem;">₹${formatNumber(c.totalMaintenanceExpenditure)}</strong></td>
            </tr>
        `).join('') || '<tr><td colspan="8" class="text-center">No cost records found.</td></tr>';
    } catch (e) {
        showToast('Error loading vehicle costs report', 'error');
    }
}

async function loadWorkloadReport() {
    try {
        const workload = await api.getWorkload();
        
        // Status dist table
        const statusTbody = document.querySelector('#report-status-dist-table tbody');
        statusTbody.innerHTML = (workload.statusDistribution || []).map(s => `
            <tr>
                <td><span class="badge badge-${s.status.toLowerCase().replace(' ', '-')}">${s.status}</span></td>
                <td><strong>${s.count}</strong> appointments</td>
            </tr>
        `).join('') || '<tr><td colspan="2">No data</td></tr>';

        // Daily workload table
        const dailyTbody = document.querySelector('#report-daily-workload-table tbody');
        dailyTbody.innerHTML = (workload.dailyWorkload || []).map(d => `
            <tr>
                <td><strong>${d.date}</strong></td>
                <td>${d.appointments} appointments scheduled</td>
            </tr>
        `).join('') || '<tr><td colspan="2">No data</td></tr>';
    } catch (e) {
        showToast('Error loading workload report', 'error');
    }
}

function switchReportTab(tabId) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.report-tab-pane').forEach(pane => pane.classList.remove('active'));

    event.target.classList.add('active');
    document.getElementById(tabId).classList.add('active');
}

// ==================== DROPDOWN POPULATORS ====================
function populateCustomerDropdowns() {
    const selects = [document.getElementById('veh-customer-id'), document.getElementById('appt-customer-id')];
    selects.forEach(select => {
        if (!select) return;
        const currentVal = select.value;
        select.innerHTML = '<option value="">Select Customer...</option>';
        state.customers.forEach(c => {
            const opt = document.createElement('option');
            opt.value = c.customerId;
            opt.textContent = `${c.name} (${c.phone})`;
            select.appendChild(opt);
        });
        select.value = currentVal;
    });
}

function populateVehicleDropdowns() {
    const selects = [document.getElementById('maint-vehicle-id'), document.getElementById('history-vehicle-filter')];
    selects.forEach(select => {
        if (!select) return;
        const isFilter = select.id === 'history-vehicle-filter';
        select.innerHTML = isFilter ? '<option value="">All Vehicles</option>' : '<option value="">Select Vehicle...</option>';
        state.vehicles.forEach(v => {
            const opt = document.createElement('option');
            opt.value = v.vehicleId;
            opt.textContent = `${v.registrationNumber} - ${v.model} (${v.customerName || 'Owner'})`;
            select.appendChild(opt);
        });
    });
}

function populatePartsDropdown() {
    const select = document.getElementById('maint-part-select');
    if (!select) return;
    select.innerHTML = '<option value="">None (Labour Only)</option>';
    state.spareParts.forEach(p => {
        const opt = document.createElement('option');
        opt.value = p.partId;
        opt.textContent = `${p.partName} - ₹${p.unitPrice} (Stock: ${p.stockQuantity})`;
        select.appendChild(opt);
    });
}

// ==================== MODAL UTILITIES ====================
function openModal(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.classList.add('show');
        if (id === 'maintenance-modal') {
            document.getElementById('maint-date').value = new Date().toISOString().split('T')[0];
            calcDraftBill();
        } else if (id === 'appointment-modal') {
            document.getElementById('appt-date').value = new Date().toISOString().split('T')[0];
        }
    }
}

function closeModal(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.remove('show');
}

// ==================== TOAST & FORMATTING ====================
function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 4000);
}

function formatNumber(num) {
    return Number(num || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function truncate(str, len) {
    if (!str) return '';
    return str.length > len ? str.substring(0, len) + '...' : str;
}
