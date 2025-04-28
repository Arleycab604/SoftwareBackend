package com.saberpro.backendsoftware.service;

import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.Normalizer;
import java.time.Year;
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

    public String uploadCsv(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("El archivo no tiene un nombre válido.");
        }

        if (!fileName.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("El archivo debe ser un archivo CSV.");
        }

        String regex = "(\\d{4})[^\\d]*(\\d{1})(\\.csv)?$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(fileName);

        int year;
        int periodo;

        if (matcher.find()) {
            year = Integer.parseInt(matcher.group(1));
            periodo = Integer.parseInt(matcher.group(2));
        } else {
            throw new IllegalArgumentException("El nombre del archivo no contiene un año y periodo válidos.");
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
            String tipoDocumento = record.get(headerMap.get("tipo de documento")).trim();
            String documento = record.get(headerMap.get("documento")).trim();
            String nombre = record.get(headerMap.get("nombre")).trim();
            String numeroRegistro = record.get(headerMap.get("numero de registro")).trim();
            String tipoEvaluado = record.get(headerMap.get("tipo de evaluado")).trim();
            int sniesId = Integer.parseInt(record.get(headerMap.get("snies programa academico")).trim());
            String nombrePrograma = record.get(headerMap.get("programa")).trim();
            String ciudad = record.get(headerMap.get("ciudad")).trim();
            String grupoReferencia = record.get(headerMap.get("grupo de referencia")).trim();
            int puntajeGlobal = Integer.parseInt(record.get(headerMap.get("puntaje global")).trim());
            int percentilNacionalGlobal = Integer.parseInt(record.get(headerMap.get("percentil nacional global")).trim());
            String tipoModulo = record.get(headerMap.get("modulo")).trim();
            int puntajeModulo = Integer.parseInt(record.get(headerMap.get("puntaje modulo")).trim());
            String nivelDesempeno = record.get(headerMap.get("nivel de desempeno")).trim();
            int percentilNacionalModulo = Integer.parseInt(record.get(headerMap.get("percentil nacional modulo")).trim());
            String novedades = record.get(headerMap.get("novedades")).trim();

            Programa programa = programaRepo.findById(sniesId).orElse(null);
            if (programa == null) {
                programa = new Programa();
                programa.setSniesId(sniesId);
                programa.setPrograma(nombrePrograma);
                programa.setGrupoDeReferencia(grupoReferencia);
                programaRepo.save(programa);
            }

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

            Reporte reporte = reporteRepo.findById(numeroRegistro).orElse(null);
            if (reporte == null) {
                reporte = new Reporte();
                reporte.setNumeroRegistro(numeroRegistro);
                reporte.setDocumento(estudiante);
                reporte.setYear(year);
                reporte.setPeriodo(periodo);
                reporte.setPuntajeGlobal(puntajeGlobal);
                reporte.setPercentilGlobal(percentilNacionalGlobal);
                reporte.setNovedades(novedades);
                reporte.setModulos(new ArrayList<>());
                reporteRepo.save(reporte);
            }

            Modulo modulo = moduloRepo.findByTipoAndNumeroRegistro(tipoModulo, numeroRegistro).orElse(null);
            if (modulo == null) {
                modulo = new Modulo();
                modulo.setTipo(tipoModulo);
                modulo.setNumeroRegistro(numeroRegistro);
                modulo.setPuntajeModulo(puntajeModulo);
                modulo.setNivelDesempeno(nivelDesempeno);
                modulo.setPercentilNacional(percentilNacionalModulo);
                modulo.setReporte(reporte);
                moduloRepo.save(modulo);

                reporte.getModulos().add(modulo);
                reporteRepo.save(reporte);
            }
        }

        passwordWriter.close();
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