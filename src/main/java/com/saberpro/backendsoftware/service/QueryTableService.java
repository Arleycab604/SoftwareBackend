package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.Dtos.InputQueryDTO;
import com.saberpro.backendsoftware.Dtos.ReporteDTO;
import com.saberpro.backendsoftware.model.*;
import com.saberpro.backendsoftware.repository.ReporteRepositorio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryTableService {

    @PersistenceContext
    private final EntityManager entityManager;

    public List<ReporteDTO> filtrarReportes(InputQueryDTO inputQueryDTO) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ReporteDTO> query = cb.createQuery(ReporteDTO.class);
        Root<Reporte> reporte = query.from(Reporte.class);

        // Joins
        Join<Reporte, Estudiante> estudiante = reporte.join("documento");
        Join<Estudiante, Usuario> usuario = estudiante.join("nombreUsuario");
        Join<Usuario, Programa> programa = usuario.join("programa");
        Join<Reporte, Modulo> modulo = reporte.join("modulos", JoinType.LEFT);

        // Selección de columnas
        query.select(cb.construct(
                ReporteDTO.class,
                estudiante.get("documento"),
                estudiante.get("tipoDocumento"),
                estudiante.get("ciudad"),
                estudiante.get("tipoDeEvaluado"),
                usuario.get("nombreUsuario"),
                usuario.get("tipoDeUsuario"),
                programa.get("sniesId"),
                programa.get("programa"),
                programa.get("grupoDeReferencia"),
                reporte.get("numero_Registro"),
                reporte.get("year"),
                reporte.get("periodo"),
                reporte.get("puntajeGlobal"),
                reporte.get("percentilGlobal"),
                reporte.get("novedades"),
                modulo.get("tipo"),
                modulo.get("puntajeModulo"),
                modulo.get("nivelDesempeno"),
                modulo.get("percentilNacional")
        ));

        // Lista de predicados (filtros dinámicos)
        List<Predicate> predicates = new ArrayList<>();

        if (inputQueryDTO.getYear() > 0) {
            predicates.add(cb.equal(reporte.get("year"), inputQueryDTO.getYear()));
        }
        if (inputQueryDTO.getPeriodo() > 0) {
            predicates.add(cb.equal(reporte.get("periodo"), inputQueryDTO.getPeriodo()));
        }
        if (inputQueryDTO.getPuntajeGlobalMinimo() > 0) {
            predicates.add(cb.greaterThanOrEqualTo(reporte.get("puntajeGlobal"), inputQueryDTO.getPuntajeGlobalMinimo()));
        }
        if (inputQueryDTO.getPuntajeGlobalMaximo() > 0) {
            predicates.add(cb.lessThanOrEqualTo(reporte.get("puntajeGlobal"), inputQueryDTO.getPuntajeGlobalMaximo()));
        }
        if (!inputQueryDTO.getNombreUsuario().equals("*")) {
            predicates.add(cb.equal(usuario.get("nombreUsuario"), inputQueryDTO.getNombreUsuario()));
        }
        if (!inputQueryDTO.getNombrePrograma().isEmpty()) {
            predicates.add(cb.equal(programa.get("programa"), inputQueryDTO.getNombrePrograma()));
        }
        if (!inputQueryDTO.getGrupoDeReferencia().isEmpty()) {
            predicates.add(cb.equal(programa.get("grupoDeReferencia"), inputQueryDTO.getGrupoDeReferencia()));
        }
        if (inputQueryDTO.getTipoModulo() != null && !inputQueryDTO.getTipoModulo().isEmpty()) {
            predicates.add(cb.equal(modulo.get("tipo"), inputQueryDTO.getTipoModulo()));
        }
        if (inputQueryDTO.getPuntajeMinimoModulo() > 0) {
            predicates.add(cb.greaterThanOrEqualTo(modulo.get("puntajeModulo"), inputQueryDTO.getPuntajeMinimoModulo()));
        }
        if (inputQueryDTO.getPuntajeMaximoModulo() > 0) {
            predicates.add(cb.lessThanOrEqualTo(modulo.get("puntajeModulo"), inputQueryDTO.getPuntajeMaximoModulo()));
        }

        // Aplicar predicados a la consulta
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Ejecutar la consulta
        return entityManager.createQuery(query).getResultList();
    }
}