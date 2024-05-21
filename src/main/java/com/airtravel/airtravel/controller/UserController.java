package com.airtravel.airtravel.controller;


import com.airtravel.airtravel.model.User;
import com.airtravel.airtravel.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;


@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // Assuming you have a dashboard.html template
    }
}

