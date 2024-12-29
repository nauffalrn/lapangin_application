package com.lapangin.web.controller;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.lapangin.web.model.Booking;
import com.lapangin.web.model.Customer;
import com.lapangin.web.model.Lapangan;
import com.lapangin.web.model.User;
import com.lapangin.web.service.BookingService;
import com.lapangin.web.service.CustomerService;
import com.lapangin.web.service.LapanganService;
import com.lapangin.web.service.UserService;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private final LapanganService lapanganService;
    private final UserService userService;
    private final CustomerService customerService;
    private final BookingService bookingService;

    // Constructor Injection
    public HomeController(LapanganService lapanganService, UserService userService, CustomerService customerService, BookingService bookingService) {
        this.lapanganService = lapanganService;
        this.userService = userService;
        this.customerService = customerService;
        this.bookingService = bookingService;
    }

    // Mapping untuk Dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        // Ambil daftar lapangan dari service
        List<Lapangan> listLapangan = lapanganService.getAllLapangan();
        model.addAttribute("listLapangan", listLapangan);

        // Tambahkan atribut lainnya jika diperlukan
        model.addAttribute("username", principal.getName());

        return "dashboard"; // Mengarahkan ke dashboard.html di templates
    }

    // Mapping untuk Detail Lapangan
    @GetMapping("/lapangan/detail/{id}")
    public String showLapanganDetail(@PathVariable("id") Long id, Model model) {
        Lapangan lapangan = lapanganService.getLapanganById(id);
        if (lapangan == null) {
            logger.warn("Lapangan dengan ID {} tidak ditemukan.", id);
            return "redirect:/error"; // Redirect ke halaman error jika tidak ditemukan
        }
        logger.info("Menampilkan detail Lapangan: {}", lapangan.getNamaLapangan());
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
    public String showNotifications(Model model, Principal principal) {
        try {
            if (principal == null) {
                return "redirect:/login";
            }
            
            Customer customer = customerService.findByUsername(principal.getName());
            if (customer == null) {
                return "redirect:/login";
            }
            
            List<Booking> bookings = bookingService.getUpcomingBookings(customer);
            model.addAttribute("bookings", bookings);
            
            return "notifications";
        } catch (Exception e) {
            logger.error("Error saat mengambil notifikasi", e);
            return "error";
        }
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
        if (user == null) {
            return "redirect:/error"; // Redirect jika user tidak ditemukan
        }

        model.addAttribute("pageTitle", "Profil Pengguna");
        model.addAttribute("user", user);

        if ("CUSTOMER".equals(user.getRole())) {
            Customer customer = customerService.findByUser(user);
            model.addAttribute("phoneNumber", (customer != null) ? customer.getPhoneNumber() : "");
        }

        return "profile";
    }

    // Mapping untuk Error
    @GetMapping("/error")
    public String showErrorPage(Model model) {
        // Tambahkan logika jika diperlukan, misalnya pesan error
        return "error"; // Pastikan ada error.html di templates
    }
}