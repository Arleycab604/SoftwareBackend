package com.saberpro.backendsoftware.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class InputQueryDTO {

    @JsonProperty("year")
    private Integer year;
    @JsonProperty("periodo")
    private Integer periodo;
    @JsonProperty("nombreUsuario")
    private String nombreUsuario;
    @JsonProperty("nombrePrograma")
    private String nombrePrograma;
    @JsonProperty("grupoDeReferencia")
    private String grupoDeReferencia;
    @JsonProperty("numeroRegistro")
    private String numeroRegistro;
    @JsonProperty("puntajeGlobalMinimo")
    private Integer puntajeGlobalMinimo;
    @JsonProperty("puntajeGlobalMaximo")
    private Integer puntajeGlobalMaximo;
    @JsonProperty("percentilGlobal")
    private Integer percentilGlobal;
    @JsonProperty("novedades")
    private String novedades;
    @JsonProperty("tipoModulo")
    private String tipoModulo;
    @JsonProperty("puntajeModuloMinimo")
    private Integer puntajeModuloMinimo;
    @JsonProperty("puntajeModuloMaximo")
    private Integer puntajeModuloMaximo;
    @JsonProperty("nivelDesempeno")
    private String nivelDesempeno;
    @JsonProperty("percentilModulo")
    private Integer percentilModulo;

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
                ", puntajeModuloMinimo=" + puntajeModuloMinimo +
                ", puntajeModuloMaximo=" + puntajeModuloMaximo +
                ", nivelDesempeno='" + nivelDesempeno + '\'' +
                ", percentilModulo=" + percentilModulo +
                '}';
    }
}