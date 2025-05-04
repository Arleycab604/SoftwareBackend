package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.ModuloYear;
import com.saberpro.backendsoftware.model.PeriodoEvaluacion;
import com.saberpro.backendsoftware.model.ReporteYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuloYearRepositorio extends JpaRepository<ModuloYear, Long> {
    List<ModuloYear> findByReporteYear(ReporteYear reporteYear);
    void deleteByReporteYear(ReporteYear reporteYear);

}