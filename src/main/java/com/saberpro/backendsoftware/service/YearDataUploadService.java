package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.model.Modulo;
import com.saberpro.backendsoftware.model.ModuloYear;
import com.saberpro.backendsoftware.model.Reporte;
import com.saberpro.backendsoftware.model.ReporteYear;
import com.saberpro.backendsoftware.repository.ModuloRepositorio;
import com.saberpro.backendsoftware.repository.ReporteRepositorio;
import com.saberpro.backendsoftware.repository.ReporteYearRepositorio;
import com.saberpro.backendsoftware.repository.ModuloYearRepositorio;
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

    public void processYearData(int year, int periodo) {
        // --- Eliminar datos existentes para el año y periodo ---
        reporteYearRepo.deleteByYearAndPeriodo(year, periodo);
        moduloYearRepo.deleteByYearAndPeriodo(year, periodo);

        // --- Calcular y guardar ReporteYear ---
        List<Integer> puntajesGlobales = reporteRepo.findByYearAndPeriodo(year, periodo)
                .stream()
                .map(Reporte::getPuntajeGlobal)
                .collect(Collectors.toList());

        if (!puntajesGlobales.isEmpty()) {
            double media = calculateMean(puntajesGlobales);
            double varianza = calculateVariance(puntajesGlobales, media);
            double coeficienteVariacion = calculateCoefficientOfVariation(media, varianza);

            ReporteYear reporteYear = new ReporteYear();
            reporteYear.setYear(year);
            reporteYear.setPeriodo(periodo);
            reporteYear.setMediaPeriodo(media);
            reporteYear.setVarianzaPeriodo(varianza);
            reporteYear.setCoeficienteVariacion(coeficienteVariacion);
            reporteYearRepo.save(reporteYear);
        }

        // --- Calcular y guardar ModuloYear ---
        List<String> tiposModulo = moduloRepo.findDistinctTiposByYearAndPeriodo(year, periodo);
        for (String tipoModulo : tiposModulo) {
            List<Integer> puntajesModulo = moduloRepo.findByTipoAndYearAndPeriodo(tipoModulo, year, periodo)
                    .stream()
                    .map(Modulo::getPuntajeModulo)
                    .collect(Collectors.toList());

            if (!puntajesModulo.isEmpty()) {
                double mediaModulo = calculateMean(puntajesModulo);
                double varianzaModulo = calculateVariance(puntajesModulo, mediaModulo);
                double coeficienteVariacionModulo = calculateCoefficientOfVariation(mediaModulo, varianzaModulo);

                ModuloYear moduloYear = new ModuloYear();
                moduloYear.setYear(year);
                moduloYear.setPeriodo(periodo);
                moduloYear.setTipoModulo(tipoModulo);
                moduloYear.setMediaModuloYear(mediaModulo);
                moduloYear.setVarianzaModuloYear(varianzaModulo);
                moduloYear.setCoeficienteVariacionModuloYear(coeficienteVariacionModulo);
                moduloYearRepo.save(moduloYear);
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