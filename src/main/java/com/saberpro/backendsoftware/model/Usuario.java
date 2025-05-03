package com.saberpro.backendsoftware.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Estrategia de herencia
public class Usuario {
    @Id
    @Column(nullable = false)
    private String nombreUsuario;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String tipoDeUsuario;

    private String correo;

    @ManyToMany
    @JoinColumn(name = "sniesId", referencedColumnName = "sniesId")
    private List<Programa> programas;

    public Usuario() {
        nombreUsuario = "";
        password = "";
        tipoDeUsuario = "";
        correo = "";
        programas = new ArrayList<>();
    }
    public Usuario(String nombreUsuario, String password, String tipoDeUsuario, String correo, List<Programa> programas) {
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.tipoDeUsuario = tipoDeUsuario;
        this.correo = correo;
        this.programas = programas;
    }
    public void AddPrograma(Programa programa) {
        if (programas != null) {
            programas.add(programa);
        } else {
            throw new IllegalStateException("La lista de programas no está inicializada.");
        }
    }
}