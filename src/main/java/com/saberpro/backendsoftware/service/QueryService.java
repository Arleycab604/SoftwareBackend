package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.Dtos.InputQueryDTO;
import com.saberpro.backendsoftware.Dtos.ReporteDTO;
import com.saberpro.backendsoftware.model.*;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.PersistenceContext;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<ReporteDTO> filtrarDatos(InputQueryDTO inputQueryDTO) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Reporte> query = cb.createQuery(Reporte.class);
        Root<Reporte> reporte = query.from(Reporte.class);
        Join<Reporte, Modulo> modulo = reporte.join("modulos", JoinType.LEFT);
        Join<Reporte, Estudiante> estudiante = reporte.join("estudiante", JoinType.LEFT);
        Join<Reporte, PeriodoEvaluacion> periodo = reporte.join("periodoEvaluacion", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (inputQueryDTO.getYear() != null && inputQueryDTO.getYear() > 0) {
            predicates.add(cb.equal(periodo.get("year"), inputQueryDTO.getYear()));
        }
        if (inputQueryDTO.getPeriodo() != null && inputQueryDTO.getPeriodo() > 0) {
            predicates.add(cb.equal(periodo.get("periodo"), inputQueryDTO.getPeriodo()));
        }
        if (inputQueryDTO.getNombreUsuario() != null && !inputQueryDTO.getNombreUsuario().isEmpty()) {
            predicates.add(cb.equal(estudiante.get("nombreEstudiante"), inputQueryDTO.getNombreUsuario()));
        }
        if (inputQueryDTO.getNombrePrograma() != null && !inputQueryDTO.getNombrePrograma().isEmpty()) {
            predicates.add(cb.equal(estudiante.get("programa").get("nombrePrograma"), inputQueryDTO.getNombrePrograma()));
        }
        if (inputQueryDTO.getPuntajeGlobalMinimo() != null && inputQueryDTO.getPuntajeGlobalMinimo() > 0) {
            predicates.add(cb.greaterThanOrEqualTo(reporte.get("puntajeGlobal"), inputQueryDTO.getPuntajeGlobalMinimo()));
        }
        if (inputQueryDTO.getPuntajeGlobalMaximo() != null && inputQueryDTO.getPuntajeGlobalMaximo() > 0) {
            predicates.add(cb.lessThanOrEqualTo(reporte.get("puntajeGlobal"), inputQueryDTO.getPuntajeGlobalMaximo()));
        }
        if (inputQueryDTO.getNivelDesempeno() != null && !inputQueryDTO.getNivelDesempeno().isEmpty()) {
            predicates.add(cb.equal(modulo.get("nivelDesempeno"), inputQueryDTO.getNivelDesempeno()));
        }
        if (inputQueryDTO.getPuntajeModuloMinimo() != null && inputQueryDTO.getPuntajeModuloMinimo() > 0) {
            predicates.add(cb.greaterThanOrEqualTo(modulo.get("puntajeModulo"), inputQueryDTO.getPuntajeModuloMinimo()));
        }
        if (inputQueryDTO.getPuntajeModuloMaximo() != null && inputQueryDTO.getPuntajeModuloMaximo() > 0) {
            predicates.add(cb.lessThanOrEqualTo(modulo.get("puntajeModulo"), inputQueryDTO.getPuntajeModuloMaximo()));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.distinct(true);

        TypedQuery<Reporte> typedQuery = entityManager.createQuery(query);
        EntityGraph<?> entityGraph = entityManager.getEntityGraph("Reporte.conRelaciones");
        typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);

        List<Reporte> reportes = typedQuery.getResultList();

        List<ReporteDTO> resultados = new ArrayList<>();
        for (Reporte reporteEntity : reportes) {
            for (Modulo moduloEntity : reporteEntity.getModulos()) {
                // Si se especifica un tipo de módulo, solo agregar si coincide
                if (inputQueryDTO.getTipoModulo() != null && !inputQueryDTO.getTipoModulo().isEmpty()) {
                    if (!moduloEntity.getTipo().equals(inputQueryDTO.getTipoModulo())) {
                        continue;
                    }
                }
                ReporteDTO dto = convertToReporteDTO(reporteEntity, moduloEntity);
                resultados.add(dto);
            }
        }

        return resultados;
    }

    private ReporteDTO convertToReporteDTO(Reporte reporte, Modulo modulo) {
        return new ReporteDTO(
                reporte.getEstudiante().getDocumento(),
                reporte.getEstudiante().getTipoDocumento(),
                reporte.getEstudiante().getCiudad(),
                reporte.getEstudiante().getTipoDeEvaluado(),
                reporte.getEstudiante().getNombreEstudiante(),
                reporte.getEstudiante().getPrograma().getSniesId(),
                reporte.getEstudiante().getPrograma().getNombrePrograma(),
                reporte.getEstudiante().getPrograma().getGrupoDeReferencia(),
                reporte.getNumeroRegistro(),
                reporte.getPeriodoEvaluacion().getYear(),
                reporte.getPeriodoEvaluacion().getPeriodo(),
                reporte.getPuntajeGlobal(),
                reporte.getPercentilGlobal(),
                reporte.getNovedades(),
                modulo.getTipo(),
                modulo.getPuntajeModulo(),
                modulo.getNivelDesempeno(),
                modulo.getPercentilModulo()
        );
    }
}
