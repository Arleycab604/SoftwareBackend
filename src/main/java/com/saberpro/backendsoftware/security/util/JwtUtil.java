package com.saberpro.backendsoftware.security.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//poliridim
@Component
public class JwtUtil {

    private final Key key;

    public JwtUtil() {
        // Genera clave segura de al menos 256 bits
        String secret = "qt5I7leh5vFdRb4V+nbITMhsNqse/Z8JH5xwX8ZzkJ4=";
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username, String tipoDeUsuario) {
        long now = System.currentTimeMillis();
        // 2 horas
        long EXPIRATION_TIME = 1000 * 60 * 60 * 2; //Milisegundos

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("tipoDeUsuario", tipoDeUsuario);
        claims.put("iat", new Date(now)); //creacion token
        claims.put("exp", new Date(now + EXPIRATION_TIME));

        return Jwts.builder()
                .addClaims(claims) // Usar addClaims para añadir los claims
                .signWith(key)
                .compact();
    }

}