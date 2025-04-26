package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.Dtos.InputQueryDTO;
import com.saberpro.backendsoftware.Dtos.ReporteDTO;
import com.saberpro.backendsoftware.model.*;
import com.saberpro.backendsoftware.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryService {

    private final EstudianteRepositorio estudianteRepo;
    private final UsuarioRepositorio usuarioRepo;
    private final ReporteRepositorio reporteRepo;
    private final ProgramaRepositorio programaRepo;
    private final ModuloRepositorio moduloRepo;

    public List<ReporteDTO> filtrarDatos(InputQueryDTO inputQueryDTO) {
        System.out.println("Iniciando filtrado con los datos: " + inputQueryDTO);
        List<ReporteDTO> resultados = new ArrayList<>();

        // Obtener todos los reportes
        List<Reporte> reportes = reporteRepo.findAll();

        for (Reporte reporte : reportes) {
            // Filtrar por criterios globales
            boolean cumpleFiltrosGlobales =
                    (inputQueryDTO.getYear() == 0 || reporte.getYear() == inputQueryDTO.getYear()) &&
                            (inputQueryDTO.getPeriodo() == 0 || reporte.getPeriodo() == inputQueryDTO.getPeriodo()) &&
                            (inputQueryDTO.getNombreUsuario() == null || inputQueryDTO.getNombreUsuario().isEmpty() ||
                                    reporte.getDocumento().getNombreUsuario().getNombreUsuario().equalsIgnoreCase(inputQueryDTO.getNombreUsuario())) &&
                            (inputQueryDTO.getNombrePrograma() == null || inputQueryDTO.getNombrePrograma().isEmpty() ||
                                    reporte.getDocumento().getNombreUsuario().getPrograma().getPrograma().equalsIgnoreCase(inputQueryDTO.getNombrePrograma())) &&
                            (inputQueryDTO.getNumeroRegistro() == null || inputQueryDTO.getNumeroRegistro().isEmpty() ||
                                    reporte.getNumero_Registro().equalsIgnoreCase(inputQueryDTO.getNumeroRegistro())) &&
                            (inputQueryDTO.getPuntajeGlobalMinimo() == 0 || reporte.getPuntajeGlobal() >= inputQueryDTO.getPuntajeGlobalMinimo()) &&
                            (inputQueryDTO.getPuntajeGlobalMaximo() == 0 || reporte.getPuntajeGlobal() <= inputQueryDTO.getPuntajeGlobalMaximo());

            if (cumpleFiltrosGlobales) {
                // Iterar sobre los módulos asociados al reporte
                for (Modulo modulo : reporte.getModulos()) {
                    // Filtrar por criterios específicos de módulos
                    boolean cumpleFiltrosModulo =
                            (inputQueryDTO.getTipoModulo() == null || inputQueryDTO.getTipoModulo().isEmpty() ||
                                    modulo.getTipo().equalsIgnoreCase(inputQueryDTO.getTipoModulo())) &&
                                    (inputQueryDTO.getNivelDesempeno() == null || inputQueryDTO.getNivelDesempeno().isEmpty() ||
                                            modulo.getNivelDesempeno().equalsIgnoreCase(inputQueryDTO.getNivelDesempeno())) &&
                                    (inputQueryDTO.getPuntajeMinimoModulo() == 0 || modulo.getPuntajeModulo() >= inputQueryDTO.getPuntajeMinimoModulo()) &&
                                    (inputQueryDTO.getPuntajeMaximoModulo() == 0 || modulo.getPuntajeModulo() <= inputQueryDTO.getPuntajeMaximoModulo());

                    if (cumpleFiltrosModulo) {
                        // Crear un ReporteDTO para cada módulo que cumpla con los filtros
                        ReporteDTO dto = convertToReporteDTO(reporte, modulo);
                        resultados.add(dto);
                    }
                }
            }
        }

        System.out.println("Resultados encontrados: " + resultados.size());
        return resultados;
    }

    private ReporteDTO convertToReporteDTO(Reporte reporte, Modulo modulo) {
        System.out.println("Convirtiendo reporte: " + reporte.getNumero_Registro() + " con módulo: " + modulo.getTipo());
        return new ReporteDTO(
                reporte.getDocumento().getDocumento(),
                reporte.getDocumento().getTipoDocumento(),
                reporte.getDocumento().getCiudad(),
                reporte.getDocumento().getTipoDeEvaluado(),
                reporte.getDocumento().getNombreUsuario().getNombreUsuario(),
                reporte.getDocumento().getNombreUsuario().getTipoDeUsuario(),
                reporte.getDocumento().getNombreUsuario().getPrograma().getSniesId(),
                reporte.getDocumento().getNombreUsuario().getPrograma().getPrograma(),
                reporte.getDocumento().getNombreUsuario().getPrograma().getGrupoDeReferencia(),
                reporte.getNumero_Registro(),
                reporte.getYear(),
                reporte.getPeriodo(),
                reporte.getPuntajeGlobal(),
                reporte.getPercentilGlobal(),
                reporte.getNovedades(),
                modulo.getTipo(), // Tipo de módulo
                modulo.getPuntajeModulo(), // Puntaje del módulo
                modulo.getNivelDesempeno(), // Nivel de desempeño
                modulo.getPercentilNacional() // Percentil nacional del módulo
        );
    }
}