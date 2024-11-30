package com.lapangin.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lapangin.web.model.Customer;
import com.lapangin.web.repository.CustomerRepository;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(Customer customer) {
        if (isUsernameTaken(customer.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);
        logger.info("Customer registered successfully: {}", customer.getUsername());
    }

    public boolean isUsernameTaken(String username) {
        return customerRepository.findByUsername(username) != null;
    }

    public boolean validateCustomer(String username, String password) {
        Customer customer = customerRepository.findByUsername(username);
        if (customer != null && passwordEncoder.matches(password, customer.getPassword())) {
            return true;
        }
        return false;
    }
}