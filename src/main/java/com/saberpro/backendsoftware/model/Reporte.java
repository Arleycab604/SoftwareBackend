package com.saberpro.backendsoftware.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
public class Reporte {
    @Id
    private String numero_Registro;
    @OneToOne
    @JoinColumn(name = "documento", referencedColumnName = "documento")
    private Estudiante documento;
    private int year;
    private int periodo;
    private int puntajeGlobal;
    private int percentilGlobal;
    @OneToMany(mappedBy = "codModulo")
    private List<Modulo> modulos;
    private String novedades;
    public Reporte(){
        numero_Registro = "";
        documento = new Estudiante();
        year = 0;
        periodo = 0;
        modulos = new ArrayList<>(7);


    }
    public Reporte(String numero_Registro, Estudiante documento, int year, int periodo, List<Modulo> modulos) {
        this.numero_Registro = numero_Registro;
        this.documento = documento;
        this.year = year;
        this.periodo = periodo;
        this.modulos.addAll(modulos);
    }
    public enum TipoModulo {
        ComunicacionEscrita,
        LecturaCritica,
        RazonamientoCuantitativo,
        CompetenciasCiudadanas,
        Ingles,
        FormulacionDeProyectos,
        DiseñoDeSoftware,
        MatematicasYEstadistica
    }
    public Modulo getModulo(TipoModulo type){
        switch (type){
            case ComunicacionEscrita:
                return modulos.get(0);
            case LecturaCritica:
                return modulos.get(1);
            case RazonamientoCuantitativo:
                return modulos.get(2);
            case CompetenciasCiudadanas:
                return modulos.get(3);
            case Ingles:
                return modulos.get(4);
            case FormulacionDeProyectos:
                return modulos.get(5);
            case DiseñoDeSoftware:
                return modulos.get(6);
            case MatematicasYEstadistica:
                return modulos.get(7);
            default:
                return null;
        }

    }

}
