package com.vehicle.service;

import com.vehicle.dao.CustomerDAO;
import com.vehicle.model.Customer;
import java.util.List;

/**
 * Service handling customer business logic and validation.
 */
public class CustomerService {
    private final CustomerDAO customerDAO = new CustomerDAO();

    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    public Customer getCustomerById(int id) {
        if (id <= 0) throw new IllegalArgumentException("Invalid customer ID");
        return customerDAO.getCustomerById(id);
    }

    public boolean addCustomer(Customer customer) {
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        if (customer.getPhone() == null || customer.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer phone number is required.");
        }
        return customerDAO.addCustomer(customer);
    }

    public boolean updateCustomer(Customer customer) {
        if (customer.getCustomerId() <= 0) {
            throw new IllegalArgumentException("Invalid customer ID for update.");
        }
        return customerDAO.updateCustomer(customer);
    }

    public boolean deleteCustomer(int id) {
        if (id <= 0) throw new IllegalArgumentException("Invalid customer ID");
        return customerDAO.deleteCustomer(id);
    }
}
