package com.saberpro.backendsoftware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // Esto incluye @ComponentScan y @EnableJpaRepositories si estás en el paquete raíz
public class BackendSoftwareApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendSoftwareApplication.class, args);
    }
}