package com.saberpro.backendsoftware.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@RequiredArgsConstructor
public class InputQueryDTO {
    //Propiedades de paginacion
    private int page = 0;
    private int size = 20;

    //Propiedades para filtrar
    @JsonProperty("year")
    private Integer year;
    @JsonProperty("periodo")
    private Integer periodo;

    @JsonProperty("nombreUsuario") // nombre estudiante
    private String nombreUsuario;
    @JsonProperty("documento")
    private String documento;
    @JsonProperty("nombrePrograma") // Por ahora solo hay un programa :v
    private String nombrePrograma;

    // Pos funciona
    @JsonProperty("puntajeGlobalMinimo")
    private Integer puntajeGlobalMinimo;
    @JsonProperty("puntajeGlobalMaximo")
    private Integer puntajeGlobalMaximo;

    // Pray to god que funcione
    @JsonProperty("tipoModulo")
    private String tipoModulo;

    @JsonProperty("puntajeModuloMinimo")
    private Integer puntajeModuloMinimo;
    @JsonProperty("puntajeModuloMaximo")
    private Integer puntajeModuloMaximo;

    // Dificil de implementar
    @JsonProperty("nivelDesempeno")
    private String nivelDesempeno;

    @Override
    public String toString() {
        return "InputQueryDTO{" +
                "year=" + year +
                ", periodo=" + periodo +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", nombrePrograma='" + nombrePrograma + '\'' +
                ", puntajeGlobalMinimo=" + puntajeGlobalMinimo +
                ", puntajeGlobalMaximo=" + puntajeGlobalMaximo +
                ", tipoModulo='" + tipoModulo + '\'' +
                ", puntajeModuloMinimo=" + puntajeModuloMinimo +
                ", puntajeModuloMaximo=" + puntajeModuloMaximo +
                ", nivelDesempeno='" + nivelDesempeno + '\'' +
                '}';
    }
}