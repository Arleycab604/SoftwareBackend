package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.Modulo;
import com.saberpro.backendsoftware.model.PeriodoEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    @Query("SELECT m FROM Modulo m WHERE m.reporte.numeroRegistro = :numeroRegistro")
    List<Modulo> findByReporteNumeroRegistro(@Param("numeroRegistro") String numeroRegistro);

    @Query("SELECT m FROM Modulo m WHERE m.tipo = :tipo AND m.reporte.periodoEvaluacion = :periodoEvaluacion")
    List<Modulo> findByTipoAndPeriodoEvaluacion(@Param("tipo") String tipo, @Param("periodoEvaluacion")PeriodoEvaluacion periodoEvaluacion);

    @Query("SELECT m FROM Modulo m WHERE m.tipo = :tipo AND m.reporte.numeroRegistro = :numeroRegistro")
    Optional<Modulo> findByTipoAndNumeroRegistro(@Param("tipo") String tipo, @Param("numeroRegistro") String numeroRegistro);

    @Query("SELECT DISTINCT m.tipo FROM Modulo m WHERE m.reporte.periodoEvaluacion = :periodoEvaluacion")
    List<String> findDistinctTiposByPeriodoEvaluacion(@Param("periodoEvaluacion") PeriodoEvaluacion periodoEvaluacion);
}