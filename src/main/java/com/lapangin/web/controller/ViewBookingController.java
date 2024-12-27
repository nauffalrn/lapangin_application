package com.lapangin.web.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.lapangin.web.model.Booking;
import com.lapangin.web.service.BookingService;
import com.lapangin.web.service.CustomerService;

@Controller
public class ViewBookingController {

    private final BookingService bookingService;
    private final CustomerService customerService;

    @Autowired
    public ViewBookingController(BookingService bookingService,
                                 CustomerService customerService) {
        this.bookingService = bookingService;
        this.customerService = customerService;
    }

    @GetMapping("/booking/payment/{bookingId}")
    public String showPayment(@PathVariable Long bookingId, Model model, Principal principal) {
        if (principal == null || principal.getName() == null) {
            return "redirect:/login?error=Unauthorized";
        }

        Booking booking = bookingService.getBookingById(bookingId);
        if (booking == null) {
            return "redirect:/dashboard?error=BookingNotFound";
        }

        if (!booking.getCustomer().getUsername().equals(principal.getName())) {
            return "redirect:/dashboard?error=Unauthorized";
        }

        model.addAttribute("booking", booking);
        model.addAttribute("noRek", "BCA: 1234567890 a.n LAPANGIN");
        return "payment";
    }
}