package com.lapangin.web.service;

import com.lapangin.web.model.Admin;
import com.lapangin.web.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
