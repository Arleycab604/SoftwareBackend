package com.saberpro.backendsoftware.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ModuloYear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idModuloYear;

    @ManyToOne
    @JoinColumn(name="reporte_year" , referencedColumnName = "idReporteYear")
    private ReporteYear reporteYear;

    private String tipoModulo;
    private double mediaModuloYear;
    private double varianzaModuloYear;
    private double coeficienteVariacionModuloYear;

    public ModuloYear() {
        tipoModulo = "";
        mediaModuloYear = 0.0;
        varianzaModuloYear = 0.0;
        coeficienteVariacionModuloYear = 0.0;
    }
    public ModuloYear(String tipoModulo, double mediaModuloYear, double varianzaModuloYear, double coeficienteVariacionModuloYear) {
        this.tipoModulo = tipoModulo;
        this.mediaModuloYear = mediaModuloYear;
        this.varianzaModuloYear = varianzaModuloYear;
        this.coeficienteVariacionModuloYear = coeficienteVariacionModuloYear;
    }

    public String toString() {
        return "ModuloYear{" +
                "idModuloYear=" + idModuloYear +
                ", tipoModulo='" + tipoModulo + '\'' +
                ", mediaModuloYear=" + mediaModuloYear +
                ", varianzaModuloYear=" + varianzaModuloYear +
                ", coeficienteVariacionModuloYear=" + coeficienteVariacionModuloYear +
                '}';
    }
}