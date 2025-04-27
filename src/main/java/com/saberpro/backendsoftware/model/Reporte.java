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
    private String numero_Registro;

    @ManyToOne
    @JoinColumn(name = "nombreUsuario", referencedColumnName = "nombreUsuario")
    private Estudiante documento;

    private int year;
    private int periodo;
    private int puntajeGlobal;
    private int percentilGlobal;

    @OneToMany(mappedBy = "codModulo")
    private List<Modulo> modulos;

    private String novedades;

    public Reporte() {
        numero_Registro = "";
        year = 0;
        periodo = 0;
        modulos = new ArrayList<>(7);
    }
}