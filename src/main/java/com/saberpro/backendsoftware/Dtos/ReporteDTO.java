package com.saberpro.backendsoftware.Dtos;

import lombok.Data;

@Data
public class ReporteDTO {
    // Datos del estudiante
    private Long documento;
    private String tipoDocumento;
    private String ciudad;
    private String tipoDeEvaluado;
    // Datos del usuario
    private String nombreUsuario;
    // Datos del programa
    private int sniesId;
    private String nombrePrograma;
    private String grupoDeReferencia;

    // Datos del reporte
    private String numeroRegistro;
    private int year;
    private int periodo;
    private int puntajeGlobal;
    private int percentilGlobal;
    private String novedades;

    // Datos de módulos (resumen ejemplo: podrías tener una lista si quieres más detalle)
    private String tipoModulo;
    private int puntajeModulo;
    private String nivelDesempeno;
    private int percentilModulo;

    public ReporteDTO() {
        // Constructor vacío
    }
    public ReporteDTO(Long documento,String tipoDocumento,
    String ciudad, String tipoDeEvaluado, String nombreUsuario,
    int sniesId,String nombrePrograma,String grupoDeReferencia,
    String numeroRegistro,int year,int periodo,int puntajeGlobal,int percentilGlobal,
    String novedades,String tipoModulo,int puntajeModulo,String nivelDesempeno,int percentilModulo) {
        this.documento = documento;
        this.tipoDocumento = tipoDocumento;
        this.ciudad = ciudad;
        this.tipoDeEvaluado = tipoDeEvaluado;
        this.nombreUsuario = nombreUsuario;
        this.sniesId = sniesId;
        this.nombrePrograma = nombrePrograma;
        this.grupoDeReferencia = grupoDeReferencia;
        this.numeroRegistro = numeroRegistro;
        this.year = year;
        this.periodo = periodo;
        this.puntajeGlobal = puntajeGlobal;
        this.percentilGlobal = percentilGlobal;
        this.novedades = novedades;
        this.tipoModulo = tipoModulo;
        this.puntajeModulo = puntajeModulo;
        this.nivelDesempeno = nivelDesempeno;
        this.percentilModulo = percentilModulo;
    }
}
