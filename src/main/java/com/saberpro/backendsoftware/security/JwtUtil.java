package com.saberpro.backendsoftware.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    // La clave secreta que se usa para firmar el token
    private final String secret = "qt5I7leh5vFdRb4V+nbITMhsNqse/Z8JH5xwX8ZzkJ4=";
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());

    public JwtUtil() {
    }

    // Singleton para obtener la instancia de JwtUtil
    private static JwtUtil instance;

    public static JwtUtil getInstance() {
        if (instance == null) {
            instance = new JwtUtil();
        }
        return instance;
    }

    // Generar un token JWT
    public String generateToken(String username, String tipoDeUsuario) {
        long now = System.currentTimeMillis();
        long EXPIRATION_TIME = 1000 * 60 * 60 * 2; // 2 horas en milisegundos

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);  // El nombre de usuario
        claims.put("tipoDeUsuario", tipoDeUsuario);  // Tipo de usuario
        claims.put("iat", new Date(now));  // Fecha de creación del token
        claims.put("exp", new Date(now + EXPIRATION_TIME));  // Fecha de expiración

        return Jwts.builder()
                .addClaims(claims)  // Añadir los claims al token
                .signWith(key)  // Firmar el token con la clave
                .compact();  // Crear el token
    }

    // Decodificar un token JWT
    public Map<String, Object> decodeToken(String token) throws JsonProcessingException {
        String payload = token.split("\\.")[1];  // Extraer el payload en base64
        String decoded = new String(Base64.getDecoder().decode(payload), StandardCharsets.UTF_8);

        // Convertir el JSON en un mapa
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(decoded, Map.class);
    }

    // Validar el token: verificar que no esté expirado y que esté firmado correctamente
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

    // Obtener el nombre de usuario desde el token JWT
    public String getUsernameFromToken(String token) {
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

}
