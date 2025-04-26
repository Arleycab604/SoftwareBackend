package com.saberpro.backendsoftware.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;

    public JwtUtil() {
        // Genera clave segura de al menos 256 bits
        String secret = "clave_secreta_para_jwt_segura_y_larga_123456789";
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username) {
        long now = System.currentTimeMillis();
        // 2 horas
        long EXPIRATION_TIME = 1000 * 60 * 60 * 2;
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(now))
                .expiration(new Date(now + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }
}