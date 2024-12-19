package com.lapangin.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lapangin.web.model.Customer;
import com.lapangin.web.model.Promo;
import com.lapangin.web.repository.CustomerRepository;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(Customer customer) {
        // Encode password
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);

        // Optional: Log proses registrasi
        logger.info("Mendaftarkan customer dengan username: {}", customer.getUsername());

        // Simpan customer ke database
        customerRepository.save(customer);

        logger.info("Customer '{}' berhasil disimpan ke database.", customer.getUsername());
    }

    @Transactional(readOnly = true)
    public Customer findByUsername(String username) {
        return customerRepository.findByUsername(username).orElse(null);
    }

    @Transactional
    public void claimPromo(Customer customer, Promo promo) {
        if (promo != null) {
            logger.info("Customer '{}' mengklaim promo '{}'", customer.getUsername(), promo.getKodePromo());
            customer.getClaimedPromos().add(promo);
            customerRepository.save(customer);
            logger.info("Promo '{}' berhasil diklaim oleh customer '{}'", promo.getKodePromo(), customer.getUsername());
        } else {
            logger.error("Promo tidak boleh null saat mengklaim promo.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Promo tidak boleh null");
        }
    }

    @Transactional(readOnly = true)
    public boolean isPromoClaimedByCustomer(Customer customer, Promo promo) {
        return customerRepository.hasClaimedPromo(customer.getId(), promo.getId());
    }

    @Transactional
    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public long countCustomers() {
        return customerRepository.count();
    }
}