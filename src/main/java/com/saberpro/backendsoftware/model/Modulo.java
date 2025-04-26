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
    private String numeroRegistro;
    private int puntajeModulo;
    private String nivelDesempeno;
    private int percentilNacional;

    @ManyToOne
    @JoinColumn(name = "codModulo")
    private Reporte codModulo;
}
