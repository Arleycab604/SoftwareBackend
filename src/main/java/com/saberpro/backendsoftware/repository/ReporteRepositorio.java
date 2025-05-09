package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.PeriodoEvaluacion;
import com.saberpro.backendsoftware.model.Reporte;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.stereotype.Repository;



@Repository
public interface ReporteRepositorio extends JpaRepository<Reporte, String> {
    List<Reporte> findByPeriodoEvaluacion(PeriodoEvaluacion periodoEvaluacion);
    List<Reporte> findByEstudianteDocumento(Long documento);
    @EntityGraph(attributePaths = {
            "estudiante",
            "estudiante.programa",
            "periodoEvaluacion",
            "modulos"
    })
    @Override
    List<Reporte> findAll();
}