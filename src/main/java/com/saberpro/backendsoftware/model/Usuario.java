package com.saberpro.backendsoftware.model;

import com.saberpro.backendsoftware.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipoDeUsuario;

    private String correo;
    private LocalDate fechaFinRol;

    @ManyToOne
    @JoinColumn(name = "sniesId", referencedColumnName = "sniesId")
    private Programa programa;

    public Usuario() {
        nombreUsuario = "";
        password = "";
        tipoDeUsuario = TipoUsuario.DOCENTE;
        correo = "";
    }
    public Usuario(String nombreUsuario, String password, TipoUsuario tipoDeUsuario, String correo) {
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