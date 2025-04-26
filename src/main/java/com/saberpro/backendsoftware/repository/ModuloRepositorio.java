package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ModuloRepositorio extends JpaRepository<Modulo, Long> {
    Optional<Modulo> findByNumeroRegistro(String numeroRegistro);

    // Buscar por tipo
    List<Modulo> findByTipo(String tipo);

    // Buscar por tipo Y numeroRegistro
    Optional<Modulo> findByTipoAndNumeroRegistro(String tipo, String numeroRegistro);

    // Si quieres múltiples resultados (por si hay más de uno con mismo tipo y registro)
    List<Modulo> findAllByTipoAndNumeroRegistro(String tipo, String numeroRegistro);
}