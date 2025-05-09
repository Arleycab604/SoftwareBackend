package com.saberpro.backendsoftware.service;

import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.Normalizer;
import java.util.*;

import com.saberpro.backendsoftware.model.*;
import com.saberpro.backendsoftware.repository.*;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class CsvUploadService {

    private final EstudianteRepositorio estudianteRepo;
    private final ProgramaRepositorio programaRepo;
    private final ReporteRepositorio reporteRepo;
    private final ModuloRepositorio moduloRepo;
    private final PeriodoEvaluacionRepositorio periodoEvRepo;
    private final YearDataUploadService yearDataUploadService;
    //private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void init() {
        System.out.println("PeriodoEvaluacionRepositorio = " + periodoEvRepo);
    }

    @Transactional
    public String uploadCSV(MultipartFile file, int year, int periodo) throws Exception {
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

        PeriodoEvaluacion periodoEv = periodoEvRepo.findByYearAndPeriodo(year, periodo).orElse(null);
        if (periodoEv == null) {
            periodoEv = new PeriodoEvaluacion();
            periodoEv.setYear(year);
            periodoEv.setPeriodo(periodo);
            periodoEvRepo.saveAndFlush(periodoEv);
        }
        System.out.println("PeriodoEv: " + periodoEv);

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
                programa.setNombrePrograma(nombre);
                programa.setGrupoDeReferencia(grupoReferencia);
                programaRepo.saveAndFlush(programa);
            }
            System.out.println("Programa " + programa);

            // --- Estudiante ---
            Long doc = Long.parseLong(documento);
            Estudiante estudiante = estudianteRepo.findById(doc).orElse(null);
            if (estudiante == null) {
                estudiante = new Estudiante();
                estudiante.setNombreEstudiante(nombre);
                estudiante.setDocumento(doc);
                estudiante.setTipoDocumento(tipoDocumento);
                estudiante.setTipoDeEvaluado(tipoEvaluado);
                estudiante.setTipoDocumento(tipoDocumento);
                estudiante.setCiudad(ciudad);
                estudiante.setPrograma(programa);
                estudianteRepo.saveAndFlush(estudiante);
            }

            System.out.println("Estudiante " + estudiante);
            // --- Reporte ---
            Reporte reporte = reporteRepo.findById(numeroRegistro).orElse(null);
            if (reporte == null) {
                reporte = new Reporte();
                reporte.setNumeroRegistro(numeroRegistro);
                reporte.setEstudiante(estudiante);
                reporte.setPeriodoEvaluacion(periodoEv);
                reporte.setNovedades(novedades);
                reporte.setPuntajeGlobal(puntajeGlobal);
                reporte.setPercentilGlobal(percentilNacionalGlobal);
                reporte.setModulos(new ArrayList<>());
                reporteRepo.saveAndFlush(reporte);
            }

            System.out.println("Reporte " + reporte);
            // --- Módulo ---
            Modulo modulo = moduloRepo.findByTipoAndNumeroRegistro(tipoModulo, numeroRegistro).orElse(null);
            if (modulo == null) {
                modulo = new Modulo();
                modulo.setPercentilModulo(percentilNacionalModulo);
                modulo.setPuntajeModulo(puntajeModulo);
                modulo.setNivelDesempeno(nivelDesempeno);
                modulo.setTipo(tipoModulo);
                modulo.setReporte(reporte);
                moduloRepo.saveAndFlush(modulo);
                reporte.addModulo(modulo);
                reporteRepo.saveAndFlush(reporte); // importante para mantener la relación bidireccional
            }
            System.out.println("modulo : " + modulo);
        }

        parser.close();
        reader.close();
        yearDataUploadService.processYearData(year, periodo);
        passwordWriter.close();
        return "Archivo procesado exitosamente";
    }

    private static String normalize(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .trim();
    }
}
