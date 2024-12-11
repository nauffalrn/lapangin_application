package com.lapangin.web.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lapangin.web.model.Admin;
import com.lapangin.web.repository.AdminRepository;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Menyimpan data admin baru dengan validasi username
    public Admin register(Admin admin) {
        if (isUsernameTaken(admin.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        // Hash password sebelum disimpan
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    // Mengecek apakah username sudah ada
    public boolean isUsernameTaken(String username) {
        return adminRepository.findByUsername(username) != null;
    }
}
