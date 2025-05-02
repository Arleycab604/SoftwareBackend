package com.saberpro.backendsoftware.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

//Maneja un solo reporte con un solo modulo con su puntaje general
//Igual a como sale en el excel
@Getter
@Setter

@RequiredArgsConstructor
// by Puntaje minimo, maximo, y por puntaje,
// By year periodo, year, periodo
public class InputQueryDTO {
    private int year;
    private int periodo;
    private String nombreUsuario;
    private String nombrePrograma;
    private String grupoDeReferencia;
    private String numeroRegistro;
    private int puntajeGlobalMinimo;
    private int puntajeGlobalMaximo;
    private int percentilGlobal;
    private String novedades;
    private String tipoModulo;

    @JsonProperty("puntajeModuloMinimo")
    private int puntajeMinimoModulo;

    @JsonProperty("puntajeModuloMaximo")
    private int puntajeMaximoModulo;

    private String nivelDesempeno;
    private int percentilModulo;

    @Override
    public String toString() {
        return "InputQueryDTO{" +
                "year=" + year +
                ", periodo=" + periodo +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", nombrePrograma='" + nombrePrograma + '\'' +
                ", grupoDeReferencia='" + grupoDeReferencia + '\'' +
                ", numeroRegistro='" + numeroRegistro + '\'' +
                ", puntajeGlobalMinimo=" + puntajeGlobalMinimo +
                ", puntajeGlobalMaximo=" + puntajeGlobalMaximo +
                ", percentilGlobal=" + percentilGlobal +
                ", novedades='" + novedades + '\'' +
                ", tipoModulo='" + tipoModulo + '\'' +
                ", puntajeMinimoModulo=" + puntajeMinimoModulo +
                ", puntajeMaximoModulo=" + puntajeMaximoModulo +
                ", nivelDesempeno='" + nivelDesempeno + '\'' +
                ", percentilModulo=" + percentilModulo +
                '}';
    }
}
