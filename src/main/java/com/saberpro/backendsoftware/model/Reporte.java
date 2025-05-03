package com.saberpro.backendsoftware.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Reporte {
    @Id
    private String numeroRegistro;

    @ManyToOne
    @JoinColumn(name = "nombreEstudiante", referencedColumnName = "nombreEstudiante")
    private Estudiante estudiante;

    private int year;
    private int periodo;
    private int puntajeGlobal;
    private int percentilGlobal;

    @OneToMany(mappedBy = "reporte")
    private List<Modulo> modulos;

    private String novedades;

    public Reporte() {
        numeroRegistro = "";
        year = 0;
        periodo = 0;
        modulos = new ArrayList<>(7);
    }
}