package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.dto.InputFilterYearDTO;
import com.saberpro.backendsoftware.dto.ReporteYearDTO;
import com.saberpro.backendsoftware.model.*;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor

public class QueryYearService {

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public List<ReporteYearDTO> filterByYear(InputFilterYearDTO filter) {
        System.out.println("Filter: " + filter);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ReporteYear> query = cb.createQuery(ReporteYear.class);
        Root<ReporteYear> reporteYear = query.from(ReporteYear.class);
        // Mantener LEFT JOIN para traer todos los ReporteYear aunque no tengan modulos
        reporteYear.fetch("modulosYear", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        if (filter.getPeriodo() != null) {
            predicates.add(cb.equal(reporteYear.get("periodoEvaluacion").get("periodo"), filter.getPeriodo()));
        }
        if (filter.getYear() != null) {
            predicates.add(cb.equal(reporteYear.get("periodoEvaluacion").get("year"), filter.getYear()));
        }
        if (filter.getMediaPeriodoMin() != null && filter.getMediaPeriodoMin() > 0) {
            predicates.add(cb.greaterThanOrEqualTo(reporteYear.get("mediaPeriodo"), filter.getMediaPeriodoMin()));
        }
        if (filter.getMediaPeriodoMax() != null && filter.getMediaPeriodoMax() > 0) {
            predicates.add(cb.lessThanOrEqualTo(reporteYear.get("mediaPeriodo"), filter.getMediaPeriodoMax()));
        }
        if (filter.getCoefVarPeriodoMin() != null && filter.getCoefVarPeriodoMin() > 0) {
            predicates.add(cb.greaterThanOrEqualTo(reporteYear.get("coeficienteVariacion"), filter.getCoefVarPeriodoMin()));
        }
        if (filter.getCoefVarPeriodoMax() != null && filter.getCoefVarPeriodoMax() > 0) {
            predicates.add(cb.lessThanOrEqualTo(reporteYear.get("coeficienteVariacion"), filter.getCoefVarPeriodoMax()));
        }
        // NO agregar filtros sobre moduloYear aquí

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<ReporteYear> typedQuery = entityManager.createQuery(query);

        EntityGraph<?> entityGraph = entityManager.getEntityGraph("ReporteYear.conRelaciones");
        typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);

        List<ReporteYear> resultados = typedQuery.getResultList();

        List<ReporteYearDTO> resultadoDTOs = new ArrayList<>();
        for (ReporteYear ry : resultados) {
            for (ModuloYear my : ry.getModulosYear()) {
                // Filtro en Java, solo si cumple:
                boolean cumpleFiltro = true;
                if (filter.getMediaModuloMin() != null && filter.getMediaModuloMin() > 0) {
                    cumpleFiltro &=  my.getMediaModuloYear() >= filter.getMediaModuloMin();
                }
                if (filter.getMediaModuloMax() != null && filter.getMediaModuloMax() > 0) {
                    cumpleFiltro &= my.getMediaModuloYear() <= filter.getMediaModuloMax();
                }
                if (filter.getCoefVarModuloMin() != null && filter.getCoefVarModuloMin() > 0) {
                    cumpleFiltro &=  my.getCoeficienteVariacionModuloYear() >= filter.getCoefVarModuloMin();
                }
                if (filter.getCoefVarModuloMax() != null && filter.getCoefVarModuloMax() > 0) {
                    cumpleFiltro &= my.getCoeficienteVariacionModuloYear() <= filter.getCoefVarModuloMax();
                }
                if (filter.getTipoModulo() != null && !filter.getTipoModulo().equalsIgnoreCase("null")) {
                    cumpleFiltro &= my.getTipoModulo() != null && my.getTipoModulo().equalsIgnoreCase(filter.getTipoModulo());
                }

                if (cumpleFiltro) {
                    ReporteYearDTO dto = new ReporteYearDTO();
                    dto.setYear(ry.getPeriodoEvaluacion().getYear());
                    dto.setPeriodo(ry.getPeriodoEvaluacion().getPeriodo());
                    dto.setMediaPeriodo(ry.getMediaPeriodo());
                    dto.setVarianzaPeriodo(ry.getVarianzaPeriodo());
                    dto.setCoefVarPeriodo(ry.getCoeficienteVariacion());
                    dto.setTipoModulo(my.getTipoModulo());
                    dto.setMediaModulo(my.getMediaModuloYear());
                    dto.setVarianzaModulo(my.getVarianzaModuloYear());
                    dto.setCoefVarModulo(my.getCoeficienteVariacionModuloYear());
                    resultadoDTOs.add(dto);
                }
            }
        }

        return resultadoDTOs;
    }




}