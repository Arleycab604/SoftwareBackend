package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    @Query("SELECT DISTINCT m.tipo FROM Modulo m WHERE m.codModulo.year = :year AND m.codModulo.periodo = :periodo")
    List<String> findDistinctTiposByYearAndPeriodo(@Param("year") int year, @Param("periodo") int periodo);

    @Query("SELECT m FROM Modulo m WHERE m.tipo = :tipo AND m.codModulo.year = :year AND m.codModulo.periodo = :periodo")
    List<Modulo> findByTipoAndYearAndPeriodo(@Param("tipo") String tipo, @Param("year") int year, @Param("periodo") int periodo);
}
