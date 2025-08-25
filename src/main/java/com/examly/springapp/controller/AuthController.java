package com.examly.springapp.controller;

import com.examly.springapp.entity.User;
import com.examly.springapp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Register
    @PostMapping("/register")
    public Response register(@RequestBody RegisterRequest request) {
        String result = authService.register(request.getUsername(), request.getPassword(), request.getRole());
        boolean success = result.equals("User registered successfully!");
        return new Response(success, result);
    }

@PostMapping("/login")
public Response login(@RequestBody LoginRequest request) {
    Optional<User> userOpt = authService.authenticate(request.getUsername(), request.getPassword());
    if (userOpt.isPresent()) {
        User user = userOpt.get();
        String role = user.getRole().replace("ROLE_", "").toUpperCase();
        return new Response(true, "Login successful", role, user.getUsername(), user.getId());
    }
    return new Response(false, "Invalid username or password");
}



    @GetMapping("/me")
public ResponseEntity<?> getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getName() == null) {
        return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
    }

    Optional<User> userOpt = authService.findByUsername(auth.getName());
    if (userOpt.isPresent()) {
        return ResponseEntity.ok(userOpt.get());
    } else {
        return ResponseEntity.status(404).body(Map.of("message", "User not found"));
    }
}

    // Request & Response classes
    public static class LoginRequest {
        private String username;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String username;
        private String password;
        private String role;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class Response {
    public boolean success;
    public String message;
    public String role;
    public String username;
    public Long id; // ✅ add this

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Response(boolean success, String message, String role, String username, Long id) {
        this.success = success;
        this.message = message;
        this.role = role;
        this.username = username;
        this.id = id;
     }
   }
}
