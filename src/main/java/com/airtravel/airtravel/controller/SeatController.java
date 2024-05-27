package com.airtravel.airtravel.controller;

import com.airtravel.airtravel.model.Booking;
import com.airtravel.airtravel.model.Flight;
import com.airtravel.airtravel.model.Seat;
import com.airtravel.airtravel.model.User;
import com.airtravel.airtravel.service.BookingService;
import com.airtravel.airtravel.service.FlightService;
import com.airtravel.airtravel.service.SeatService;
import com.airtravel.airtravel.service.UserService;

//import com.airtravel.airtravel.util.SeatServiceAlgorithm;
import com.airtravel.airtravel.util.SeatServiceAlgorithm;
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
import java.util.*;
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
    @Autowired
    private SeatServiceAlgorithm seatServiceAlgorithm;




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


        if (selectedSeats.size() > 6) {
            redirectAttributes.addFlashAttribute("bookingErrorMessage", "You can only book up to 6 seats at once.");
            return "redirect:/seats/flight/" + flightNumber;
        }

        Flight flight = flightService.getFlightByNumber(flightNumber);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        Map<String, Object> bookSuggestion = seatServiceAlgorithm.bookSeats(flightNumber, selectedSeats);


        if (bookSuggestion != null) {
            selectedSeats.forEach(seat -> {
                Seat seatnum = seatService.getSeatByNumberAndFlight(seat, flightNumber);
                if (seatnum != null && seatnum.isLocked()) {
                    seatnum.setLocked(false);
                    seatnum.setLockedAt(null);
                    seatService.updateSeat(seatnum);
                }
            });
            Object suggestion = bookSuggestion.get("suggestedSeats"); // Assuming "suggestedSeats" is the key for the suggested seats

            if (!suggestion.equals(selectedSeats)) {
                redirectAttributes.addFlashAttribute("bookingErrorMessage", "You can select these two seats: " + suggestion);
                return "redirect:/seats/flight/" + flight.getFlightId();
            }
        }



        List<Seat> selectedSeat = new ArrayList<>();
        selectedSeats.forEach(seat -> {
            Seat seats = seatService.getSeatByNumberAndFlight(seat, flightNumber);
            seats.setAvailable(false);
            seats.setLocked(false);
            seats.setLockedAt(null);
            selectedSeat.add(seats);
        });

        Booking booking = new Booking();
        booking.setFlight(flight);
        booking.setPassengerName(user.getUsername());
        booking.setPassengerEmail(user.getEmail());
        booking.setPrice(price);
        booking.setBookingDateTime(LocalDateTime.now());
        bookingService.saveBooking(booking);

        selectedSeat.forEach(seat -> seatService.updateSeat(seat));

        redirectAttributes.addFlashAttribute("bookingSuccessMessage", "Your booking has been confirmed!");
        return "redirect:/";
    }


    private boolean hasSingleScatteredSeats(List<Seat> seats) {
        Collections.sort(seats, Comparator.comparing(Seat::getSeatNumber));

        for (int i = 1; i < seats.size(); i++) {
            String currentSeatNumber = seats.get(i).getSeatNumber();
            String prevSeatNumber = seats.get(i - 1).getSeatNumber();
            int currentRow = Integer.parseInt(currentSeatNumber.substring(0, currentSeatNumber.length() - 1));
            int prevRow = Integer.parseInt(prevSeatNumber.substring(0, prevSeatNumber.length() - 1));

            char currentSeat = currentSeatNumber.charAt(currentSeatNumber.length() - 1);
            char prevSeat = prevSeatNumber.charAt(prevSeatNumber.length() - 1);

            if (currentRow == prevRow && currentSeat - prevSeat > 1) {
                return true; // Scattered seats in the same row
            } else if (currentRow != prevRow) {
                return true; // Gap between rows
            }
        }
        return false;
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
