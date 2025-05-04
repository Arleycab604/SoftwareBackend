package com.saberpro.backendsoftware.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {
    @Id
    @Column(nullable = false)
    private String nombreUsuario;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String tipoDeUsuario;

    private String correo;

    @ManyToOne
    @JoinColumn(name = "sniesId", referencedColumnName = "sniesId")
    private Programa programa;

    public Usuario() {
        nombreUsuario = "";
        password = "";
        tipoDeUsuario = "";
        correo = "";
    }
    public Usuario(String nombreUsuario, String password, String tipoDeUsuario, String correo) {
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.tipoDeUsuario = tipoDeUsuario;
        this.correo = correo;
    }

    public String toString() {
        return "Usuario{" +
                "nombreUsuario='" + nombreUsuario + '\'' +
                ", password='" + password + '\'' +
                ", tipoDeUsuario='" + tipoDeUsuario + '\'' +
                ", correo='" + correo + '\'' +
                '}';
    }
}