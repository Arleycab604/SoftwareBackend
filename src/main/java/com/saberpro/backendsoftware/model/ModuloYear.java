package com.saberpro.backendsoftware.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ModuloYear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idModuloYear;
    private int year;
    private int periodo;
    private String tipoModulo;
    private double mediaModuloYear;
    private double varianzaModuloYear;
    private double coeficienteVariacionModuloYear;
}
