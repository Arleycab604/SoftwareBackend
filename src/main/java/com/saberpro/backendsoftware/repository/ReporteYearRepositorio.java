package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.PeriodoEvaluacion;
import com.saberpro.backendsoftware.model.ReporteYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Period;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
@Repository
public interface ReporteYearRepositorio extends JpaRepository<ReporteYear, Integer> {
    void deleteByPeriodoEvaluacion(PeriodoEvaluacion periodoEvaluacion);
    Optional<ReporteYear> findByPeriodoEvaluacion(PeriodoEvaluacion periodoEvaluacion);
}