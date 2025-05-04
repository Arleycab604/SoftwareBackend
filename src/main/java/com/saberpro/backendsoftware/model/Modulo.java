package com.saberpro.backendsoftware.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Modulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idModulo;

    private String tipo;
    private int puntajeModulo;
    private String nivelDesempeno;
    private int percentilModulo;

    @ManyToOne
    @JoinColumn(name = "reporte_id", referencedColumnName = "numeroRegistro")
    private Reporte reporte;

    public Modulo() {
        tipo = "";
        puntajeModulo = 0;
        nivelDesempeno = "";
        percentilModulo = 0;
    }
    public Modulo(String tipo,Reporte reporte, int puntajeModulo, String nivelDesempeno, int percentilModulo) {
        this.tipo = tipo;
        this.puntajeModulo = puntajeModulo;
        this.nivelDesempeno = nivelDesempeno;
        this.percentilModulo = percentilModulo;
        this.reporte = reporte;
    }
    public String toString() {
        return "Modulo{" +
                "idModulo=" + idModulo +
                ", tipo='" + tipo + '\'' +
                ", puntajeModulo=" + puntajeModulo +
                ", nivelDesempeno='" + nivelDesempeno + '\'' +
                ", percentilModulo=" + percentilModulo +
                '}';
    }
}