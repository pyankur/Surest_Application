package com.surest.management.Surest_Management_App.config;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Map;


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public String generateToken(String username, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(username)
                .setClaims(Map.of("role", role))
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public Jws<Claims> validate(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes())).build()
                .parseClaimsJws(token);
    }




}
