package org.example.sentinelcorebackend.Controller;


import org.example.sentinelcorebackend.Util.Jwtutil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

        import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final Jwtutil jwtUtil;

    @PostMapping("/login")
    public Map<String, String> login(
            @RequestBody Map<String, String> credentials) {

        String username = credentials.get("username");
        String password = credentials.get("password");

        // Temporary login for Milestone 3
        if ("admin".equals(username) &&
                "admin123".equals(password)) {

            String token = jwtUtil.generateToken(username);

            return Map.of("token", token);
        }

        throw new RuntimeException("Invalid credentials");
    }
}