package com.saberpro.backendsoftware.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class PeriodoEvaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPeriodoEvaluacion;

    private int year;
    private int periodo;

    @OneToMany(mappedBy = "periodoEvaluacion")
    private List<Reporte> reportes = new ArrayList<>();
    public PeriodoEvaluacion() {
    }
    public PeriodoEvaluacion(int year, int periodo) {
        this.year = year;
        this.periodo = periodo;
    }

    public void addReporte(Reporte reporte) {
        this.reportes.add(reporte);
        reporte.setPeriodoEvaluacion(this);
    }
    public String toString() {
        return "PeriodoEvaluacion{" +
                "idPeriodoEvaluacion=" + idPeriodoEvaluacion +
                ", year=" + year +
                ", periodo=" + periodo +
                '}';
    }

}