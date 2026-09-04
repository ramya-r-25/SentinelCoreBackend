package org.example.sentinelcorebackend.Controller;

import org.example.sentinelcorebackend.Entity.User;
import org.example.sentinelcorebackend.Repository.UserRepository;
import org.example.sentinelcorebackend.Util.Jwtutil;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final Jwtutil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    // ==========================================
    // LOGIN
    // ==========================================

    @PostMapping("/login")
    public Map<String, String> login(
            @RequestBody Map<String, String> credentials) {

        String username = credentials.get("username");
        String password = credentials.get("password");

        // Find user from database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Invalid credentials")
                );

        // Check BCrypt password
        if (!passwordEncoder.matches(
                password,
                user.getPassword()
        )) {
            throw new RuntimeException("Invalid credentials");
        }

        // Get user role
        String role = user.getRoles()
                .stream()
                .findFirst()
                .map(r -> r.getName())
                .orElse("ROLE_VIEWER");

        System.out.println("LOGIN USER: " + username);
        System.out.println("LOGIN ROLE: " + role);

        // Generate access token
        String accessToken =
                jwtUtil.generateToken(
                        username,
                        role
                );

        // Generate refresh token
        String refreshToken =
                jwtUtil.generateRefreshToken(username);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }


    // ==========================================
    // REFRESH TOKEN
    // ==========================================

    @PostMapping("/refresh")
    public Map<String, String> refresh(
            @RequestBody Map<String, String> body) {

        String refreshToken = body.get("refreshToken");

        // Validate refresh token
        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new RuntimeException(
                    "Invalid or expired refresh token"
            );
        }

        // Extract username
        String username =
                jwtUtil.extractUsername(refreshToken);

        // Find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // Get current role
        String role = user.getRoles()
                .stream()
                .findFirst()
                .map(r -> r.getName())
                .orElse("ROLE_VIEWER");

        // Generate new access token
        String newAccessToken =
                jwtUtil.generateToken(
                        username,
                        role
                );

        return Map.of(
                "accessToken",
                newAccessToken
        );
    }
}
