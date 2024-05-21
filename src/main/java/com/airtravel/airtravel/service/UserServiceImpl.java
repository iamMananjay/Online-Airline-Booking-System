package com.airtravel.airtravel.service;

import com.airtravel.airtravel.model.User;

import com.airtravel.airtravel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void register(User user) {
        // Hash the password before saving the user
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);


        userRepository.save(user);
    }


public User login(String username, String password) {
    User user = userRepository.findByUsername(username);
    if (user != null && passwordEncoder.matches(password, user.getPassword())) {
        return user;
    } else {
        return null;
    }
}
}

