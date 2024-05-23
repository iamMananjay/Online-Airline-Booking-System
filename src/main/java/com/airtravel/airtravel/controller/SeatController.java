package com.airtravel.airtravel.controller;

import com.airtravel.airtravel.model.Booking;
import com.airtravel.airtravel.model.Flight;
import com.airtravel.airtravel.model.Seat;
import com.airtravel.airtravel.model.User;
import com.airtravel.airtravel.service.BookingService;
import com.airtravel.airtravel.service.FlightService;
import com.airtravel.airtravel.service.SeatService;
import com.airtravel.airtravel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/seats")
public class SeatController {
    @Autowired
    private SeatService seatService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private FlightService flightService;


    @GetMapping
    public String getSeatPage(Model model) {
        List<Seat> seats = seatService.getAllSeats();
        model.addAttribute("seats", seats);
        return "seat-availability";
    }

    @GetMapping("/flight/{flightId}")
    public String getSeatsByFlight(@PathVariable Long flightId, Model model) {
        List<Seat> seats = seatService.getSeatsByFlightId(flightId);
        model.addAttribute("seats", seats);
        return "seat-selection";
    }

    @GetMapping("/api/flight/{flightId}")
    @ResponseBody
    public List<Seat> getSeatsByFlightApi(@PathVariable Long flightId) {
        return seatService.getSeatsByFlightId(flightId);
    }



    @PostMapping("/flight/book")
    public String bookSeats(@RequestParam("flightNumber") String flightNumber,
                            @RequestParam("selectedSeats") List<String> selectedSeats,
                            @RequestParam("numberOfChildren") int numberOfChildren,
                            @RequestParam("price") double price,
                            RedirectAttributes redirectAttributes) {

        // Server-side validation to ensure no more than 6 seats are booked at once
        if (selectedSeats.size() > 6) {
            redirectAttributes.addFlashAttribute("bookingErrorMessage", "You can only book up to 6 seats at once.");
            return "redirect:/seats/flight/" + flightNumber;
        }

        if (numberOfChildren > 0) {
            if (selectedSeats.size() < numberOfChildren) {
                redirectAttributes.addFlashAttribute("bookingErrorMessage", "Number of children exceeds the selected seats.");
                return "redirect:/seats/flight/" + flightNumber;
            }

            List<Integer> seatNumbers = selectedSeats.stream()
                    .map(seat -> Integer.parseInt(seat.replaceAll("[^\\d]", "")))
                    .sorted()
                    .collect(Collectors.toList());

            for (int i = 1; i < seatNumbers.size(); i++) {
                if (seatNumbers.get(i) - seatNumbers.get(i - 1) > 1) {
                    redirectAttributes.addFlashAttribute("bookingErrorMessage", "Children must be seated next to a guardian.");
                    return "redirect:/seats/flight/" + flightNumber;
                }
            }
        }

        flightNumber = flightNumber.replaceAll(",", "");

        Flight flight = flightService.getFlightByNumber(flightNumber);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get username
        User user = userService.getUserByUsername(username); // Get user from the database

        List<Seat> seats = selectedSeats.stream()
                .map(seatNumber -> seatService.getSeatByNumberAndFlight(seatNumber, flight))
                .collect(Collectors.toList());

        // Check if any of the selected seats are locked and not available
//        for (Seat seat : seats) {
//            if (seat.isLocked() && !seat.isAvailable()) {
//                redirectAttributes.addFlashAttribute("bookingErrorMessage", "One or more selected seats are locked or unavailable.");
//                return "redirect:/seats/flight/" + flightNumber;
//            }
//        }

        // Create Booking object and set booking details
        Booking booking = new Booking();
        booking.setFlight(flight);
        booking.setSeats(seats);
        booking.setPassengerName(user.getUsername());
        booking.setPassengerEmail(user.getEmail());
        booking.setPrice(price); // Set the price
        booking.setBookingDateTime(LocalDateTime.now()); // Set booking date and time
        bookingService.saveBooking(booking);

        // Update seat availability and unlock seats
//        for (Seat seat : seats) {
//            seat.setAvailable(false);
//            seat.setLocked(false);
//            seat.setLockedAt(null);
//            seatService.updateSeat(seat);
//        }

        redirectAttributes.addFlashAttribute("bookingSuccessMessage", "Your booking has been confirmed!");
        return "redirect:/";
    }









    @PostMapping("/lock")
    public ResponseEntity<?> lockSeat(@RequestParam("seatNumber") String seatNumber, @RequestParam("flightNumber") String flightNumber) {
        Seat seat = seatService.getSeatByNumberAndFlight(seatNumber, flightNumber);
        if (seat != null && seat.isAvailable() && !seat.isLocked()) {
            seat.setLocked(true);
            seat.setLockedAt(LocalDateTime.now());
            seatService.updateSeat(seat);
            return ResponseEntity.ok("Seat locked successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Seat is not available or already locked");
    }

    @PostMapping("/unlock")
    public ResponseEntity<?> unlockSeat(@RequestParam("seatNumber") String seatNumber, @RequestParam("flightNumber") String flightNumber) {
        Seat seat = seatService.getSeatByNumberAndFlight(seatNumber, flightNumber);
        if (seat != null && seat.isLocked()) {
            seat.setLocked(false);
            seat.setLockedAt(null);
            seatService.updateSeat(seat);
            return ResponseEntity.ok("Seat unlocked successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Seat is not locked or does not exist");
    }

}
