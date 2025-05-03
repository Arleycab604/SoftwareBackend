package com.saberpro.backendsoftware.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@Entity
public class Programa {
    @Id
    private int sniesId;
    private String nombrePrograma;
    private String grupoDeReferencia;

    @JsonIgnore
    @JsonManagedReference
    @ManyToMany
    @JoinColumn(name = "nombreUsuario", referencedColumnName = "nombreUsuario")
    private List<Usuario> usuarios = new ArrayList<>();

    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "programa")
    private List<Estudiante>  estudiantes= new ArrayList<Estudiante>();

}
