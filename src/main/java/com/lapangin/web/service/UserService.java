package com.lapangin.web.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lapangin.web.model.User;
import com.lapangin.web.repository.CustomerRepository;
import com.lapangin.web.repository.UserRepository;
import com.lapangin.web.security.UserPrincipal;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public UserService(UserRepository userRepository, CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            throw new UsernameNotFoundException("User tidak ditemukan: " + username);
        }
        return UserPrincipal.create(userOpt.get());
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User tidak ditemukan: " + username));
    }

    public void save(User user) {
        userRepository.save(user);
    }

    // Cek apakah email sudah digunakan
    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Cek apakah phoneNumber sudah digunakan
    public boolean isPhoneNumberTaken(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    // Cek apakah name sudah digunakan (jika diperlukan)
    public boolean isNameTaken(String name) {
        return userRepository.findByName(name).isPresent();
    }
}
