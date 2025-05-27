package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.PeriodoEvaluacion;
import com.saberpro.backendsoftware.model.Reporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.stereotype.Repository;



@Repository
public interface ReporteRepository extends JpaRepository<Reporte, String> {
    List<Reporte> findByPeriodoEvaluacion(PeriodoEvaluacion periodoEvaluacion);
    List<Reporte> findByEstudianteDocumento(Long documento);

    @EntityGraph(value = "Reporte.dtoGraph", type = EntityGraph.EntityGraphType.FETCH)
        // para cargar las relaciones definidas en NamedEntityGraph automáticamente
    Page<Reporte> findAll(Specification<Reporte> spec, Pageable pageable);

}