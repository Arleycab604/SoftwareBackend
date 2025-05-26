package com.saberpro.backendsoftware;

import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(scanBasePackages = "com.saberpro.backendsoftware")  // Esto incluye @ComponentScan y @EnableJpaRepositories si estás en el paquete raíz
public class BackendSoftwareApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendSoftwareApplication.class, args);
    }
    @PreDestroy
    public void onDestroy() {
        // Aquí puedes realizar cualquier limpieza necesaria antes de que la aplicación se cierre
        System.out.println("La aplicación está cerrando...");
    }
}