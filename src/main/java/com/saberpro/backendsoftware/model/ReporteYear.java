package com.saberpro.backendsoftware.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class ReporteYear {
    @Id
    private int year;
    private int periodo;
    private double mediaPeriodo;
    private double varianzaPeriodo;
    private double coeficienteVariacion;

    @OneToMany(mappedBy = "idModuloYear")
    private List<ModuloYear> modulosYear;
}
