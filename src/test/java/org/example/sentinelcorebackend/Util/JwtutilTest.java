package org.example.sentinelcorebackend.Util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtutilTest {

    private Jwtutil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new Jwtutil();
    }

    @Test
    void generateToken_and_extractUsername_and_extractRole() {
        String username = "adminUser";
        String role = "ROLE_ADMIN";

        String token = jwtUtil.generateToken(username, role);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtUtil.isTokenValid(token));
        assertEquals(username, jwtUtil.extractUsername(token));
        assertEquals(role, jwtUtil.extractRole(token));
    }

    @Test
    void generateRefreshToken_shouldGenerateValidTokenWithUsername() {
        String username = "operatorUser";

        String refreshToken = jwtUtil.generateRefreshToken(username);

        assertNotNull(refreshToken);
        assertTrue(jwtUtil.isTokenValid(refreshToken));
        assertEquals(username, jwtUtil.extractUsername(refreshToken));
    }

    @Test
    void isTokenValid_shouldReturnFalse_forInvalidToken() {
        String invalidToken = "invalid.jwt.token";

        boolean isValid = jwtUtil.isTokenValid(invalidToken);

        assertFalse(isValid);
    }
}
