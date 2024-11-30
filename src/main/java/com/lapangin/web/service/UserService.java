package com.lapangin.web.service;

import com.lapangin.web.repository.AdminRepository;
import com.lapangin.web.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public boolean isUsernameTaken(String username) {
        return adminRepository.findByUsername(username) != null || customerRepository.findByUsername(username) != null;
    }
}
