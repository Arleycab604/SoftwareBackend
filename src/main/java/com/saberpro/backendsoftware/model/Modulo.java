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

    @Column(insertable = false, updatable = false)
    private String numeroRegistro;

    private int puntajeModulo;
    private String nivelDesempeno;
    private int percentilNacional;

    @ManyToOne
    @JoinColumn(name = "numeroRegistro", referencedColumnName = "numeroRegistro")
    private Reporte reporte;
}