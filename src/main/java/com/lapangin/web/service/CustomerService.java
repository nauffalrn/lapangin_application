package com.lapangin.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lapangin.web.model.Customer;
import com.lapangin.web.repository.CustomerRepository;

import jakarta.transaction.Transactional;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(Customer customer) {
        logger.debug("Registering customer: {}", customer.getUsername());

        String username = customer.getUsername();
        if (findByUsername(username) != null) {
            logger.warn("Username '{}' already taken", username);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already taken");
        }

        // Validasi kecocokan password dan confirmPassword
        if (!customer.getPassword().equals(customer.getConfirmPassword())) {
            logger.warn("Password dan konfirmasi password tidak cocok untuk username '{}'", username);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password dan konfirmasi password tidak cocok");
        }

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        logger.debug("Saving customer to repository");
        customerRepository.save(customer);
        logger.info("Customer saved to database: {}", customer.getUsername());
    }

    public Customer findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    public boolean validatePassword(Customer customer, String password) {
        if (customer == null) {
            logger.warn("Customer object is null");
            return false;
        }

        boolean isPasswordValid = passwordEncoder.matches(password, customer.getPassword());
        if (!isPasswordValid) {
            logger.warn("Invalid password for username '{}'", customer.getUsername());
        }
        return isPasswordValid;
    }
}
