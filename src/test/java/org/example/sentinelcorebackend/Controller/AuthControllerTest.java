package org.example.sentinelcorebackend.Controller;

import org.example.sentinelcorebackend.Entity.Role;
import org.example.sentinelcorebackend.Entity.User;
import org.example.sentinelcorebackend.Repository.UserRepository;
import org.example.sentinelcorebackend.Util.Jwtutil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private Jwtutil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private User adminUser;

    @BeforeEach
    void setUp() {
        Role adminRole = Role.builder()
                .id(1L)
                .name("ROLE_ADMIN")
                .build();

        adminUser = User.builder()
                .id(1L)
                .username("admin")
                .password("encodedPassword123")
                .email("admin@sentinel.com")
                .roles(Set.of(adminRole))
                .build();
    }

    @Test
    void login_shouldReturnAccessAndRefreshToken_whenCredentialsAreValid() {
        Map<String, String> credentials = Map.of(
                "username", "admin",
                "password", "secret123"
        );

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("secret123", "encodedPassword123")).thenReturn(true);
        when(jwtUtil.generateToken("admin", "ROLE_ADMIN")).thenReturn("mock-access-token");
        when(jwtUtil.generateRefreshToken("admin")).thenReturn("mock-refresh-token");

        Map<String, String> response = authController.login(credentials);

        assertNotNull(response);
        assertEquals("mock-access-token", response.get("accessToken"));
        assertEquals("mock-refresh-token", response.get("refreshToken"));

        verify(userRepository, times(1)).findByUsername("admin");
        verify(passwordEncoder, times(1)).matches("secret123", "encodedPassword123");
    }

    @Test
    void login_shouldThrowException_whenUserNotFound() {
        Map<String, String> credentials = Map.of(
                "username", "unknown",
                "password", "secret123"
        );

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authController.login(credentials)
        );

        assertEquals("Invalid credentials", exception.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_shouldThrowException_whenPasswordIsIncorrect() {
        Map<String, String> credentials = Map.of(
                "username", "admin",
                "password", "wrongpassword"
        );

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword123")).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authController.login(credentials)
        );

        assertEquals("Invalid credentials", exception.getMessage());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void refresh_shouldReturnNewAccessToken_whenRefreshTokenIsValid() {
        Map<String, String> requestBody = Map.of(
                "refreshToken", "valid-refresh-token"
        );

        when(jwtUtil.isTokenValid("valid-refresh-token")).thenReturn(true);
        when(jwtUtil.extractUsername("valid-refresh-token")).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(jwtUtil.generateToken("admin", "ROLE_ADMIN")).thenReturn("new-mock-access-token");

        Map<String, String> response = authController.refresh(requestBody);

        assertNotNull(response);
        assertEquals("new-mock-access-token", response.get("accessToken"));

        verify(jwtUtil, times(1)).isTokenValid("valid-refresh-token");
        verify(jwtUtil, times(1)).extractUsername("valid-refresh-token");
    }

    @Test
    void refresh_shouldThrowException_whenRefreshTokenIsInvalid() {
        Map<String, String> requestBody = Map.of(
                "refreshToken", "invalid-refresh-token"
        );

        when(jwtUtil.isTokenValid("invalid-refresh-token")).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authController.refresh(requestBody)
        );

        assertEquals("Invalid or expired refresh token", exception.getMessage());
        verify(jwtUtil, never()).extractUsername(anyString());
    }
}
