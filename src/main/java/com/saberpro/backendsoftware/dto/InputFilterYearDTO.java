package com.saberpro.backendsoftware.dto;

import lombok.Data;

@Data
public class InputFilterYearDTO {
    private Integer year;
    private Integer periodo;
    private Double mediaPeriodoMin;
    private Double mediaPeriodoMax;
    private Double coefVarPeriodoMin;
    private Double coefVarPeriodoMax;
    private Double mediaModuloMin;
    private Double mediaModuloMax;
    private Double coefVarModuloMin;
    private Double coefVarModuloMax;
    private String tipoModulo; // Comas separadas

    public String toString(){
        return "InputFilterYearDTO{" +
                "year=" + year +
                ", periodo=" + periodo +
                ", mediaPeriodoMin=" + mediaPeriodoMin +
                ", mediaPeriodoMax=" + mediaPeriodoMax +
                ", coefVarPeriodoMin=" + coefVarPeriodoMin +
                ", coefVarPeriodoMax=" + coefVarPeriodoMax +
                ", mediaModuloMin=" + mediaModuloMin +
                ", mediaModuloMax=" + mediaModuloMax +
                ", coefVarModuloMin=" + coefVarModuloMin +
                ", coefVarModuloMax=" + coefVarModuloMax +
                ", tipoModulo='" + tipoModulo + '\'' +
                '}';
    }
}