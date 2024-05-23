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

    @PostMapping("/perform_login")
    public String perform_login(@ModelAttribute("username") String username,
                        @ModelAttribute("password") String password, HttpSession session,Model model) {
        User user = userService.login(username, password);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            session.setAttribute("user", user); // Store user in session
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Assuming you have a login.html template
    }

    @GetMapping("/register")
    public String register() {
        return "register"; // Assuming you have a register.html template
    }

//
@GetMapping("/dashboard")
public String dashboard(Model model) {
    List<Flight> flights = flightService.getAllFlights();

    // Format departureDatetime before passing to the template
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    flights.forEach(flight -> flight.setFormattedDepartureDate(flight.getDepartureDatetime().format(formatter)));

    model.addAttribute("flights", flights);
    return "dashboard"; // Redirect to flights list
}





}

