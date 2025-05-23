package com.saberpro.backendsoftware.service;

import java.io.FileWriter;
import java.text.Normalizer;
import java.util.*;

import com.saberpro.backendsoftware.model.*;
import com.saberpro.backendsoftware.repository.*;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class ExcelUploadService {

    private final EstudianteRepository estudianteRepo;
    private final ProgramaRepository programaRepo;
    private final ReporteRepository reporteRepo;
    private final ModuloRepository moduloRepo;
    private final PeriodoEvaluacionRepository periodoEvRepo;
    private final YearDataUploadService yearDataUploadService;
    //private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void init() {
        System.out.println("PeriodoEvaluacionRepositorio = " + periodoEvRepo);
    }

    @Transactional
    public String uploadExcel(MultipartFile file, int year, int periodo) throws Exception {
        String fileName = file.getOriginalFilename();


        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();

        if (!rowIterator.hasNext()) throw new IllegalStateException("El archivo está vacío");

        Row headerRow = rowIterator.next();
        Map<String, Integer> headerIndexMap = new HashMap<>();

        for (Cell cell : headerRow) {
            String normalized = normalize(cell.getStringCellValue());
            headerIndexMap.put(normalized, cell.getColumnIndex());
        }

        FileWriter passwordWriter = new FileWriter("passwords.txt", true);

        PeriodoEvaluacion periodoEv = periodoEvRepo.findByYearAndPeriodo(year, periodo).orElseGet(() -> {
            PeriodoEvaluacion nuevo = new PeriodoEvaluacion();
            nuevo.setYear(year);
            nuevo.setPeriodo(periodo);
            return periodoEvRepo.saveAndFlush(nuevo);
        });

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            String tipoDocumento = getCellString(row, headerIndexMap.get("tipo de documento"));
            String documento = getCellString(row, headerIndexMap.get("documento"));
            String nombre = getCellString(row, headerIndexMap.get("nombre"));
            String numeroRegistro = getCellString(row, headerIndexMap.get("numero de registro"));
            String tipoEvaluado = getCellString(row, headerIndexMap.get("tipo de evaluado"));
            String nombrePrograma = getCellString(row, headerIndexMap.get("programa"));
            String ciudad = getCellString(row, headerIndexMap.get("ciudad"));
            String grupoReferencia = getCellString(row, headerIndexMap.get("grupo de referencia"));
            String tipoModulo = getCellString(row, headerIndexMap.get("modulo"));
            String nivelDesempeno = getCellString(row, headerIndexMap.get("nivel de desempeno"));
            String novedades = getCellString(row, headerIndexMap.get("novedades"));

            int sniesId = Integer.parseInt(getCellString(row, headerIndexMap.get("snies programa academico")));
            int puntajeGlobal = Integer.parseInt(getCellString(row, headerIndexMap.get("puntaje global")));
            int percentilNacionalGlobal = Integer.parseInt(getCellString(row, headerIndexMap.get("percentil nacional global")));
            int puntajeModulo = Integer.parseInt(getCellString(row, headerIndexMap.get("puntaje modulo")));
            int percentilNacionalModulo = Integer.parseInt(getCellString(row, headerIndexMap.get("percentil nacional modulo")));

            Programa programa = programaRepo.findById(sniesId).orElseGet(() -> {
                Programa nuevo = new Programa();
                nuevo.setSniesId(sniesId);
                nuevo.setNombrePrograma(nombrePrograma);
                nuevo.setGrupoDeReferencia(grupoReferencia);
                return programaRepo.saveAndFlush(nuevo);
            });

            Long doc = Long.parseLong(documento);
            Estudiante estudiante = estudianteRepo.findById(doc).orElseGet(() -> {
                Estudiante nuevo = new Estudiante();
                nuevo.setNombreEstudiante(nombre);
                nuevo.setDocumento(doc);
                nuevo.setTipoDocumento(tipoDocumento);
                nuevo.setTipoDeEvaluado(tipoEvaluado);
                nuevo.setCiudad(ciudad);
                nuevo.setPrograma(programa);
                return estudianteRepo.saveAndFlush(nuevo);
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

        workbook.close();
        passwordWriter.close();
        yearDataUploadService.processYearData(year, periodo);
        return "Excel procesado exitosamente";
    }
    private static String getCellString(Row row, int index) {
        Cell cell = row.getCell(index);
        return cell == null ? "" : switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }

    private static String normalize(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .trim();
    }
}
