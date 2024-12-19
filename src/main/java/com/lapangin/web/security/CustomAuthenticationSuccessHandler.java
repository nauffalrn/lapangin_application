package com.lapangin.web.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.lapangin.web.model.User;
import com.lapangin.web.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect("/admin"); // Pastikan ada mapping untuk /admin
        } else {
            response.sendRedirect("/dashboard?promo=true"); // Sesuaikan dengan controller mapping
        }
    }
}
