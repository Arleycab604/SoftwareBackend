package com.saberpro.backendsoftware.Dtos;

import lombok.Data;

@Data
public class InputFilterYearDTO {
    private int year;
    private int periodo;
    private Double mediaPeriodoMin;
    private Double mediaPeriodoMax;
    private Double coefVarPeriodoMin;
    private Double coefVarPeriodoMax;
    private Double mediaModuloMin;
    private Double mediaModuloMax;
    private Double coefVarModuloMin;
    private Double coefVarModuloMax;
    private String tipoModulo; // Comas separadas
}