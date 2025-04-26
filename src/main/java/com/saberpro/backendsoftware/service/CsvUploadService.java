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

    public String uploadExcel(MultipartFile file) throws Exception {
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

        String[] colCSV = {
                "tipo de documento", "documento", "nombre", "numero de registro",
                "tipo de evaluado", "snies programa academico", "programa", "ciudad",
                "grupo de referencia", "puntaje global", "percentil nacional global",
                "percentil grupo de referencia", "modulo", "puntaje modulo",
                "nivel de desempeno", "percentil nacional modulo", "novedades",
                "Percentil grupo de referencia modulo"
        };

        FileWriter passwordWriter = new FileWriter("passwords.txt", true);

        for (CSVRecord record : parser) {
            String tipoDocumento = record.get(headerMap.get(colCSV[0])).trim();
            String documento = record.get(headerMap.get(colCSV[1])).trim();
            String nombre = record.get(headerMap.get(colCSV[2])).trim();
            String numeroRegistro = record.get(headerMap.get(colCSV[3])).trim();
            String tipoEvaluado = record.get(headerMap.get(colCSV[4])).trim();
            int sniesId = Integer.parseInt( record.get(headerMap.get(colCSV[5])).trim());
            String nombrePrograma = record.get(headerMap.get(colCSV[6])).trim();
            String ciudad = record.get(headerMap.get(colCSV[7])).trim();
            String grupoReferencia = record.get(headerMap.get(colCSV[8])).trim();
            int puntajeGlobal = Integer.parseInt(record.get(headerMap.get(colCSV[9])).trim());
            int percentilNacionalGlobal= Integer.parseInt(record.get(headerMap.get(colCSV[10])).trim());
            //int percentilGrupoReferencia = Integer.parseInt(record.get(headerMap.get(colCSV[11])).trim());
            String tipoModulo = record.get(headerMap.get(colCSV[12])).trim();
            int puntajeModulo = Integer.parseInt(record.get(headerMap.get(colCSV[13])).trim());
            String nivelDesempeno = record.get(headerMap.get(colCSV[14])).trim();
            int percentilNacionalModulo = Integer.parseInt(record.get(headerMap.get(colCSV[15])).trim());
            String novedades = record.get(headerMap.get(colCSV[16])).trim();

            // --- Programa ---
            Programa programa = programaRepo.findById(sniesId).orElse(null);
            if (programa == null) {
                programa = new Programa();
                programa.setSniesId(sniesId);
                programa.setPrograma(nombrePrograma);
                programa.setGrupoDeReferencia(grupoReferencia);
                programaRepo.save(programa);
            }

            // --- Usuario ---
            Usuario usuario = usuarioRepo.findById(nombre).orElse(null);
            if (usuario == null) {
                String rawPassword = generateRandomPassword();
                String hashed = passwordEncoder.encode(rawPassword);
                usuario = new Usuario();
                usuario.setNombreUsuario(nombre);
                usuario.setPassword(hashed);
                usuario.setSniesId(sniesId);
                usuario.setTipoDeUsuario("estudiante");
                usuario.setPrograma(programa);
                usuarioRepo.save(usuario);
                passwordWriter.write(nombre + ": " + rawPassword + "\n");
            }

            // --- Estudiante ---
            Long doc = Long.parseLong(documento);
            Estudiante estudiante = estudianteRepo.findById(doc).orElse(null);
            if (estudiante == null) {
                estudiante = new Estudiante();
                estudiante.setDocumento(doc);
                estudiante.setTipoDocumento(tipoDocumento);
                estudiante.setNombreUsuario(usuario);
                estudiante.setTipoDeEvaluado(tipoEvaluado);
                estudiante.setCiudad(ciudad);
                estudianteRepo.save(estudiante);
            }

            // --- Reporte ---
            Reporte reporte = reporteRepo.findById(numeroRegistro).orElse(null);
            if (reporte == null) {
                reporte = new Reporte();
                reporte.setNumero_Registro(numeroRegistro);
                reporte.setDocumento(estudiante);
                reporte.setYear(2023); // o puedes extraerlo del CSV si lo tienes
                reporte.setPeriodo(2);
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
                modulo.setNumeroRegistro(numeroRegistro);
                modulo.setPuntajeModulo(puntajeModulo);
                modulo.setNivelDesempeno(nivelDesempeno);
                modulo.setPercentilNacional(percentilNacionalModulo);
                modulo.setCodModulo(reporte);
                moduloRepo.save(modulo);

                // Añadir a la lista en el reporte
                reporte.getModulos().add(modulo);
                reporteRepo.save(reporte); // importante para mantener la relación bidireccional
            }
        }

        passwordWriter.close();
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
