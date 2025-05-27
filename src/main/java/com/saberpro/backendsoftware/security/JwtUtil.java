package com.saberpro.backendsoftware.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
public class JwtUtil {

    private final String secret = "qt5I7leh5vFdRb4V+nbITMhsNqse/Z8JH5xwX8ZzkJ4=";
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());

    public String generateToken(String username, String tipoDeUsuario) {
        long now = System.currentTimeMillis();
        long EXPIRATION_TIME = 1000 * 60 * 60 * 2; // 2 horas

        return Jwts.builder()
                .subject(username)
                .claim("tipoDeUsuario", tipoDeUsuario)
                .issuedAt(new Date(now))
                .expiration(new Date(now + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public String extractTipoDeUsuario(String token) {
        try {
            return (String) Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("tipoDeUsuario");
        } catch (Exception e) {
            return null;
        }
    }

    // Método opcional para ver todo el payload decodificado como JSON (no siempre necesario)
    public Map<String, Object> decodeToken(String token) {
        try {
            String payload = token.split("\\.")[1];
            String decoded = new String(Base64.getDecoder().decode(payload), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(decoded, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al decodificar el token", e);
        }
    }
}
