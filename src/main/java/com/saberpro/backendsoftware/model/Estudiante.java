package com.saberpro.backendsoftware.model;

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

    @OneToOne
    @JoinColumn(name = "nombreUsuario", referencedColumnName = "nombreUsuario")
    private Usuario nombreUsuario;

    private String tipoDeEvaluado;
    private String ciudad;
}
