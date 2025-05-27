package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.dto.InputQueryDTO;
import com.saberpro.backendsoftware.dto.ReporteDTO;
import com.saberpro.backendsoftware.model.*;
import com.saberpro.backendsoftware.model.usuarios.Estudiante;
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
        System.out.println("InputQueryDTO: " + inputQueryDTO);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Reporte> query = cb.createQuery(Reporte.class);
        Root<Reporte> reporte = query.from(Reporte.class);
        Join<Reporte, Estudiante> estudiante = reporte.join("estudiante", JoinType.LEFT);
        Join<Reporte, PeriodoEvaluacion> periodo = reporte.join("periodoEvaluacion", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        if (inputQueryDTO.getYear() != null && inputQueryDTO.getYear() > 0) {
            predicates.add(cb.equal(periodo.get("year"), inputQueryDTO.getYear()));}
        if (inputQueryDTO.getPeriodo() != null && inputQueryDTO.getPeriodo() > 0) {
            predicates.add(cb.equal(periodo.get("periodo"), inputQueryDTO.getPeriodo()));}
        if (inputQueryDTO.getNombreUsuario() != null && !inputQueryDTO.getNombreUsuario().isEmpty()) {
            predicates.add(cb.equal(estudiante.get("usuario").get("nombreUsuario"), inputQueryDTO.getNombreUsuario()));}
        if (inputQueryDTO.getDocumento() != null && !inputQueryDTO.getDocumento().isEmpty()) {
            predicates.add(cb.equal(estudiante.get("documento"), inputQueryDTO.getDocumento()));}
        if (inputQueryDTO.getNombrePrograma() != null && !inputQueryDTO.getNombrePrograma().isEmpty()) {
            predicates.add(cb.equal(estudiante.get("programa").get("nombrePrograma"), inputQueryDTO.getNombrePrograma()));}
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.distinct(true);
        TypedQuery<Reporte> typedQuery = entityManager.createQuery(query);
        EntityGraph<?> entityGraph = entityManager.getEntityGraph("Reporte.conRelaciones");
        typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);
        List<Reporte> reportes = typedQuery.getResultList();

        List<ReporteDTO> resultados = new ArrayList<>();
        for (Reporte reporteEntity : reportes) {
            if (inputQueryDTO.getPuntajeGlobalMinimo() != null) {
                if (reporteEntity.getPuntajeGlobal() < inputQueryDTO.getPuntajeGlobalMinimo()) {
                    continue;}}
            if (inputQueryDTO.getPuntajeGlobalMaximo() != null) {
                if (reporteEntity.getPuntajeGlobal() > inputQueryDTO.getPuntajeGlobalMaximo()) {
                    continue;}}
            if(inputQueryDTO.getNombreUsuario() != null && !inputQueryDTO.getNombreUsuario().isEmpty()) {
                if (!reporteEntity.getEstudiante().getUsuario().getNombreUsuario().equalsIgnoreCase(inputQueryDTO.getNombreUsuario())) {
                    continue;}}
            for (Modulo moduloEntity : reporteEntity.getModulos()) {
                if (inputQueryDTO.getTipoModulo() != null && !inputQueryDTO.getTipoModulo().isEmpty()) {
                    if (!moduloEntity.getTipo().equalsIgnoreCase(inputQueryDTO.getTipoModulo())) {continue;}}

                if (inputQueryDTO.getNivelDesempeno() != null && !inputQueryDTO.getNivelDesempeno().isEmpty()) {
                    if (!moduloEntity.getNivelDesempeno().equalsIgnoreCase(inputQueryDTO.getNivelDesempeno())) {continue;}}

                if (inputQueryDTO.getPuntajeModuloMinimo() != null) {
                    if (moduloEntity.getPuntajeModulo() < inputQueryDTO.getPuntajeModuloMinimo()) {continue;}}
                if (inputQueryDTO.getPuntajeModuloMaximo() != null) {
                    if (moduloEntity.getPuntajeModulo() > inputQueryDTO.getPuntajeModuloMaximo()) {continue;}}

                ReporteDTO dto = convertToReporteDTO(reporteEntity, moduloEntity);
                resultados.add(dto);
            }
        }
        System.out.println("Resultados obtenidos: " + resultados.size());
        return resultados;
    }

    private ReporteDTO convertToReporteDTO(Reporte reporte, Modulo modulo) {
        return new ReporteDTO(
                reporte.getEstudiante().getDocumento(),
                reporte.getEstudiante().getTipoDocumento(),
                reporte.getEstudiante().getCiudad(),
                reporte.getEstudiante().getTipoDeEvaluado(),
                reporte.getEstudiante().getUsuario().getNombreUsuario(),
                reporte.getEstudiante().getUsuario().getPrograma().getSniesId(),
                reporte.getEstudiante().getUsuario().getPrograma().getNombrePrograma(),
                reporte.getEstudiante().getUsuario().getPrograma().getGrupoDeReferencia(),
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
