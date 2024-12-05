package com.lapangin.web.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    // Rename the mapping for the main page to match the physical HTML page
    @GetMapping("/dashboard")
    public String showHomePage(Model model) {
        String greeting = "Welcome to the Dashboard!";
        model.addAttribute("greeting", greeting);
        return "dashboard"; // Render dashboard.html
    }

    // Halaman pemesanan lapangan
    @GetMapping("/booking")
    public String showBookingPage(Model model) {
        return "booking"; // Menampilkan halaman booking lapangan
    }

    // Proses pemesanan lapangan
    @PostMapping("/booking")
    public String processBooking(@RequestParam String lapangan, @RequestParam String waktu) {
        // Proses pemesanan lapangan
        return "redirect:/dashboard"; // Redirect to dashboard after booking
    }

    // Halaman riwayat pemesanan
    @GetMapping("/history")
    public String showHistory(Model model) {
        return "history"; // Menampilkan halaman riwayat pemesanan
    }
}