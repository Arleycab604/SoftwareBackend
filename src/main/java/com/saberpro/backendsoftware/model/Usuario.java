package com.saberpro.backendsoftware.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Estrategia de herencia
public class Usuario {
    @Id
    private String nombreUsuario;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String tipoDeUsuario;

    private String correo;

    @ManyToOne
    @JoinColumn(name = "sniesId", referencedColumnName = "sniesId")
    @JsonBackReference
    private Programa programa;

    public void setSniesId(int sniesId) {
        if (this.programa != null) {
            this.programa.setSniesId(sniesId);
        } else {
            Programa p = new Programa();
            p.setSniesId(sniesId);
            this.programa = p;
        }
    }
}