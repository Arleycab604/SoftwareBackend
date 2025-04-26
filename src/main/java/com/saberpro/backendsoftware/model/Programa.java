package com.saberpro.backendsoftware.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Programa {
    @Id
    private int sniesId;
    private String programa;
    private String grupoDeReferencia;

    @OneToMany(mappedBy = "programa")
    private List<Usuario> usuarios = new ArrayList<Usuario>();
}
