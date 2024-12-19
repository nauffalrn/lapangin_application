package com.lapangin.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lapangin.web.model.Customer;
import com.lapangin.web.service.CustomerService;
import com.lapangin.web.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final CustomerService customerService;
    private final UserService userService;

    @Autowired
    public AuthController(CustomerService customerService, UserService userService) {
        this.customerService = customerService;
        this.userService = userService;
    }
    
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    // Mapping untuk Login
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "login";
    }

    // Mapping untuk Register
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }

    @PostMapping("/register")
    public String registerCustomer(
            @Valid Customer customer,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        // Cek validasi form
        if (result.hasErrors()) {
            return "register";
        }

        // Cek apakah username sudah digunakan
        if (userService.isUsernameTaken(customer.getUsername())) {
            result.rejectValue("username", "error.customer", "Username sudah digunakan.");
            return "register";
        }

        try {
            // Registrasi customer
            customerService.register(customer);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Error during registration for username: {}", customer.getUsername(), e);
            result.reject("registrationError", "Pendaftaran gagal. Silakan coba lagi.");
            return "register";
        }
    }
}