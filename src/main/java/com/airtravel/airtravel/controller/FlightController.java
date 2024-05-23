package com.airtravel.airtravel.controller;

import com.airtravel.airtravel.model.Flight;
import com.airtravel.airtravel.service.FlightService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FlightController {
    @Autowired
    private FlightService flightService;

    @GetMapping("/search-flights")
    public String searchFlights(@RequestParam("departureAirport") String departureAirport,
                                @RequestParam("destinationAirport") String destinationAirport,
                                @RequestParam("class") String flightClass,
                                Model model) {
        // Use flightService to search for flights based on the criteria
        List<Flight> flights = flightService.searchFlights(departureAirport, destinationAirport, flightClass);

        // Add the list of flights to the model
        model.addAttribute("flights", flights);

        // Add the 'departureAirport' and 'destinationAirport' parameters to the model
        model.addAttribute("departureAirport", departureAirport);
        model.addAttribute("destinationAirport", destinationAirport);

        // Return the view to display the list of flights
        return "flight-list";
    }


    @GetMapping("/flight/{flightNumber}")
    public ResponseEntity<Flight> getFlightByNumber(@PathVariable String flightNumber) {
        Flight flight = flightService.getFlightByNumber(flightNumber);
        if (flight != null) {
            return ResponseEntity.ok(flight);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
