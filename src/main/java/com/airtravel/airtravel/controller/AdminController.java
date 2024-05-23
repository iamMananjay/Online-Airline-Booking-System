package com.airtravel.airtravel.controller;

import com.airtravel.airtravel.model.Flight;
import com.airtravel.airtravel.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class AdminController {

    @Autowired
    private FlightService flightService;

    // Show all flights
    @GetMapping("/admin/flights")
    public String showFlights(Model model) {
        model.addAttribute("flights", flightService.getAllFlights());
        return "admin/flights";
    }

    // Show form to add a new flight
    @GetMapping("/admin/add-flight")
    public String showAddFlightForm(Model model) {
        model.addAttribute("flight", new Flight()); // Initialize a new Flight object
        return "add-flight"; // Assuming you have a Thymeleaf template named "add-flight.html"
    }

    // Show form to edit an existing flight
    @GetMapping("/admin/edit-flight/{id}")
    public String showEditFlightForm(@PathVariable Long id, Model model) {
        Optional<Flight> optionalFlight = flightService.getFlightById(id);
        if (optionalFlight.isPresent()) {
            Flight flight = optionalFlight.get();
            model.addAttribute("flight", flight);
            return "admin/edit-flight"; // Assuming the Thymeleaf template for editing flight is "edit-flight.html"
        } else {
            // Handle the case where the flight with the given ID is not found
            return "redirect:/admin/flights"; // Redirect to flights list or show an error page
        }
    }

    // Process the form submission to add a new flight
    @PostMapping("/admin/add-flight")
    public String addFlight(@ModelAttribute Flight flight) {
        flightService.saveFlight(flight);
        return "redirect:/dashboard"; // Redirect to flights list
    }

    // Process the form submission to update an existing flight
    @PostMapping("/admin/edit-flight")
    public String updateFlight(@ModelAttribute Flight flight, @RequestParam String departureDate) {
        // Parse the departureDate string to LocalDateTime
        flight.setDepartureDatetime(LocalDateTime.parse(departureDate));
        flightService.saveFlight(flight);
        return "redirect:/admin/flights"; // Redirect to flights list
    }

    // Process the request to delete a flight
    @GetMapping("/admin/delete-flight/{id}")
    public String deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return "redirect:/dashboard"; // Redirect to flights list
    }
}
