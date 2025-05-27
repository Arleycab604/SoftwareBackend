package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.enums.TipoUsuario;
import com.saberpro.backendsoftware.model.*;
import com.saberpro.backendsoftware.model.usuarios.Estudiante;
import com.saberpro.backendsoftware.repository.*;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.Normalizer;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final EstudianteRepository estudianteRepo;
    private final UsuarioRepository usuarioRepo;
    private final ProgramaRepository programaRepo;
    private final ReporteRepository reporteRepo;
    private final ModuloRepository moduloRepo;
    private final PeriodoEvaluacionRepository periodoEvRepo;
    private final YearDataUploadService yearDataUploadService;

    @PostConstruct
    public void init() {
        System.out.println("PeriodoEvaluacionRepositorio = " + periodoEvRepo);
    }

    @Transactional
    public String uploadCSV(MultipartFile file, int year, int periodo) throws Exception {
        Reader reader = new InputStreamReader(file.getInputStream());
        CSVParser parser = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build()
                .parse(reader);

        Map<String, String> headerMap = new HashMap<>();
        for (String rawHeader : parser.getHeaderMap().keySet()) {
            headerMap.put(normalize(rawHeader), rawHeader);
        }

        PeriodoEvaluacion periodoEv = obtenerOcrearPeriodoEvaluacion(year, periodo);

        for (CSVRecord record : parser) {
            Map<String, String> datos = new HashMap<>();
            for (String key : headerMap.keySet()) {
                datos.put(key, record.get(headerMap.get(key)).trim());
            }
            procesarFila(datos, periodoEv);
        }

        parser.close();
        reader.close();
        yearDataUploadService.processYearData(year, periodo);
        return "CSV procesado exitosamente";
    }

    @Transactional
    public String uploadExcel(MultipartFile file, int year, int periodo) throws Exception {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        if (!rowIterator.hasNext()) throw new IllegalStateException("El archivo está vacío");

        Row headerRow = rowIterator.next();
        Map<String, Integer> headerIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            headerIndexMap.put(normalize(cell.getStringCellValue()), cell.getColumnIndex());
        }

        PeriodoEvaluacion periodoEv = obtenerOcrearPeriodoEvaluacion(year, periodo);

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Map<String, String> datos = new HashMap<>();
            for (Map.Entry<String, Integer> entry : headerIndexMap.entrySet()) {
                datos.put(entry.getKey(), getCellString(row, entry.getValue()));
            }
            procesarFila(datos, periodoEv);
        }

        workbook.close();
        yearDataUploadService.processYearData(year, periodo);
        return "Excel procesado exitosamente";
    }

    private void procesarFila(Map<String, String> datos, PeriodoEvaluacion periodoEv) {
        String tipoDocumento = datos.get("tipo de documento"),
                documento = datos.get("documento"),
                nombre = datos.get("nombre"),
                numeroRegistro = datos.get("numero de registro"),
                tipoEvaluado = datos.get("tipo de evaluado"),
                nombrePrograma = datos.get("programa"),
                ciudad = datos.get("ciudad"),
                grupoReferencia = datos.get("grupo de referencia"),
                tipoModulo = datos.get("modulo"),
                nivelDesempeno = datos.get("nivel de desempeno"),
                novedades = datos.get("novedades");

        int sniesId = Integer.parseInt(datos.get("snies programa academico")),
                puntajeGlobal = Integer.parseInt(datos.get("puntaje global")),
                percentilNacionalGlobal = Integer.parseInt(datos.get("percentil nacional global")),
                puntajeModulo = Integer.parseInt(datos.get("puntaje modulo")),
                percentilNacionalModulo = Integer.parseInt(datos.get("percentil nacional modulo"));

        Programa programa = programaRepo.findById(sniesId).orElseGet(() -> {
            Programa nuevo = new Programa();
            nuevo.setSniesId(sniesId);
            nuevo.setNombrePrograma(nombrePrograma);
            nuevo.setGrupoDeReferencia(grupoReferencia);
            return programaRepo.saveAndFlush(nuevo);
        });

        Long doc = Long.parseLong(documento);

// Buscar o crear el Usuario
        Usuario usuario = usuarioRepo.findByNombreUsuario(nombre).orElseGet(() -> {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(nombre); // nombre del estudiante como ID
            nuevoUsuario.setPassword("defaultPassword"); // puedes cifrar o generar aleatoriamente
            nuevoUsuario.setTipoDeUsuario(TipoUsuario.ESTUDIANTE);
            nuevoUsuario.setPrograma(programa);
            nuevoUsuario.setCorreo(nombre.toLowerCase().replaceAll("\\s+", ".") + "@saberpro.com"); // o algún valor por defecto
            return usuarioRepo.saveAndFlush(nuevoUsuario);
        });

// Buscar o crear el Estudiante
        Estudiante estudiante = estudianteRepo.findById(doc).orElseGet(() -> {
            Estudiante nuevoEst = new Estudiante();
            nuevoEst.setDocumento(doc);
            nuevoEst.setTipoDocumento(tipoDocumento);
            nuevoEst.setTipoDeEvaluado(tipoEvaluado);
            nuevoEst.setCiudad(ciudad);
            nuevoEst.setUsuario(usuario);
            return estudianteRepo.saveAndFlush(nuevoEst);
        });

        Reporte reporte = reporteRepo.findById(numeroRegistro).orElseGet(() -> {
            Reporte nuevo = new Reporte();
            nuevo.setNumeroRegistro(numeroRegistro);
            nuevo.setEstudiante(estudiante);
            nuevo.setPeriodoEvaluacion(periodoEv);
            nuevo.setNovedades(novedades);
            nuevo.setPuntajeGlobal(puntajeGlobal);
            nuevo.setPercentilGlobal(percentilNacionalGlobal);
            nuevo.setModulos(new ArrayList<>());
            return reporteRepo.saveAndFlush(nuevo);
        });

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
            reporteRepo.saveAndFlush(reporte);
        }
    }

    private PeriodoEvaluacion obtenerOcrearPeriodoEvaluacion(int year, int periodo) {
        return periodoEvRepo.findByYearAndPeriodo(year, periodo).orElseGet(() -> {
            PeriodoEvaluacion nuevo = new PeriodoEvaluacion();
            nuevo.setYear(year);
            nuevo.setPeriodo(periodo);
            return periodoEvRepo.saveAndFlush(nuevo);
        });
    }

    private static String normalize(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .trim();
    }

    private static String getCellString(Row row, int index) {
        Cell cell = row.getCell(index);
        return cell == null ? "" : switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }
}
