package com.lapangin.web.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/admin/lapangan/{id}")
    @ResponseBody
    public ResponseEntity<Lapangan> getLapanganById(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.findByUsername(principal.getName());
        if (user == null || !user.getRole().equalsIgnoreCase("ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Lapangan lapangan = lapanganService.getLapanganById(id);
        if (lapangan == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(lapangan);
    }

    @PostMapping("/admin/lapangan/add")
    @ResponseBody
    public ResponseEntity<String> addLapangan(
            @RequestParam("namaLapangan") String namaLapangan,
            @RequestParam("city") String city,
            @RequestParam("cabangOlahraga") String cabangOlahraga,
            @RequestParam("alamatLapangan") String alamatLapangan,
            @RequestParam("price") int price,
            @RequestParam("rating") double rating,
            @RequestParam("reviews") int reviews,
            @RequestParam("jamBuka") String jamBuka,
            @RequestParam("jamTutup") String jamTutup,
            @RequestParam("fieldFacilities") List<String> fieldFacilities,
            @RequestParam("image") MultipartFile image,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized.");
        }

        User user = userService.findByUsername(principal.getName());
        if (user == null || !user.getRole().equalsIgnoreCase("ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized.");
        }

        try {
            // Simpan gambar ke direktori tertentu
            String fileName = StringUtils.cleanPath(image.getOriginalFilename());
            String uploadDir = "src/main/resources/static/images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath);

            // Buat objek Lapangan baru
            Lapangan lapangan = new Lapangan();
            lapangan.setNamaLapangan(namaLapangan);
            lapangan.setCity(city);
            lapangan.setCabangOlahraga(cabangOlahraga);
            lapangan.setAlamatLapangan(alamatLapangan);
            lapangan.setPrice(price);
            lapangan.setRating(rating);
            lapangan.setReviews(reviews);
            lapangan.setFacilities(String.join(", ", fieldFacilities));
            lapangan.setImage(fileName);

            // Parsing jam buka dan tutup
            LocalTime buka = LocalTime.parse(jamBuka);
            LocalTime tutup = LocalTime.parse(jamTutup);
            lapangan.setJamBuka(buka);
            lapangan.setJamTutup(tutup);

            // Simpan ke database
            lapanganService.saveLapangan(lapangan);

            return ResponseEntity.ok("Lapangan berhasil ditambahkan.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal menambahkan lapangan.");
        }
    }

    @PostMapping("/admin/lapangan/edit")
    @ResponseBody
    public ResponseEntity<String> editLapangan(
            @RequestParam("id") Long id,
            @RequestParam("namaLapangan") String namaLapangan,
            @RequestParam("city") String city,
            @RequestParam("cabangOlahraga") String cabangOlahraga,
            @RequestParam("alamatLapangan") String alamatLapangan,
            @RequestParam("price") int price,
            @RequestParam("rating") double rating,
            @RequestParam("reviews") int reviews,
            @RequestParam("jamBuka") String jamBuka,
            @RequestParam("jamTutup") String jamTutup,
            @RequestParam("fieldFacilities") List<String> fieldFacilities,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized.");
        }

        User user = userService.findByUsername(principal.getName());
        if (user == null || !user.getRole().equalsIgnoreCase("ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized.");
        }

        try {
            Lapangan lapangan = lapanganService.getLapanganById(id);
            if (lapangan == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lapangan tidak ditemukan.");
            }

            // Update gambar jika ada
            if (image != null && !image.isEmpty()) {
                String fileName = StringUtils.cleanPath(image.getOriginalFilename());
                String uploadDir = "src/main/resources/static/images/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(image.getInputStream(), filePath);
                lapangan.setImage(fileName);
            }

            // Update data lapangan
            lapangan.setNamaLapangan(namaLapangan);
            lapangan.setCity(city);
            lapangan.setCabangOlahraga(cabangOlahraga);
            lapangan.setAlamatLapangan(alamatLapangan);
            lapangan.setPrice(price);
            lapangan.setRating(rating);
            lapangan.setReviews(reviews);
            lapangan.setFacilities(String.join(", ", fieldFacilities));

            // Parsing jam buka dan tutup
            LocalTime buka = LocalTime.parse(jamBuka);
            LocalTime tutup = LocalTime.parse(jamTutup);
            lapangan.setJamBuka(buka);
            lapangan.setJamTutup(tutup);

            // Simpan perubahan ke database
            lapanganService.saveLapangan(lapangan);

            return ResponseEntity.ok("Lapangan berhasil diperbarui.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal memperbarui lapangan.");
        }
    }

    @PostMapping("/admin/lapangan/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteLapangan(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized.");
        }

        User user = userService.findByUsername(principal.getName());
        if (user == null || !user.getRole().equalsIgnoreCase("ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized.");
        }

        try {
            lapanganService.deleteLapangan(id);
            return ResponseEntity.ok("Lapangan berhasil dihapus.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal menghapus lapangan.");
        }
    }
}
