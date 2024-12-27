package com.lapangin.web.dto;

public class BookingResponse extends Response {
    private Long bookingId;
    
    public BookingResponse(boolean success, String message, Long bookingId) {
        super(success, message);
        this.bookingId = bookingId;
    }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
}