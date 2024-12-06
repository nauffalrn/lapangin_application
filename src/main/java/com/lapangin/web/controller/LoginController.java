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

    // Menampilkan halaman login
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("customer", new Customer()); // Objek Customer untuk form
        return "login"; // Mengarahkan ke template login.html
    }

    // Memproses data login
    @PostMapping("/login")
    public String login(@ModelAttribute("customer") Customer customer, RedirectAttributes redirectAttributes) {
        String username = customer.getUsername();
        String password = customer.getPassword();

        // Validasi login
        Customer existingCustomer = customerService.findByUsername(username);
        if (existingCustomer == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Akun tidak ditemukan, silakan Signup terlebih dahulu");
            return "login"; // Redirect jika akun tidak ditemukan
        }

        if (!customerService.validatePassword(existingCustomer, password)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password salah, silakan coba lagi");
            return "login"; // Redirect jika password salah
        }

        // Jika login berhasil, arahkan ke dashboard
        return "redirect:/dashboard";
    }
}
