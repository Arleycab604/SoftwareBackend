package com.saberpro.backendsoftware.service;

import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.Normalizer;
import java.util.*;

import com.saberpro.backendsoftware.model.*;
import com.saberpro.backendsoftware.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CsvUploadService {

    private final UsuarioRepositorio usuarioRepo;
    private final EstudianteRepositorio estudianteRepo;
    private final ProgramaRepositorio programaRepo;
    private final ReporteRepositorio reporteRepo;
    private final ModuloRepositorio moduloRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final YearDataUploadService yearDataUploadService;

    public String uploadExcel(MultipartFile file,int year, int periodo) throws Exception {
        // Obtener el nombre del archivo
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("El archivo no tiene un nombre válido.");
        }

        Reader reader = new InputStreamReader(file.getInputStream());
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build();

        CSVParser parser = format.parse(reader);

        Map<String, String> headerMap = new HashMap<>();
        for (String rawHeader : parser.getHeaderMap().keySet()) {
            headerMap.put(normalize(rawHeader), rawHeader);
        }

        FileWriter passwordWriter = new FileWriter("passwords.txt", true);

        for (CSVRecord record : parser) {
            String tipoDocumento = record.get(headerMap.get("tipo de documento")).trim(),
                    documento = record.get(headerMap.get("documento")).trim(),
                    nombre = record.get(headerMap.get("nombre")).trim(),
                    numeroRegistro = record.get(headerMap.get("numero de registro")).trim(),
                    tipoEvaluado = record.get(headerMap.get("tipo de evaluado")).trim(),
                    nombrePrograma = record.get(headerMap.get("programa")).trim(),
                    ciudad = record.get(headerMap.get("ciudad")).trim(),
                    grupoReferencia = record.get(headerMap.get("grupo de referencia")).trim(),
                    tipoModulo = record.get(headerMap.get("modulo")).trim(),
                    nivelDesempeno = record.get(headerMap.get("nivel de desempeno")).trim(),
                    novedades = record.get(headerMap.get("novedades")).trim();

            int sniesId = Integer.parseInt(record.get(headerMap.get("snies programa academico")).trim()),
                    puntajeGlobal = Integer.parseInt(record.get(headerMap.get("puntaje global")).trim()),
                    percentilNacionalGlobal = Integer.parseInt(record.get(headerMap.get("percentil nacional global")).trim()),
                    puntajeModulo = Integer.parseInt(record.get(headerMap.get("puntaje modulo")).trim()),
                    percentilNacionalModulo = Integer.parseInt(record.get(headerMap.get("percentil nacional modulo")).trim());


            // --- Programa ---
            Programa programa = programaRepo.findById(sniesId).orElse(null);
            if (programa == null) {
                programa = new Programa();
                programa.setSniesId(sniesId);
                programa.setNombrePrograma(nombrePrograma);
                programa.setGrupoDeReferencia(grupoReferencia);
                programaRepo.save(programa);
            }

            // --- Estudiante ---
            Long doc = Long.parseLong(documento);
            Estudiante estudiante = estudianteRepo.findById(doc).orElse(null);
            if (estudiante == null) {
                estudiante = new Estudiante();
                estudiante.setDocumento(doc);
                estudiante.setTipoDocumento(tipoDocumento);
                estudiante.setNombreEstudiante(nombre);
                estudiante.setTipoDeEvaluado(tipoEvaluado);
                estudiante.setCiudad(ciudad);
                estudianteRepo.save(estudiante);
            }

            // --- Reporte ---
            Reporte reporte = reporteRepo.findById(numeroRegistro).orElse(null);
            if (reporte == null) {
                reporte = new Reporte();
                reporte.setNumeroRegistro(numeroRegistro);
                reporte.setEstudiante(estudiante);
                reporte.setYear(year); // Usar el año extraído
                reporte.setPeriodo(periodo); // Usar el periodo extraído
                reporte.setPuntajeGlobal(puntajeGlobal);
                reporte.setPercentilGlobal(percentilNacionalGlobal);
                reporte.setNovedades(novedades);
                reporte.setModulos(new ArrayList<>());
                reporteRepo.save(reporte);
            }

            // --- Módulo ---
            Modulo modulo = moduloRepo.findByTipoAndNumeroRegistro(tipoModulo, numeroRegistro).orElse(null);
            if (modulo == null) {
                modulo = new Modulo();
                modulo.setTipo(tipoModulo);
                modulo.setReporte(reporte);
                modulo.setPuntajeModulo(puntajeModulo);
                modulo.setNivelDesempeno(nivelDesempeno);
                modulo.setPercentilNacional(percentilNacionalModulo);
                modulo.setReporte(reporte);
                moduloRepo.save(modulo);

                // Añadir a la lista en el reporte
                reporte.getModulos().add(modulo);
                reporteRepo.save(reporte); // importante para mantener la relación bidireccional
            }
        }

        passwordWriter.close();

        // Llamar a YearDataUploadService con el año y periodo extraídos
        yearDataUploadService.processYearData(year, periodo);

        return "Archivo procesado exitosamente";
    }
    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private static String normalize(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .trim();
    }
}
