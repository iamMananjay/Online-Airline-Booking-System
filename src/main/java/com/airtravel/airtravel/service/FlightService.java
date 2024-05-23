package com.airtravel.airtravel.service;


import com.airtravel.airtravel.model.Flight;
import java.util.Optional;



import java.util.List;

public interface FlightService {
    List<Flight> getAllFlights();
    void saveFlight(Flight flight);
    void deleteFlight(Long id);
    Optional<Flight> getFlightById(Long id); // New method to get flight by ID

    List<Flight> searchFlights(String from, String to, String flightClass);
    Flight getFlightByNumber(String flightNumber);

}
