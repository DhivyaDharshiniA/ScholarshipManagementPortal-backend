package com.examly.springapp.service;

import com.examly.springapp.entity.User;
import com.examly.springapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register a new user
    public String register(String username, String password, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            return "Username already exists!";
        }
        String formattedRole = "ROLE_" + role.toUpperCase();
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setRole(formattedRole);
        userRepository.save(user);
        return "User registered successfully!";
    }

    public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
}


    // Authenticate user
    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()));
    }
}
