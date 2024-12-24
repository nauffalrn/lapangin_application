package com.lapangin.web.controller;

import java.io.IOException;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lapangin.web.model.Customer;
import com.lapangin.web.model.User;
import com.lapangin.web.service.CustomerService;
import com.lapangin.web.service.UserService;
import com.lapangin.web.util.FileUploadUtil;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final CustomerService customerService;
    private final UserService userService;

    @Autowired
    public AuthController(CustomerService customerService, UserService userService) {
        this.customerService = customerService;
        this.userService = userService;
    }
    
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    // Mapping untuk Login
    @GetMapping("/login")
    public String showLoginForm(Model model, @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("customer", new Customer());
        if (error != null) {
            model.addAttribute("errorMessage", "Username atau Password salah.");
        }
        return "login";
    }

    // Mapping untuk Register
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }

    @PostMapping("/register")
    public String registerCustomer(
            @Valid Customer customer,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        logger.info("Starting registration for username: {}", customer.getUsername());

        // Cek validasi yang otomatis dari anotasi @Valid
        if (result.hasErrors()) {
            logger.warn("Validation errors found: {}", result.getAllErrors());
            return "register";
        }

        // Validasi konfirmasi password
        logger.info("Validating password and confirm password");
        if (!customer.getPassword().equals(customer.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.customer", "Passwords do not match.");
            logger.warn("Passwords do not match for user: {}", customer.getUsername());
        }

        // Validasi apakah nama sudah digunakan
        logger.info("Checking if name is taken");
        if (userService.isNameTaken(customer.getName())) {
            result.rejectValue("name", "error.customer", "Nama sudah digunakan.");
            logger.warn("Name {} is already taken.", customer.getName());
        }

        // Validasi apakah username sudah digunakan
        logger.info("Checking if username is taken");
        if (userService.isUsernameTaken(customer.getUsername())) {
            result.rejectValue("username", "error.customer", "Username sudah digunakan.");
            logger.warn("Username {} is already taken.", customer.getUsername());
        }

        // Validasi apakah email sudah digunakan
        logger.info("Checking if email is taken");
        if (userService.isEmailTaken(customer.getEmail())) {
            result.rejectValue("email", "error.customer", "Email sudah digunakan.");
            logger.warn("Email {} is already taken.", customer.getEmail());
        }

        // Validasi apakah nomor telepon sudah digunakan
        logger.info("Checking if phone number is taken");
        if (userService.isPhoneNumberTaken(customer.getPhoneNumber())) {
            result.rejectValue("phoneNumber", "error.customer", "Nomor Telepon sudah digunakan.");
            logger.warn("Phone number {} is already taken.", customer.getPhoneNumber());
        }

        // Setelah semua validasi tambahan, cek kembali apakah ada error
        if (result.hasErrors()) {
            logger.warn("After additional validations, errors found: {}", result.getAllErrors());
            return "register";
        }

        try {
            // Registrasi customer
            logger.info("Registering customer: {}", customer.getUsername());
            customerService.register(customer);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            logger.info("Registration successful for user: {}", customer.getUsername());
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Error during registration for username: {}", customer.getUsername(), e);
            result.reject("registrationError", "Pendaftaran gagal. Silakan coba lagi.");
            return "register";
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam("profileImage") MultipartFile profileImage,
            Model model,
            RedirectAttributes redirectAttributes,
            Principal principal
    ) {
        try {
            User user = userService.findByUsername(principal.getName());

            // Pastikan hanya CUSTOMER yang dapat memperbarui profil
            if (!"CUSTOMER".equals(user.getRole())) {
                model.addAttribute("errorMessage", "Anda tidak memiliki izin untuk memperbarui profil.");
                model.addAttribute("user", user); // Tambahkan user ke model
                return "profile";
            }

            // Update User fields
            user.setUsername(username);
            user.setEmail(email);

            // Handle profile image upload
            if (!profileImage.isEmpty()) {
                String fileName = StringUtils.cleanPath(profileImage.getOriginalFilename());
                String uploadDir = "user-photos/" + user.getId();
                FileUploadUtil.saveFile(uploadDir, fileName, profileImage);
                user.setProfileImage(uploadDir + "/" + fileName);
            }

            userService.save(user);

            // Update Customer's phone number
            Customer customer = customerService.findByUser(user);
            if (customer != null) {
                customer.setPhoneNumber(phone);
                customerService.save(customer);
            }

            redirectAttributes.addFlashAttribute("success", "Profil berhasil diperbarui.");
            return "redirect:/profile";
        } catch (IOException e) {
            logger.error("Gagal mengunggah gambar profil: {}", e.getMessage());
            model.addAttribute("errorMessage", "Gagal mengunggah gambar profil.");
            User user = userService.findByUsername(principal.getName());
            model.addAttribute("user", user); // Tambahkan user ke model

            // Tambahkan phoneNumber jika pengguna adalah CUSTOMER
            if ("CUSTOMER".equals(user.getRole())) {
                Customer customer = customerService.findByUser(user);
                model.addAttribute("phoneNumber", (customer != null) ? customer.getPhoneNumber() : "");
            }

            return "profile";
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat memperbarui profil: {}", e.getMessage());
            model.addAttribute("errorMessage", "Terjadi kesalahan saat memperbarui profil.");
            User user = userService.findByUsername(principal.getName());
            model.addAttribute("user", user); // Tambahkan user ke model

            // Tambahkan phoneNumber jika pengguna adalah CUSTOMER
            if ("CUSTOMER".equals(user.getRole())) {
                Customer customer = customerService.findByUser(user);
                model.addAttribute("phoneNumber", (customer != null) ? customer.getPhoneNumber() : "");
            }

            return "profile";
        }
    }
}