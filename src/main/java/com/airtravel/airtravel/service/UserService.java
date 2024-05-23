package com.airtravel.airtravel.service;


import com.airtravel.airtravel.model.User;

public interface UserService {
    void register(User user);
    User login(String username, String password);
    User getUserByUsername(String username);

}

