package com.saberpro.backendsoftware.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.saberpro.backendsoftware.model.usuarios.Estudiante;
import jakarta.persistence.*;
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

    private String nombrePrograma;
    private String grupoDeReferencia;


    public Programa(){
        sniesId = 0;
        nombrePrograma = "";
        grupoDeReferencia = "";
    }
    public Programa(int sniesId, String nombrePrograma, String grupoDeReferencia) {
        this.sniesId = sniesId;
        this.nombrePrograma = nombrePrograma;
        this.grupoDeReferencia = grupoDeReferencia;
    }


    public String toString() {
        return "Programa{" +
                "sniesId=" + sniesId +
                ", nombrePrograma='" + nombrePrograma + '\'' +
                ", grupoDeReferencia='" + grupoDeReferencia + '\'' +
                '}';
    }
}