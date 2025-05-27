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

    private final ReporteRepository reporteRepo;
    private final ModuloRepository moduloRepo;
    private final ReporteYearRepository reporteYearRepo;
    private final ModuloYearRepository moduloYearRepo;
    private final PeriodoEvaluacionRepository periodoEvRepo;

    @Transactional
    public void processYearData(int year, int periodo) {
        System.out.println("Pasando a yearDataUploadService");
        PeriodoEvaluacion periodoEv = periodoEvRepo.findByYearAndPeriodo(year, periodo).orElse(null);
        if (periodoEv == null) {
            periodoEv = new PeriodoEvaluacion(year, periodo);
            periodoEvRepo.saveAndFlush(periodoEv);
            System.out.println("periodoEv GUARDADO = " + periodoEv);
        } else {
            System.out.println("PeriodoEvaluacion ya existe: " + periodoEv);
        }
        System.out.println("periodoEv = " + periodoEv);

        // --- Calcular y actualizar ReporteYear ---
        List<Integer> puntajesGlobales = reporteRepo.findByPeriodoEvaluacion(periodoEv)
                .stream()
                .map(Reporte::getPuntajeGlobal)
                .collect(Collectors.toList());

        if (!puntajesGlobales.isEmpty()) {
            double media = calculateMean(puntajesGlobales);
            double varianza = calculateVariance(puntajesGlobales, media);

            ReporteYear reporteYear = reporteYearRepo.findByPeriodoEvaluacion(periodoEv).orElse(new ReporteYear());
            reporteYear.setPeriodoEvaluacion(periodoEv);
            reporteYear.setMediaPeriodo(media);
            reporteYear.setVarianzaPeriodo(varianza);
            reporteYear.setCoeficienteVariacion(calculateCoefficientOfVariation(media, varianza));
            reporteYearRepo.saveAndFlush(reporteYear);
        }

        // --- Calcular y actualizar ModuloYear ---
        List<String> tiposModulo = moduloRepo.findDistinctTiposByPeriodoEvaluacion(periodoEv);

        for (String tipoModulo : tiposModulo) {
            System.out.println("TipoModulo = " + tipoModulo);
            List<Integer> puntajesModulo = moduloRepo.findByTipoAndPeriodoEvaluacion(tipoModulo, periodoEv)
                    .stream()
                    .map(Modulo::getPuntajeModulo)
                    .collect(Collectors.toList());

            if (!puntajesModulo.isEmpty()) {
                double mediaModulo = calculateMean(puntajesModulo);
                double varianzaModulo = calculateVariance(puntajesModulo, mediaModulo);
                double coeficienteVariacionModulo = calculateCoefficientOfVariation(mediaModulo, varianzaModulo);

                ModuloYear moduloYear = moduloYearRepo.findByReporteYearAndTipoModulo(
                        reporteYearRepo.findByPeriodoEvaluacion(periodoEv).orElse(null), tipoModulo
                ).orElse(new ModuloYear());

                moduloYear.setReporteYear(reporteYearRepo.findByPeriodoEvaluacion(periodoEv).orElse(null));
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