package com.saberpro.backendsoftware.model;

import com.saberpro.backendsoftware.repository.EstudianteRepositorio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Reporte {
    @Id
    private String numeroRegistro;
    private String novedades;
    @OneToOne
    @JoinColumn(name = "estudiante_documento", referencedColumnName = "documento")
    private Estudiante estudiante;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_periodo_evaluacion", referencedColumnName = "idPeriodoEvaluacion")
    private PeriodoEvaluacion periodoEvaluacion;

    private int puntajeGlobal;
    private int percentilGlobal;

    @OneToMany(mappedBy = "reporte")
    private List<Modulo> modulos = new ArrayList<>();

    public Reporte() {
        numeroRegistro = "";
        novedades = "";
        puntajeGlobal = 0;
        percentilGlobal = 0;
    }
    public Reporte(String numeroRegistro, PeriodoEvaluacion perEv, Estudiante estudiante, String novedades, int puntajeGlobal, int percentilGlobal) {
        this.numeroRegistro = numeroRegistro;
        this.novedades = novedades;
        this.puntajeGlobal = puntajeGlobal;
        this.percentilGlobal = percentilGlobal;
        this.periodoEvaluacion=perEv;
        this.estudiante = estudiante;
    }
    public void addModulo(Modulo modulo) {
        this.modulos.add(modulo);
        modulo.setReporte(this);
    }

    public String toString() {
        return "Reporte{" +
                "numeroRegistro='" + numeroRegistro + '\'' +
                ", novedades='" + novedades + '\'' +
                ", estudiante=" + estudiante +
                ", periodoEvaluacion=" + periodoEvaluacion +
                ", puntajeGlobal=" + puntajeGlobal +
                ", percentilGlobal=" + percentilGlobal +
                '}';
    }
}