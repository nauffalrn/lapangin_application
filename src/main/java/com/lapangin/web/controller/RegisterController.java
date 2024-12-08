package com.lapangin.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lapangin.web.model.Customer;
import com.lapangin.web.service.CustomerService;

import jakarta.validation.Valid;

@Controller
public class RegisterController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private CustomerService customerService;

    @GetMapping("/register")
    public String showCustomerRegistrationForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }

    @PostMapping("/register")
    public String registerCustomer(
            @Valid @ModelAttribute("customer") Customer customer,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        logger.debug("Customer email: {}", customer.getEmail());

        if (result.hasErrors()) {
            logger.debug("Validation errors occurred during registration");
            return "register"; // Kembali ke form registrasi jika ada error
        }

        try {
            customerService.register(customer);
            redirectAttributes.addFlashAttribute("success", "Registration successful!");
            return "redirect:/login"; // Pastikan redirect ini berjalan
        } catch (ResponseStatusException e) {
            logger.error("Error during registration: {}", e.getMessage());
            result.rejectValue("username", "error.customer", e.getReason());
            return "register";
        } catch (Exception e) {
            logger.error("Unexpected error during registration", e);
            result.reject("registrationError", "An unexpected error occurred.");
            return "register";
        }
    }
}