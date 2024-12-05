package com.lapangin.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lapangin.web.model.Customer;
import com.lapangin.web.service.CustomerService;

@Controller
public class RegisterController {

    @Autowired
    private CustomerService customerService;

    // Menampilkan halaman registrasi
    @GetMapping("/register")
    public String showCustomerRegistrationForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "register"; // Mengarahkan ke template register.html
    }

    // Memproses data registrasi
    @PostMapping("/register")
    public String registerCustomer(
            @ModelAttribute("customer") Customer customer,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        // Validasi password dan confirm password
        if (!customer.getPassword().equals(customer.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            return "redirect:/register";
        }

        try {
            // Simpan data customer jika validasi berhasil
            customerService.register(customer);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            // Tangkap error jika terjadi kesalahan pada service
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }
}
