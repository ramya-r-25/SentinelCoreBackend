package org.example.sentinelcorebackend.Config;

import org.example.sentinelcorebackend.Security.JwtFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // CORS preflight
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // Login
                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // -------------------------
                        // ASSETS
                        // -------------------------

                        // ADMIN + OPERATOR + VIEWER
                        // can view assets
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/assets/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "OPERATOR",
                                "VIEWER"
                        )

                        // ADMIN + OPERATOR
                        // can create assets
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/assets/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "OPERATOR"
                        )

                        // ADMIN + OPERATOR
                        // can update assets
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/assets/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "OPERATOR"
                        )

                        // ADMIN only
                        // can delete assets
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/assets/**"
                        ).hasRole("ADMIN")

                        // -------------------------
                        // ALERTS
                        // -------------------------

                        .requestMatchers(
                                "/api/alerts/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "OPERATOR"
                        )

                        // Everything else
                        .anyRequest().authenticated()
                )

                // Add JWT filter
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type"
                )
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}