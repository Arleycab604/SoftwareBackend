package com.saberpro.backendsoftware.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class ReporteYear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idReporteYear;
    @OneToOne
    private PeriodoEvaluacion periodoEvaluacion;
    private double mediaPeriodo;
    private double varianzaPeriodo;
    private double coeficienteVariacion;

    @OneToMany(mappedBy = "idModuloYear")
    private List<ModuloYear> modulosYear;

    public ReporteYear() {
        mediaPeriodo = 0.0;
        varianzaPeriodo = 0.0;
        coeficienteVariacion = 0.0;
    }
    public ReporteYear(PeriodoEvaluacion periodoEvaluacion, double mediaPeriodo, double varianzaPeriodo, double coeficienteVariacion) {
        this.periodoEvaluacion = periodoEvaluacion;
        this.mediaPeriodo = mediaPeriodo;
        this.varianzaPeriodo = varianzaPeriodo;
        this.coeficienteVariacion = coeficienteVariacion;
    }

    public String toString() {
        return "ReporteYear{" +
                "idReporteYear=" + idReporteYear +
                ", periodoEvaluacion=" + periodoEvaluacion +
                ", mediaPeriodo=" + mediaPeriodo +
                ", varianzaPeriodo=" + varianzaPeriodo +
                ", coeficienteVariacion=" + coeficienteVariacion +
                '}';
    }
}