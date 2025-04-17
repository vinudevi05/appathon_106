package com.app.proj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.proj.entities.User;
import com.app.proj.repository.UserRepository;
import com.app.proj.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // POST: Register a new user with address
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        System.out.println("User registered: " + user.toString());
        User newUser = userService.register(user);
        return ResponseEntity.ok(newUser);
    }

    // POST: User login with email and password
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User request) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(request.getPassword())) { // Consider using hashing instead of plain text
                response.put("status", "success");
                response.put("user", user);
                return ResponseEntity.ok(response);
            }
        }

        response.put("status", "failed");
        response.put("message", "User not found or invalid credentials!");
        return ResponseEntity.status(404).body(response);
    }

    // GET: Fetch user by email
    @GetMapping("/find")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        Optional<User> user = userService.findByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // PUT: Update user's address
    @PutMapping("/updateAddress/{id}")
    public ResponseEntity<User> updateAddress(@PathVariable Long id, @RequestBody String newAddress) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAddress(newAddress);
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }
}
