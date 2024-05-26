package com.airtravel.airtravel.controller;

import com.airtravel.airtravel.model.Booking;
import com.airtravel.airtravel.model.Flight;
import com.airtravel.airtravel.model.Seat;
import com.airtravel.airtravel.model.User;
import com.airtravel.airtravel.service.BookingService;
import com.airtravel.airtravel.service.FlightService;
import com.airtravel.airtravel.service.SeatService;
import com.airtravel.airtravel.service.UserService;
import com.airtravel.airtravel.util.PlaneSetting;
import com.airtravel.airtravel.util.SeatAllocation;
import com.airtravel.airtravel.util.SeatingPlan;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        if (selectedSeats.size() > 6) {
            redirectAttributes.addFlashAttribute("bookingErrorMessage", "You can only book up to 6 seats at once.");
            return "redirect:/seats/flight/" + flightNumber;
        }

        Flight flight = flightService.getFlightByNumber(flightNumber);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        // Calculate the number of rows and columns based on totalSeats
        int numRows = flight.getTotalSeats() / 6; // Assuming each row has 6 seats
        int numCols = 6;

        SeatingPlan seatingPlan = new SeatingPlan(numRows, numCols);
        List<List<Integer>> plan = seatingPlan.getSeatingPlan();
        List<int[]> allocatedSeats = SeatAllocation.allocateSeats(plan, selectedSeats.size());

        if (allocatedSeats.isEmpty()) {
            redirectAttributes.addFlashAttribute("bookingErrorMessage", "Not enough contiguous seats available.");
            return "redirect:/seats/flight/" + flight.getFlightId();
        }

        List<Seat> seats = allocatedSeats.stream()
                .map(seat -> {
                    int row = seat[0];
                    int col = seat[1];
                    char seatLetter = (char) ('A' + col);
                    String seatNumber = String.format("%d%c", row + 1, seatLetter);
                    return seatService.getSeatByNumberAndFlight(seatNumber, flight);
                })
                .collect(Collectors.toList());

        // Check for null seats (if seat number does not exist)
        if (seats.contains(null)) {
            redirectAttributes.addFlashAttribute("bookingErrorMessage", "One or more selected seats are invalid.");
            return "redirect:/seats/flight/" + flight.getFlightId();
        }

        Booking booking = new Booking();
        booking.setFlight(flight);
        booking.setSeats(seats);
        booking.setPassengerName(user.getUsername());
        booking.setPassengerEmail(user.getEmail());
        booking.setPrice(price);
        booking.setBookingDateTime(LocalDateTime.now());
        bookingService.saveBooking(booking);

        for (Seat seat : seats) {
            seat.setAvailable(false);
            seat.setLocked(false);
            seat.setLockedAt(null);
            seatService.updateSeat(seat);
        }

        redirectAttributes.addFlashAttribute("bookingSuccessMessage", "Your booking has been confirmed!");
        return "redirect:/";
    }

//    @PostMapping("/flight/book")
//    public String bookSeats(@RequestParam("flightNumber") String flightNumber,
//                            @RequestParam("selectedSeats") List<String> selectedSeats,
//                            @RequestParam("numberOfChildren") int numberOfChildren,
//                            @RequestParam("price") double price,
//                            RedirectAttributes redirectAttributes) {
//
//        if (selectedSeats.size() > 6) {
//            redirectAttributes.addFlashAttribute("bookingErrorMessage", "You can only book up to 6 seats at once.");
//            return "redirect:/seats/flight/" + flightNumber;
//        }
//
//        Flight flight = flightService.getFlightByNumber(flightNumber);
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        User user = userService.getUserByUsername(username);
//
//        // Assuming 6 seats per row
//        int numCols = 6;
//        int numRows = flight.getTotalSeats() / numCols;
//
//        // Initialize the seating array
//        boolean[][] seating = new boolean[numRows][numCols];
//
//        // Fetch all seats for the flight
//        List<Seat> allSeats = seatService.getSeatsByFlightNumber(flightNumber);
//        for (Seat seat : allSeats) {
//            // Parse the seat number to get row and column
//            String seatNumber = seat.getSeatNumber();
//            int row = Integer.parseInt(seatNumber.substring(0, seatNumber.length() - 1)) - 1;
//            int col = seatNumber.charAt(seatNumber.length() - 1) - 'A';
//            seating[row][col] = !seat.isAvailable();
//        }
//
//        // Convert selectedSeats to seat indices
//        Set<String> seatsToBook = new HashSet<>(selectedSeats);
//        boolean canBook = PlaneSetting.canBookSeats(seating, seatsToBook);
//
//        if (!canBook) {
//            redirectAttributes.addFlashAttribute("bookingErrorMessage", "Cannot book selected seats as they are either isolated or not available.");
//            return "redirect:/seats/flight/" + flight.getFlightId();
//        }
//
//        List<Seat> seats = new ArrayList<>();
//        for (String seat : selectedSeats) {
//            int row = Integer.parseInt(seat.substring(0, seat.length() - 1)) - 1;
//            int col = seat.charAt(seat.length() - 1) - 'A';
//            String seatNumber = String.format("%d%c", row + 1, (char) ('A' + col));
//            Seat seatEntity = seatService.getSeatByNumberAndFlight(seatNumber, flight);
//            if (seatEntity != null && seatEntity.isAvailable()) {
//                seats.add(seatEntity);
//            } else {
//                redirectAttributes.addFlashAttribute("bookingErrorMessage", "One or more selected seats are invalid.");
//                return "redirect:/seats/flight/" + flight.getFlightId();
//            }
//        }
//
//        Booking booking = new Booking();
//        booking.setFlight(flight);
//        booking.setSeats(seats);
//        booking.setPassengerName(user.getUsername());
//        booking.setPassengerEmail(user.getEmail());
//        booking.setPrice(price);
//        booking.setBookingDateTime(LocalDateTime.now());
//        bookingService.saveBooking(booking);
//
//        for (Seat seat : seats) {
//            seat.setAvailable(false);
//            seat.setLocked(false);
//            seat.setLockedAt(null);
//            seatService.updateSeat(seat);
//        }
//
//        redirectAttributes.addFlashAttribute("bookingSuccessMessage", "Your booking has been confirmed!");
//        return "redirect:/";
//    }




//    @PostMapping("/flight/book")
//    public String bookSeats(@RequestParam("flightNumber") String flightNumber,
//                            @RequestParam("selectedSeats") List<String> selectedSeats,
//                            @RequestParam("numberOfChildren") int numberOfChildren,
//                            @RequestParam("price") double price,
//                            RedirectAttributes redirectAttributes) {
//
//        // Server-side validation to ensure no more than 6 seats are booked at once
//        if (selectedSeats.size() > 6) {
//            redirectAttributes.addFlashAttribute("bookingErrorMessage", "You can only book up to 6 seats at once.");
//            return "redirect:/seats/flight/" + flightNumber;
//        }
//
//        if (numberOfChildren > 0) {
//            if (selectedSeats.size() < numberOfChildren) {
//                redirectAttributes.addFlashAttribute("bookingErrorMessage", "Number of children exceeds the selected seats.");
//                return "redirect:/seats/flight/" + flightNumber;
//            }
//
//            List<Integer> seatNumbers = selectedSeats.stream()
//                    .map(seat -> Integer.parseInt(seat.replaceAll("[^\\d]", "")))
//                    .sorted()
//                    .collect(Collectors.toList());
//
//            for (int i = 1; i < seatNumbers.size(); i++) {
//                if (seatNumbers.get(i) - seatNumbers.get(i - 1) > 1) {
//                    redirectAttributes.addFlashAttribute("bookingErrorMessage", "Children must be seated next to a guardian.");
//                    return "redirect:/seats/flight/" + flightNumber;
//                }
//            }
//        }
//
//        flightNumber = flightNumber.replaceAll(",", "");
//
//        Flight flight = flightService.getFlightByNumber(flightNumber);
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName(); // Get username
//        User user = userService.getUserByUsername(username); // Get user from the database
//
//        List<Seat> seats = selectedSeats.stream()
//                .map(seatNumber -> seatService.getSeatByNumberAndFlight(seatNumber, flight))
//                .collect(Collectors.toList());
//
//        // Check if any of the selected seats are locked and not available
//        for (Seat seat : seats) {
//            if (seat.isLocked() && !seat.isAvailable()) {
//                redirectAttributes.addFlashAttribute("bookingErrorMessage", "One or more selected seats are locked or unavailable.");
//                return "redirect:/seats/flight/" + flight.getFlightId();
//            }
//        }
//
//        // Check for single scattered seats
//        if (hasSingleScatteredSeats(seats)) {
//            redirectAttributes.addFlashAttribute("bookingErrorMessage", "Seats must be booked together without leaving single scattered seats.");
//            return "redirect:/seats/flight/" + flight.getFlightId();
//        }
//
//        // Create Booking object and set booking details
//        Booking booking = new Booking();
//        booking.setFlight(flight);
//        booking.setSeats(seats);
//        booking.setPassengerName(user.getUsername());
//        booking.setPassengerEmail(user.getEmail());
//        booking.setPrice(price); // Set the price
//        booking.setBookingDateTime(LocalDateTime.now()); // Set booking date and time
//        bookingService.saveBooking(booking);
//
//        // Update seat availability and unlock seats
//        for (Seat seat : seats) {
//            seat.setAvailable(false);
//            seat.setLocked(false);
//            seat.setLockedAt(null);
//            seatService.updateSeat(seat);
//        }
//
//
//
//        redirectAttributes.addFlashAttribute("bookingSuccessMessage", "Your booking has been confirmed!");
//        return "redirect:/";
//    }


    // Method to check for single scattered seats
//    private boolean hasSingleScatteredSeats(List<Seat> seats) {
//        Collections.sort(seats, Comparator.comparing(Seat::getSeatNumber));
//
//        for (int i = 1; i < seats.size(); i++) {
//            String currentSeatNumber = seats.get(i).getSeatNumber();
//            String prevSeatNumber = seats.get(i - 1).getSeatNumber();
//            int currentRow = Integer.parseInt(seats.get(i).getSeatNumber().substring(0, seats.get(i).getSeatNumber().length() - 1));
//            int prevRow = Integer.parseInt(seats.get(i - 1).getSeatNumber().substring(0, seats.get(i - 1).getSeatNumber().length() - 1));
//
//            if (currentRow == prevRow && seats.get(i).getSeatNumber().charAt(seats.get(i).getSeatNumber().length() - 1) - seats.get(i - 1).getSeatNumber().charAt(seats.get(i - 1).getSeatNumber().length() - 1) > 1) {
//                return true;
//            } else if (currentSeatNumber !== prevSeatNumber) {
//                return true;
//
//            }
//        }
//        return false;
//    }
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
