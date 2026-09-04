package org.example.sentinelcorebackend.Util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class Jwtutil {

    // ==========================================
    // SECRET KEY
    // ==========================================

    private final SecretKey key =
            Keys.secretKeyFor(SignatureAlgorithm.HS256);


    // ==========================================
    // TOKEN EXPIRATION
    // ==========================================

    // Access token - 15 minutes
    private final long ACCESS_TOKEN_EXPIRATION =
            1000L * 60 * 15;

    // Refresh token - 7 days
    private final long REFRESH_TOKEN_EXPIRATION =
            1000L * 60 * 60 * 24 * 7;


    // ==========================================
    // GENERATE ACCESS TOKEN
    // ==========================================

    public String generateToken(
            String username,
            String role) {

        return Jwts.builder()
                .setSubject(username)

                // Store user's role in JWT
                .claim("role", role)

                .setIssuedAt(new Date())

                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + ACCESS_TOKEN_EXPIRATION
                        )
                )

                .signWith(key)

                .compact();
    }


    // ==========================================
    // GENERATE REFRESH TOKEN
    // ==========================================

    public String generateRefreshToken(
            String username) {

        return Jwts.builder()
                .setSubject(username)

                .setIssuedAt(new Date())

                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + REFRESH_TOKEN_EXPIRATION
                        )
                )

                .signWith(key)

                .compact();
    }


    // ==========================================
    // EXTRACT USERNAME
    // ==========================================

    public String extractUsername(
            String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    // ==========================================
    // EXTRACT ROLE
    // ==========================================

    public String extractRole(
            String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }


    // ==========================================
    // VALIDATE TOKEN
    // ==========================================

    public boolean isTokenValid(
            String token) {

        try {

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (JwtException e) {

            return false;
        }
    }
}