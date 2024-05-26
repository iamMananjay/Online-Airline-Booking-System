package com.airtravel.airtravel.service;

import com.airtravel.airtravel.model.Flight;
import com.airtravel.airtravel.model.Seat;
import com.airtravel.airtravel.repository.FlightRepository;
import com.airtravel.airtravel.repository.SeatRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FlightServiceImpl implements FlightService {

    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private SeatRepository seatRepository;

//    @Override
//    public List<Flight> getAllFlights() {
//        return flightRepository.findAll();
//    }
@Override
@Transactional(readOnly = true)
public List<Flight> getAllFlights() {
    List<Flight> flights = flightRepository.findAll();
    // Initialize the bookings collection for each flight
    flights.forEach(flight -> flight.getBookings().size());
    return flights;
}


    @Override
    public void saveFlight(Flight flight) {
        flightRepository.save(flight);
        // Generate seats for the flight
        generateSeatsForFlight(flight);
    }

    @Override
    @Transactional
    public void deleteFlight(Long id) {
        // First, delete seats associated with the flight
//        seatRepository.deleteByFlight_FlightId(id);
        seatRepository.deleteByFlight_FlightId(id);


        // Then, delete the flight itself
        flightRepository.deleteByFlightId(id);
    }

    @Override
    public Optional<Flight> getFlightById(Long id) {
        return flightRepository.findById(id);
    }

    @Override
    public List<Flight> searchFlights(String departureAirport, String destinationAirport, String flightClass) {
        // Implement the logic to search for flights based on the provided criteria
        // For example, you can use Spring Data JPA to query the database
        // Here's a sample query using Spring Data JPA
        List<Flight> flights = flightRepository.findByDepartureAirportAndDestinationAirport(departureAirport, destinationAirport);
        // Add additional filtering based on flight class if needed
        // Initialize bookings collection for each flight
        for (Flight flight : flights) {
            Hibernate.initialize(flight.getBookings());
        }
        return flights;
    }

    private void generateSeatsForFlight(Flight flight) {
        // Retrieve the ticket price for the flight
        double ticketPrice = flight.getTicketPrice();

        // Determine number of seats based on flight number (e.g., B100 -> 55 seats, B200 -> 80 seats)
        int numSeats = 0;
        if (flight.getFlightNumber().equals("B100")) {
            numSeats = 66;
        } else if (flight.getFlightNumber().equals("B200")) {
            numSeats = 84;
        } else {
            numSeats = 25;
        }

        // Generate seats
        List<Seat> seats = new ArrayList<>();
        int numSeatsPerRow = 6; // Number of seats per row (A to F)

        // Calculate the total number of rows needed
        int numSeatRows = (int) Math.ceil((double) numSeats / numSeatsPerRow);

        // Maintain a counter for the number of seats generated
        int seatCount = 0;

        for (int row = 1; row <= numSeatRows; row++) {
            for (char seatLetter = 'A'; seatLetter <= 'F'; seatLetter++) {
                // Exit loop if total number of seats is reached
                if (seatCount >= numSeats) {
                    break;
                }

                // Create a seat
                Seat seat = new Seat();
                String seatNumber = row + String.valueOf(seatLetter);
                seat.setSeatNumber(seatNumber);
                seat.setAvailable(true); // Initially, all seats are available
                seat.setFlight(flight);
                seat.setFlightNumber(flight.getFlightNumber());
                seat.setTicketPrice(ticketPrice); // Set the ticket price for each seat
                // Set other seat properties (e.g., class, locked, etc.)
                seats.add(seat);
                seatCount++;
            }
        }

        // Save seats
        seatRepository.saveAll(seats);
    }





    private int determineNumberOfSeats(String flightNumber) {
        // Implement logic to determine number of seats based on flight number
        // For example, parse flight number and return corresponding seat count
        // This logic can be customized based on your requirements
        // For simplicity, let's assume that flight numbers like B100 correspond to seat count directly
        if (flightNumber.startsWith("B")) {
            // Extract numeric part and parse it as integer
            return Integer.parseInt(flightNumber.substring(1));
        }
        // Return a default value if flight number format is not recognized
        return 0;
    }
    public Flight getFlightByNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber);
    }

}

