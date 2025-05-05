package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.Dtos.InputQueryDTO;
import com.saberpro.backendsoftware.Dtos.ReporteDTO;
import com.saberpro.backendsoftware.model.*;
import com.saberpro.backendsoftware.repository.*;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;



import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryService {

    @PersistenceContext
    private EntityManager entityManager;

    private final EstudianteRepositorio estudianteRepo;
    private final UsuarioRepositorio usuarioRepo;
    private final ReporteRepositorio reporteRepo;
    private final ProgramaRepositorio programaRepo;
    private final ModuloRepositorio moduloRepo;

    @Transactional
    public List<ReporteDTO> filtrarDatos(InputQueryDTO inputQueryDTO) {
        System.out.println("inputQUERYDTO = " + inputQueryDTO);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Reporte> query = cb.createQuery(Reporte.class);
        Root<Reporte> reporte = query.from(Reporte.class);
        Join<Reporte, Modulo> modulo = reporte.join("modulos", JoinType.LEFT);
        Join<Reporte, Estudiante> estudiante = reporte.join("estudiante", JoinType.LEFT);
        Join<Reporte, PeriodoEvaluacion> periodo = reporte.join("periodoEvaluacion", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        List<String> filtrosAplicados = new ArrayList<>();


        if (inputQueryDTO.getYear() != null && inputQueryDTO.getYear() > 0) {
            predicates.add(cb.equal(periodo.get("year"), inputQueryDTO.getYear()));
            filtrosAplicados.add("periodo.year = " + inputQueryDTO.getYear());
        }
        if (inputQueryDTO.getPeriodo() != null && inputQueryDTO.getPeriodo() > 0) {
            predicates.add(cb.equal(periodo.get("periodo"), inputQueryDTO.getPeriodo()));
            filtrosAplicados.add("periodo.periodo = " + inputQueryDTO.getPeriodo());
        }
        if (inputQueryDTO.getNombreUsuario() != null && !inputQueryDTO.getNombreUsuario().isEmpty()) {
            predicates.add(cb.equal(estudiante.get("nombreEstudiante"), inputQueryDTO.getNombreUsuario()));
            filtrosAplicados.add("estudiante.nombreEstudiante = '" + inputQueryDTO.getNombreUsuario() + "'");
        }
        if (inputQueryDTO.getNombrePrograma() != null && !inputQueryDTO.getNombrePrograma().isEmpty()) {
            predicates.add(cb.equal(estudiante.get("programa").get("nombrePrograma"), inputQueryDTO.getNombrePrograma()));
            filtrosAplicados.add("programa.nombrePrograma = '" + inputQueryDTO.getNombrePrograma() + "'");
        }
        if (inputQueryDTO.getGrupoDeReferencia() != null && !inputQueryDTO.getGrupoDeReferencia().isEmpty()) {
            predicates.add(cb.equal(estudiante.get("programa").get("grupoDeReferencia"), inputQueryDTO.getGrupoDeReferencia()));
            filtrosAplicados.add("programa.grupoDeReferencia = '" + inputQueryDTO.getGrupoDeReferencia() + "'");
        }
        if (inputQueryDTO.getNumeroRegistro() != null && !inputQueryDTO.getNumeroRegistro().isEmpty()) {
            predicates.add(cb.equal(reporte.get("numeroRegistro"), inputQueryDTO.getNumeroRegistro()));
            filtrosAplicados.add("reporte.numeroRegistro = '" + inputQueryDTO.getNumeroRegistro() + "'");
        }
        if (inputQueryDTO.getPuntajeGlobalMinimo() != null && inputQueryDTO.getPuntajeGlobalMinimo() > 0) {
            predicates.add(cb.greaterThanOrEqualTo(reporte.get("puntajeGlobal"), inputQueryDTO.getPuntajeGlobalMinimo()));
            filtrosAplicados.add("reporte.puntajeGlobal >= " + inputQueryDTO.getPuntajeGlobalMinimo());
        }
        if (inputQueryDTO.getPuntajeGlobalMaximo() != null && inputQueryDTO.getPuntajeGlobalMaximo() > 0) {
            predicates.add(cb.lessThanOrEqualTo(reporte.get("puntajeGlobal"), inputQueryDTO.getPuntajeGlobalMaximo()));
            filtrosAplicados.add("reporte.puntajeGlobal <= " + inputQueryDTO.getPuntajeGlobalMaximo());
        }
        if (inputQueryDTO.getPercentilGlobal() != null && inputQueryDTO.getPercentilGlobal() > 0) {
            predicates.add(cb.equal(reporte.get("percentilGlobal"), inputQueryDTO.getPercentilGlobal()));
            filtrosAplicados.add("reporte.percentilGlobal = " + inputQueryDTO.getPercentilGlobal());
        }
        if (inputQueryDTO.getNovedades() != null && !inputQueryDTO.getNovedades().isEmpty()) {
            predicates.add(cb.equal(reporte.get("novedades"), inputQueryDTO.getNovedades()));
            filtrosAplicados.add("reporte.novedades = '" + inputQueryDTO.getNovedades() + "'");
        }
        if (inputQueryDTO.getTipoModulo() != null && !inputQueryDTO.getTipoModulo().isEmpty()) {
            String[] tipoModuloArray = inputQueryDTO.getTipoModulo().split(",");
            predicates.add(modulo.get("tipo").in((Object[]) tipoModuloArray));

            filtrosAplicados.add("modulo.tipo IN (" + String.join(", ", tipoModuloArray) + ")");
        }
        if (inputQueryDTO.getNivelDesempeno() != null && !inputQueryDTO.getNivelDesempeno().isEmpty()) {
            predicates.add(cb.equal(modulo.get("nivelDesempeno"), inputQueryDTO.getNivelDesempeno()));
            filtrosAplicados.add("modulo.nivelDesempeno = '" + inputQueryDTO.getNivelDesempeno() + "'");
        }
        if (inputQueryDTO.getPercentilModulo() != null && inputQueryDTO.getPercentilModulo() > 0) {
            predicates.add(cb.equal(modulo.get("percentilNacional"), inputQueryDTO.getPercentilModulo()));
            filtrosAplicados.add("modulo.percentilNacional = " + inputQueryDTO.getPercentilModulo());
        }
        if (inputQueryDTO.getPuntajeModuloMinimo() != null && inputQueryDTO.getPuntajeModuloMinimo() > 0) {
            predicates.add(cb.greaterThanOrEqualTo(modulo.get("puntajeModulo"), inputQueryDTO.getPuntajeModuloMinimo()));
            filtrosAplicados.add("modulo.puntajeModulo >= " + inputQueryDTO.getPuntajeModuloMinimo());
        }
        if (inputQueryDTO.getPuntajeModuloMaximo() != null && inputQueryDTO.getPuntajeModuloMaximo() > 0) {
            predicates.add(cb.lessThanOrEqualTo(modulo.get("puntajeModulo"), inputQueryDTO.getPuntajeModuloMaximo()));
            filtrosAplicados.add("modulo.puntajeModulo <= " + inputQueryDTO.getPuntajeModuloMaximo());
        }

        // Para debug/log:
        System.out.println("Filtros aplicados:");
        filtrosAplicados.forEach(System.out::println);

        System.out.println("Predicados: " + predicates);
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        List<Reporte> reportes = entityManager.createQuery(query).getResultList();

        List<ReporteDTO> resultados = new ArrayList<>();
        for (Reporte reporteEntity : reportes) {
            for (Modulo moduloEntity : reporteEntity.getModulos()) {
                ReporteDTO dto = convertToReporteDTO(reporteEntity, moduloEntity);
                resultados.add(dto);
            }
        }
        entityManager.close();
        System.out.println("Resultados encontrados: " + resultados.size());
        return resultados;
    }

    private ReporteDTO convertToReporteDTO(Reporte reporte, Modulo modulo) {
        //System.out.println("Convirtiendo reporte: " + reporte.getNumero_Registro() + " con módulo: " + modulo.getTipo());
        return new ReporteDTO(
                reporte.getEstudiante().getDocumento(),                         //Documento
                reporte.getEstudiante().getTipoDocumento(),                     // Tipo Documento
                reporte.getEstudiante().getCiudad(),                            // Ciudad
                reporte.getEstudiante().getTipoDeEvaluado(),                    // Tipo de evaluado
                reporte.getEstudiante().getNombreEstudiante(),                  // Nombre estudiante
                reporte.getEstudiante().getPrograma().getSniesId(),             // SniesId
                reporte.getEstudiante().getPrograma().getNombrePrograma(),      // Nombre programa
                reporte.getEstudiante().getPrograma().getGrupoDeReferencia(),   // Grupo de referencia
                reporte.getNumeroRegistro(),                                    // Número de registro
                reporte.getPeriodoEvaluacion().getYear(),                       // Año
                reporte.getPeriodoEvaluacion().getPeriodo(),                    // Periodo
                reporte.getPuntajeGlobal(),                                     // Puntaje Global
                reporte.getPercentilGlobal(),                                   // Percentil Global
                reporte.getNovedades(),                                         // Novedades
                modulo.getTipo(),                                               // Tipo de módulo
                modulo.getPuntajeModulo(),                                      // Puntaje del módulo
                modulo.getNivelDesempeno(),                                     // Nivel de desempeño
                modulo.getPercentilModulo()                                     // Percentil nacional del módulo
        );
    }
}