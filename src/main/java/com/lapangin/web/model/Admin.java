package com.lapangin.web.model;

import jakarta.persistence.*;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "admin")
public class Admin extends User {
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> privileges; // Privilege khusus admin, misalnya "CREATE", "DELETE"

    private String role; // Misal: "SUPER_ADMIN" atau "ADMIN"

    // Getter & Setter
    public List<String> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public class PasswordEncoderUtil {
        private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        public static String encodePassword(String rawPassword) {
            return encoder.encode(rawPassword);
        }

        public static boolean matches(String rawPassword, String encodedPassword) {
            return encoder.matches(rawPassword, encodedPassword);
        }
    }
}
