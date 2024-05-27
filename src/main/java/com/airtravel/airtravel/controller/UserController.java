package com.airtravel.airtravel.controller;

import com.airtravel.airtravel.model.Flight;
import com.airtravel.airtravel.model.User;
import com.airtravel.airtravel.service.FlightService;
import com.airtravel.airtravel.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private FlightService flightService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index() {
        return "index"; // Assuming you have a home.html template
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user) {
        userService.register(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Assuming you have a login.html template
    }

    @GetMapping("/register")
    public String register() {
        return "register"; // Assuming you have a register.html template
    }

    @GetMapping("/userdashboard")
    public String userDashboard() {
        return "userdashboard"; // Assuming you have a userdashboard.html template
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        return "dashboard"; // Assuming you have an admin dashboard template
    }

    @GetMapping("/userdetail")
    public String userDetail(Model model) {
        return "userdetail"; // Assuming you have a user detail template
    }

    @GetMapping("/manage-flights")
    public String manageFlights(Model model) {
        List<Flight> flights = flightService.getAllFlights();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        flights.forEach(flight -> flight.setFormattedDepartureDate(flight.getDepartureDatetime().format(formatter)));
        model.addAttribute("flights", flights);
        return "manage-flights"; // Assuming you have a manage-flights.html template
    }
}
