package com.saberpro.backendsoftware.model;

import com.saberpro.backendsoftware.model.usuarios.Estudiante;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@NamedEntityGraph(
        name = "Reporte.conRelaciones",
        attributeNodes = {
                @NamedAttributeNode(value="estudiante", subgraph="estudianteSubgraph"),
                @NamedAttributeNode("periodoEvaluacion"),
                @NamedAttributeNode("modulos")
        },
        subgraphs = {
                @NamedSubgraph(name = "estudianteSubgraph", attributeNodes = {
                        @NamedAttributeNode("programa"),
                })
        }
)
@Getter
@Setter
@Entity
public class Reporte {
    @Id
    private String numeroRegistro;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_documento", referencedColumnName = "documento")
    private Estudiante estudiante;

    @JoinColumn(name = "id_periodo_evaluacion", referencedColumnName = "idPeriodoEvaluacion")
    @ManyToOne(fetch = FetchType.LAZY)
    private PeriodoEvaluacion periodoEvaluacion;

    @OneToMany(mappedBy = "reporte", fetch = FetchType.LAZY)
    private List<Modulo> modulos;

    private int puntajeGlobal;
    private int percentilGlobal;
    private String novedades;


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