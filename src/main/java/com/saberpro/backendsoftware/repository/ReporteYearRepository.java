package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.PeriodoEvaluacion;
import com.saberpro.backendsoftware.model.ReporteYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import org.springframework.stereotype.Repository;
@Repository
public interface ReporteYearRepository extends JpaRepository<ReporteYear, Integer> {
    void deleteByPeriodoEvaluacion(PeriodoEvaluacion periodoEvaluacion);
    Optional<ReporteYear> findByPeriodoEvaluacion(PeriodoEvaluacion periodoEvaluacion);
}