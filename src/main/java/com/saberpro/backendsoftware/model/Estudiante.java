package com.saberpro.backendsoftware.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Estudiante {
    @Id
    private Long documento;

    private String tipoDocumento;
    private String nombreEstudiante;
    private String tipoDeEvaluado;
    private String ciudad;

    @ManyToOne
    @JoinColumn(name = "snies_id", referencedColumnName = "sniesId")
    @JsonBackReference
    private Programa programa;

    public Estudiante() {
        documento = 0L;
        tipoDocumento = "";
        nombreEstudiante = "";
        tipoDeEvaluado = "";
        ciudad = "";
    }
    public Estudiante(Long documento, String tipoDocumento, String nombreEstudiante, String tipoDeEvaluado, String ciudad) {
        this.documento = documento;
        this.tipoDocumento = tipoDocumento;
        this.nombreEstudiante = nombreEstudiante;
        this.tipoDeEvaluado = tipoDeEvaluado;
        this.ciudad = ciudad;
    }
    public String toString() {
        return "Estudiante{" +
                "documento=" + documento +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", nombreEstudiante='" + nombreEstudiante + '\'' +
                ", tipoDeEvaluado='" + tipoDeEvaluado + '\'' +
                ", ciudad='" + ciudad + '\'' +
                '}';
    }
}