package com.saberpro.backendsoftware.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PropuestaMejora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idPropuestaMejora;
    private String nombrePropuesta;

    @ManyToOne
    @JoinColumn
    private Usuario usuarioProponente;
    private String moduloPropuesta;
    private String descripcion;
    private byte[] DocumentoDetalles; //Guia

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLimiteEntrega;
}
