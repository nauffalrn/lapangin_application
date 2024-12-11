package com.lapangin.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.lapangin.web.model.Customer;
import com.lapangin.web.service.CustomerService;

@Controller
public class LoginController {

    private final CustomerService customerService;

    public LoginController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Menampilkan halaman login
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("customer", new Customer()); // Objek Customer untuk form
        return "login"; // Mengarahkan ke template login.html
    }
}
