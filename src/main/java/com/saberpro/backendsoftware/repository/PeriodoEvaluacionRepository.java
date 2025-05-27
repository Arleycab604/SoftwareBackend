package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.PeriodoEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodoEvaluacionRepository extends JpaRepository<PeriodoEvaluacion, Long> {
    Optional<PeriodoEvaluacion> findByYearAndPeriodo(int year, int periodo);
}