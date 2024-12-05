package com.lapangin.web.controller;

import com.lapangin.web.model.Customer;
import com.lapangin.web.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private CustomerService customerService;

    // Menyediakan objek Customer secara global untuk form
    @ModelAttribute("customer")
    public Customer customer() {
        return new Customer();
    }

    // Menampilkan halaman login
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Mengarahkan ke template login.html
    }

    // Memproses data login
    @PostMapping("/login")
    public String login(@ModelAttribute("customer") Customer customer, RedirectAttributes redirectAttributes) {
        String username = customer.getUsername();
        String password = customer.getPassword();

        Customer existingCustomer = customerService.findByUsername(username);

        if (existingCustomer == null) {
            // Akun tidak ditemukan
            redirectAttributes.addFlashAttribute("errorMessage", "Akun tidak ditemukan. Silakan signup terlebih dahulu.");
            return "redirect:/login";
        }

        if (!customerService.validatePassword(existingCustomer, password)) {
            // Password salah
            redirectAttributes.addFlashAttribute("errorMessage", "Username atau password tidak valid.");
            return "redirect:/login";
        }

        // Login berhasil
        return "redirect:/dashboard";
    }
}
