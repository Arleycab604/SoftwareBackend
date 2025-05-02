package com.saberpro.backendsoftware.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    private String programa;
    private String grupoDeReferencia;

    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "programa")
    private List<Usuario> usuarios = new ArrayList<Usuario>();
}
