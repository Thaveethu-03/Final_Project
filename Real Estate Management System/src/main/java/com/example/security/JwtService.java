package com.example.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.enums.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService 
{

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret:change-this-secret-key-to-a-long-random-value-1234567890}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) 
    {
        // HS256 needs at least 32 bytes
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, Role role) 
    {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String extractUsername(String token) 
    {
        return parse(token).getSubject();
    }

    public String extractRole(String token) 
    {
        return parse(token).get("role", String.class);
    }

    public long getExpirationMs() 
    {
        return expirationMs;
    }

    private Claims parse(String token) 
    {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
