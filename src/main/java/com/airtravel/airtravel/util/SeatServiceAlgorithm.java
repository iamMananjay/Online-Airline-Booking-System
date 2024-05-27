package com.airtravel.airtravel.util;

import com.airtravel.airtravel.model.Seat;
import com.airtravel.airtravel.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
public class SeatServiceAlgorithm {
    @Autowired
    private SeatRepository seatRepository;

    public List<String> suggestSeats(String flightNumber, int numberOfSeats) {
        List<Seat> availableSeats = seatRepository.findByFlightNumberAndIsAvailableTrue(flightNumber);
        List<String> suggestedSeats = new ArrayList<>();

        // Extract seat numbers from available seats
        List<String> availableSeatNumbers = availableSeats.stream()
                .map(Seat::getSeatNumber)
                .collect(Collectors.toList());

        if (numberOfSeats == 1) {
            // Suggest a window or corner seat
            for (String seat : availableSeatNumbers) {
                if (seat.endsWith("A") || seat.endsWith("F")) {
                    suggestedSeats.add(seat);
                    break;
                }
            }
        } else if (numberOfSeats > 1) {
            // Suggest seats in sequence
            for (int i = 0; i < availableSeatNumbers.size(); i++) {
                boolean canBook = true;
                List<String> tempSeats = new ArrayList<>();

                for (int j = 0; j < numberOfSeats; j++) {
                    if (i + j >= availableSeatNumbers.size() || !availableSeatNumbers.get(i + j).equals(availableSeatNumbers.get(i + j))) {
                        canBook = false;
                        break;
                    }
                    tempSeats.add(availableSeatNumbers.get(i + j));
                }

                if (canBook) {
                    suggestedSeats.addAll(tempSeats);
                    break;
                }
            }
        }

        return suggestedSeats;
    }


//public Map<String, Object> bookSeats(String flightNumber, List<String> selectedSeats) {
//    Map<String, Object> response = new HashMap<>();
//    List<String> suggestedSeats = new ArrayList<>();
//
//    // Find available seats for the flight
//    List<Seat> availableSeats = seatRepository.findByFlightNumberAndIsAvailableTrue(flightNumber);
//    List<String> availableSeatNumbers = availableSeats.stream()
//            .map(Seat::getSeatNumber)
//            .collect(Collectors.toList());
//
//    // Check if all selected seats are available
//    boolean allSeatsAvailable = selectedSeats.stream().allMatch(availableSeatNumbers::contains);
//
//    if (!allSeatsAvailable) {
//        response.put("status", "Selected seats are not available");
//    }else {
//
//        // Suggest alternative seats
//        if (selectedSeats.size() == 1) {
//            // Suggest a window or corner seat
//            for (String seat : availableSeatNumbers) {
//                if (seat.endsWith("A") || seat.endsWith("F")) {
//                    suggestedSeats.add(seat);
//                    break;
//                }
//            }
//        } else if (selectedSeats.size() > 1) {
//            // Suggest seats in sequence
//            for (int i = 0; i < availableSeatNumbers.size(); i++) {
//                boolean canBook = true;
//                List<String> tempSeats = new ArrayList<>();
//
//                for (int j = 0; j < selectedSeats.size(); j++) {
//                    if (i + j >= availableSeatNumbers.size() || !availableSeatNumbers.get(i + j).equals(availableSeatNumbers.get(i + j))) {
//                        canBook = false;
//                        break;
//                    }
//                    tempSeats.add(availableSeatNumbers.get(i + j));
//                }
//
//                if (canBook) {
//                    suggestedSeats.addAll(tempSeats);
//                    break;
//                }
//            }
//        }
//        response.put("suggestedSeats", suggestedSeats);
//        return response;
//    }
//
//    // Book the seats if all are available
////    List<Seat> seatsToBook = seatRepository.findByFlightNumberAndSeatNumberIn(flightNumber, selectedSeats);
////    seatsToBook.forEach(seat -> seat.setBooked(true));
////    seatRepository.saveAll(seatsToBook);
////
////    response.put("status", "Seats booked successfully");
////    response.put("bookedSeats", selectedSeats);
//    return null;
//}
public Map<String, Object> bookSeats(String flightNumber, List<String> selectedSeats) {
    Map<String, Object> response = new HashMap<>();
    List<String> suggestedSeats = new ArrayList<>();

    // Find available seats for the flight
    List<Seat> availableSeats = seatRepository.findByFlightNumberAndIsAvailableTrue(flightNumber);
    List<String> availableSeatNumbers = availableSeats.stream()
            .map(Seat::getSeatNumber)
            .collect(Collectors.toList());

    // Check if all selected seats are available
    boolean allSeatsAvailable = selectedSeats.stream().allMatch(availableSeatNumbers::contains);

    if (!allSeatsAvailable) {
        response.put("status", "Selected seats are not available");
    }else {

        // Suggest alternative seats
        if (selectedSeats.size() == 1) {
            // Suggest a window or corner seat
            for (String seat : availableSeatNumbers) {
                if (seat.endsWith("A") || seat.endsWith("F")) {
                    suggestedSeats.add(seat);
                    break;
                }
            }
        } else if (selectedSeats.size() > 1) {
            // Suggest seats in sequence
            for (int i = 0; i < availableSeatNumbers.size(); i++) {
                boolean canBook = true;
                List<String> tempSeats = new ArrayList<>();

                for (int j = 0; j < selectedSeats.size(); j++) {
                    if (i + j >= availableSeatNumbers.size() || !availableSeatNumbers.get(i + j).equals(availableSeatNumbers.get(i + j))) {
                        canBook = false;
                        break;
                    }
                    tempSeats.add(availableSeatNumbers.get(i + j));
                }

                if (canBook) {
                    suggestedSeats.addAll(tempSeats);
                    break;
                }
            }
        }
        response.put("suggestedSeats", suggestedSeats);
        return response;
    }
    return null;
}

    public boolean isSeatAvailable(String flightNumber, String seat) {
        List<Seat> seats = seatRepository.findByFlightNumberAndSeatNumberIn(flightNumber, Collections.singletonList(seat));
        return seats.size() == 1 && !seats.get(0).isAvailable();
    }
}
