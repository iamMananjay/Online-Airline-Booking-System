//package com.airtravel.airtravel.controller;
//
//import com.airtravel.airtravel.model.Booking;
//import com.airtravel.airtravel.model.Seat;
//import com.airtravel.airtravel.service.BookingService;
//import com.airtravel.airtravel.service.FlightService;
//import com.airtravel.airtravel.service.SeatService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/bookings")
//public class BookingController {
//    @Autowired
//    private BookingService bookingService;
//
//    @Autowired
//    private FlightService flightService;
//
//    @Autowired
//    private SeatService seatService;
//
//    @PostMapping("/create")
//    public Booking createBooking(@RequestParam Long userId, @RequestParam Long flightId, @RequestBody List<Seat> seats) {
//        return bookingService.createBooking(userId, flightId, seats);
//    }
//
//    @DeleteMapping("/cancel/{bookingId}")
//    public void cancelBooking(@PathVariable Long bookingId) {
//        bookingService.cancelBooking(bookingId);
//    }
//
//    @GetMapping("/user/{userId}")
//    public List<Booking> getBookingsByUserId(@PathVariable Long userId) {
//        return bookingService.getBookingsByUserId(userId);
//    }
//}
