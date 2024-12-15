package com.lapangin.web.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.lapangin.web.model.Lapangan;
import com.lapangin.web.model.User;
import com.lapangin.web.service.LapanganService;
import com.lapangin.web.service.UserService;

@Controller
public class HomeController {

    private final LapanganService lapanganService;
    private final UserService userService;

    // Constructor Injection
    public HomeController(LapanganService lapanganService, UserService userService) {
        this.lapanganService = lapanganService;
        this.userService = userService;
    }

    // Mapping untuk Dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<Lapangan> listLapangan = lapanganService.getAllLapangan();
        model.addAttribute("listLapangan", listLapangan);
        return "dashboard";
    }

    // Mapping untuk Detail Lapangan
    @GetMapping("/lapangan/detail/{id}")
    public String showLapanganDetail(@PathVariable("id") Long id, Model model) {
        Lapangan lapangan = lapanganService.getLapanganById(id);
        if (lapangan == null) {
            return "redirect:/error"; // Redirect ke halaman error jika tidak ditemukan
        }
        model.addAttribute("lapangan", lapangan);
        return "lapangan_detail";
    }

    // Mapping untuk Calendar
    @GetMapping("/calendar")
    public String showCalendar(Model model) {
        // Tambahkan logika jika diperlukan
        return "calendar"; // Pastikan ada calendar.html di templates
    }

    // Mapping untuk Notifications
    @GetMapping("/notifications")
    public String showNotifications(Model model) {
        // Tambahkan logika jika diperlukan
        return "notifications"; // Pastikan ada notifications.html di templates
    }

    // Mapping untuk History
    @GetMapping("/history")
    public String showHistory(Model model) {
        // Implementasi logika untuk menampilkan riwayat pemesanan
        return "history"; // Pastikan ada history.html di templates
    }

    // Mapping untuk Wallets
    @GetMapping("/wallet")
    public String showWallet(Model model) {
        // Tambahkan logika jika diperlukan
        return "wallet"; 
    }

    // Mapping untuk Profile
    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login"; // Redirect jika pengguna belum login
        }

        User user = userService.findByUsername(principal.getName());
        model.addAttribute("pageTitle", "Profil Pengguna");
        model.addAttribute("user", user);
        return "profile";
    }   

    // Mapping untuk Error
    @GetMapping("/error")
    public String showErrorPage(Model model) {
        // Tambahkan logika jika diperlukan, misalnya pesan error
        return "error"; // Pastikan ada error.html di templates
    }
}