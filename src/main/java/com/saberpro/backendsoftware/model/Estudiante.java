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
    @JoinColumn(name = "programa", referencedColumnName = "sniesId")
    @JsonBackReference
    private Programa programa;
}
