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

    @PostMapping("/login")
    public Map<String, String> login(
            @RequestBody Map<String, String> credentials) {

        String username = credentials.get("username");
        String password = credentials.get("password");

        // Find user
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

        // Get role
        String role = user.getRoles()
                .stream()
                .findFirst()
                .map(r -> r.getName())
                .orElse("ROLE_VIEWER");

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
}