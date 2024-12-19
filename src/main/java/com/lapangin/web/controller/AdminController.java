package com.lapangin.web.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.lapangin.web.model.Lapangan;
import com.lapangin.web.model.User;
import com.lapangin.web.service.CustomerService;
import com.lapangin.web.service.LapanganService;
import com.lapangin.web.service.UserService;

@Controller
public class AdminController {

    private final UserService userService;
    private final CustomerService customerService;
    private final LapanganService lapanganService;

    public AdminController(UserService userService, CustomerService customerService, LapanganService lapanganService) {
        this.userService = userService;
        this.customerService = customerService;
        this.lapanganService = lapanganService;
    }

    @GetMapping("/admin")
    public String showAdminDashboard(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName());
        if (user == null || !user.getRole().equalsIgnoreCase("ADMIN")) {
            return "redirect:/error";
        }

        long totalCustomers = customerService.countCustomers();
        long totalLapangan = lapanganService.countLapangan();
        List<Lapangan> listLapangan = lapanganService.getAllLapangan();

        model.addAttribute("pageTitle", "Admin Dashboard");
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalLapangan", totalLapangan);
        model.addAttribute("listLapangan", listLapangan);

        return "admin"; // Mengarahkan ke admin.html di templates
    }

    @GetMapping("/dashboardAdmin")
    public String showDashboardAdmin(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName());
        if (user == null || !user.getRole().equalsIgnoreCase("ADMIN")) {
            return "redirect:/error";
        }

        long totalCustomers = customerService.countCustomers();
        long totalLapangan = lapanganService.countLapangan();
        List<Lapangan> listLapangan = lapanganService.getAllLapangan();

        model.addAttribute("pageTitle", "Dashboard Admin");
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalLapangan", totalLapangan);
        model.addAttribute("listLapangan", listLapangan);

        return "dashboardAdmin"; // Mengarahkan ke dashboardAdmin.html di templates
    }

    // Metode untuk Menambahkan Lapangan tanpa DTO
    @PostMapping("/admin/lapangan/add")
    @ResponseBody
    public ResponseEntity<String> addLapangan(@RequestBody Lapangan lapangan, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized.");
        }

        User user = userService.findByUsername(principal.getName());
        if (user == null || !user.getRole().equalsIgnoreCase("ADMIN")) {
            return ResponseEntity.status(401).body("Unauthorized.");
        }

        try {
            // Misalnya, menyimpan fasilitas sebagai string terpisah dengan koma
            List<String> facilities = lapangan.getFieldFacilitiesAsList(); // Implementasikan metode ini di Lapangan
            if (facilities != null && !facilities.isEmpty()) {
                lapangan.setFacilities(String.join(", ", facilities));
            }

            lapanganService.saveLapangan(lapangan);
            return ResponseEntity.ok("Lapangan berhasil ditambahkan.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Gagal menambahkan lapangan.");
        }
    }
}
