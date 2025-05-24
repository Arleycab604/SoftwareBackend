package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.dto.InputFilterYearDTO;
import com.saberpro.backendsoftware.dto.ReporteYearDTO;
import com.saberpro.backendsoftware.model.ModuloYear;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ModuloYear> query = cb.createQuery(ModuloYear.class);
        Root<ModuloYear> moduloYear = query.from(ModuloYear.class);
        Join<Object, Object> reporteYear = moduloYear.join("reporteYear", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (filter.getMediaPeriodoMin() != null && filter.getMediaPeriodoMax() != null) {
            predicates.add(cb.between(reporteYear.get("mediaPeriodo"), filter.getMediaPeriodoMin(), filter.getMediaPeriodoMax()));
        } else {
            if (filter.getMediaPeriodoMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(reporteYear.get("mediaPeriodo"), filter.getMediaPeriodoMin()));
            }
            if (filter.getMediaPeriodoMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(reporteYear.get("mediaPeriodo"), filter.getMediaPeriodoMax()));
            }
        }

        if (filter.getMediaModuloMin() != null && filter.getMediaModuloMax() != null) {
            predicates.add(cb.between(moduloYear.get("mediaModulo"), filter.getMediaModuloMin(), filter.getMediaModuloMax()));
        } else {
            if (filter.getMediaModuloMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(moduloYear.get("mediaModulo"), filter.getMediaModuloMin()));
            }
            if (filter.getMediaModuloMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(moduloYear.get("mediaModulo"), filter.getMediaModuloMax()));
            }
        }

        if (filter.getCoefVarPeriodoMin() != null && filter.getCoefVarPeriodoMax() != null) {
            predicates.add(cb.between(reporteYear.get("coefVarPeriodo"), filter.getCoefVarPeriodoMin(), filter.getCoefVarPeriodoMax()));
        } else {
            if (filter.getCoefVarPeriodoMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(reporteYear.get("coefVarPeriodo"), filter.getCoefVarPeriodoMin()));
            }
            if (filter.getCoefVarPeriodoMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(reporteYear.get("coefVarPeriodo"), filter.getCoefVarPeriodoMax()));
            }
        }

        if (filter.getCoefVarModuloMin() != null && filter.getCoefVarModuloMax() != null) {
            predicates.add(cb.between(moduloYear.get("coefVarModulo"), filter.getCoefVarModuloMin(), filter.getCoefVarModuloMax()));
        } else {
            if (filter.getCoefVarModuloMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(moduloYear.get("coefVarModulo"), filter.getCoefVarModuloMin()));
            }
            if (filter.getCoefVarModuloMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(moduloYear.get("coefVarModulo"), filter.getCoefVarModuloMax()));
            }
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        List<ModuloYear> resultados = entityManager.createQuery(query).getResultList();

        // Mapeo de ModuloYear a ReporteYearDTO
        List<ReporteYearDTO> resultadoDTOs = new ArrayList<>();
        for (ModuloYear my : resultados) {
            ReporteYearDTO dto = new ReporteYearDTO();
            dto.setYear(my.getReporteYear().getPeriodoEvaluacion().getYear());
            dto.setPeriodo(my.getReporteYear().getPeriodoEvaluacion().getPeriodo());
            dto.setMediaPeriodo(my.getReporteYear().getMediaPeriodo());
            dto.setVarianzaPeriodo(my.getReporteYear().getVarianzaPeriodo());
            dto.setCoefVarPeriodo(my.getReporteYear().getCoeficienteVariacion());
            dto.setTipoModulo(my.getTipoModulo());
            dto.setMediaModulo(my.getMediaModuloYear());
            dto.setVarianzaModulo(my.getVarianzaModuloYear());
            dto.setCoefVarModulo(my.getCoeficienteVariacionModuloYear());
            resultadoDTOs.add(dto);
        }

        return resultadoDTOs;
    }
}