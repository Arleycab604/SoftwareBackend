package com.saberpro.backendsoftware.Dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

//Maneja un solo reporte con un módulo con su puntaje general
//Igual a como sale en el excel
@Getter
@Setter

@RequiredArgsConstructor
// by Puntaje minimo, maxim, y por puntaje,
// By year periodo, year, periodo
public class InputQueryDTO {
    @JsonIgnoreProperties(ignoreUnknown = true)

    @JsonProperty("year")
    private int year;
    @JsonProperty("periodo")
    private int periodo;
    @JsonProperty("nombreUsuario")
    private String nombreUsuario;
    @JsonProperty("nombrePrograma")
    private String nombrePrograma;
    @JsonProperty("grupoDeReferencia")
    private String grupoDeReferencia;
    @JsonProperty("numeroRegistro")
    private String numeroRegistro;
    @JsonProperty("puntajeGlobalMinimo")
    private int puntajeGlobalMinimo;
    @JsonProperty("puntajeGlobalMaximo")
    private int puntajeGlobalMaximo;
    @JsonProperty("percentilGlobal")
    private int percentilGlobal;
    @JsonProperty("novedades")
    private String novedades;
    @JsonProperty("tipoModulo")
    private String tipoModulo;

    @JsonProperty("puntajeModuloMinimo")
    private int puntajeModuloMinimo;
    @JsonProperty("puntajeModuloMaximo")
    private int puntajeModuloMaximo;
    @JsonProperty("nivelDesempeno")
    private String nivelDesempeno;
    @JsonProperty("percentilModulo")
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
                ", puntajeMinimoModulo=" + puntajeModuloMinimo +
                ", puntajeMaximoModulo=" + puntajeModuloMaximo +
                ", nivelDesempeno='" + nivelDesempeno + '\'' +
                ", percentilModulo=" + percentilModulo +
                '}';
    }
}
