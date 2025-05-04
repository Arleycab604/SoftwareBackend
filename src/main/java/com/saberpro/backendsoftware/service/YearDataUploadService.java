package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.model.*;
import com.saberpro.backendsoftware.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class YearDataUploadService {

    private final ReporteRepositorio reporteRepo;
    private final ModuloRepositorio moduloRepo;
    private final ReporteYearRepositorio reporteYearRepo;
    private final ModuloYearRepositorio moduloYearRepo;
    private final PeriodoEvaluacionRepositorio periodoEvRepo;

    @Transactional
    public void processYearData(int year, int periodo) {
        System.out.println("Pasando a yearDataUploadService");
        PeriodoEvaluacion periodoEv = periodoEvRepo.findByYearAndPeriodo(year, periodo).orElse(null);
        if (periodoEv == null) {
            periodoEv = new PeriodoEvaluacion(year,periodo);
            periodoEvRepo.saveAndFlush(periodoEv);
            System.out.println("periodoEv GUARDADO = " + periodoEv);
        }
        System.out.println("periodoEv = " + periodoEv);
        // --- Eliminar datos existentes para el año y periodo ---
        List<ReporteYear> reportesYear = reporteYearRepo.findByPeriodoEvaluacion(periodoEv);
        if (reportesYear != null && !reportesYear.isEmpty()) {
            moduloYearRepo.deleteByReporteYear(reportesYear.getFirst());
            reporteYearRepo.deleteByPeriodoEvaluacion(periodoEv);
        } else {
            System.out.println("No se encontraron datos para eliminar en reporteYear.");
        }
        // --- Calcular y guardar ReporteYear ---
        List<Integer> puntajesGlobales = reporteRepo.findByPeriodoEvaluacion(periodoEv)
                .stream()
                .map(Reporte::getPuntajeGlobal)
                .collect(Collectors.toList());

        System.out.println("hola");
        ReporteYear reporteYear = new ReporteYear();
        if (!puntajesGlobales.isEmpty()) {
            double media = calculateMean(puntajesGlobales);
            double varianza = calculateVariance(puntajesGlobales, media);

            reporteYear.setPeriodoEvaluacion(periodoEv);
            reporteYear.setMediaPeriodo(calculateMean(puntajesGlobales));
            reporteYear.setVarianzaPeriodo(calculateVariance(puntajesGlobales, media));
            reporteYear.setCoeficienteVariacion(calculateCoefficientOfVariation(media, varianza));
            reporteYearRepo.saveAndFlush(reporteYear);
        }

        // --- Calcular y guardar ModuloYear ---
        List<String> tiposModulo = moduloRepo.findDistinctTiposByPeriodoEvaluacion(periodoEv);
        for (String tipoModulo : tiposModulo) {
            List<Integer> puntajesModulo = moduloRepo.findByTipoAndPeriodoEvaluacion(tipoModulo, periodoEv)
                    .stream()
                    .map(Modulo::getPuntajeModulo)
                    .collect(Collectors.toList());

            if (!puntajesModulo.isEmpty()) {
                double mediaModulo = calculateMean(puntajesModulo);
                double varianzaModulo = calculateVariance(puntajesModulo, mediaModulo);
                double coeficienteVariacionModulo = calculateCoefficientOfVariation(mediaModulo, varianzaModulo);

                ModuloYear moduloYear = new ModuloYear();
                moduloYear.setReporteYear(reporteYear);
                moduloYear.setTipoModulo(tipoModulo);
                moduloYear.setMediaModuloYear(mediaModulo);
                moduloYear.setVarianzaModuloYear(varianzaModulo);
                moduloYear.setCoeficienteVariacionModuloYear(coeficienteVariacionModulo);
                moduloYearRepo.saveAndFlush(moduloYear);
            }
        }
    }
    private double calculateMean(List<Integer> values) {
        return values.stream().mapToDouble(v -> v).average().orElse(0.0);
    }

    private double calculateVariance(List<Integer> values, double mean) {
        return values.stream().mapToDouble(v -> Math.pow(v - mean, 2)).average().orElse(0.0);
    }

    private double calculateCoefficientOfVariation(double mean, double variance) {
        return mean != 0 ? Math.sqrt(variance) / mean : 0.0;
    }
}