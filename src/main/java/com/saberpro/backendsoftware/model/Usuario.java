package com.saberpro.backendsoftware.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
public class Usuario {
    @Id
    private String nombreUsuario;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String tipoDeUsuario;

    @OneToOne(mappedBy = "nombreUsuario") // Relación bidireccional con Estudiante
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "sniesId", referencedColumnName = "sniesId")
    private Programa programa;


    public void setSniesId(int sniesId) {
        if (this.programa != null) {
            this.programa.setSniesId(sniesId); // solo actualizas el ID
        } else {
            Programa p = new Programa();
            p.setSniesId(sniesId);
            this.programa = p;
        }
    }
}
