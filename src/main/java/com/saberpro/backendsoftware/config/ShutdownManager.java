package com.saberpro.backendsoftware.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShutdownManager {

    private final HikariDataSource dataSource;

    @PreDestroy
    public void onShutdown() {
        System.out.println("Cerrando conexiones del pool...");
        dataSource.close(); // Deberia cerrar todo el pool correctamente
    }
}