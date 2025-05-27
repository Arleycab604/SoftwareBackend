package com.saberpro.backendsoftware.model;

import com.saberpro.backendsoftware.enums.ModulosSaberPro;
import com.saberpro.backendsoftware.enums.PropuestaMejoraState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private PropuestaMejoraState estadoPropuesta;

    private String descripcion;
    @ElementCollection
    private List<String> urlsDocumentoDetalles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ModulosSaberPro moduloPropuesta;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLimiteEntrega;

    public PropuestaMejora(){
        this.nombrePropuesta = "";
        this.usuarioProponente = null;
        this.estadoPropuesta = PropuestaMejoraState.PENDIENTE;
        this.descripcion = "";
        this.moduloPropuesta = ModulosSaberPro.COMPETENCIAS_CIUDADANAS;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaLimiteEntrega = LocalDateTime.now();
    }
    public PropuestaMejora(String nombrePropuesta,
                           Usuario usuarioProponente,
                           PropuestaMejoraState estadoPropuesta,
                           String descripcion, ModulosSaberPro moduloPropuesta, LocalDateTime fechaCreacion, LocalDateTime fechaLimiteEntrega) {
        this.nombrePropuesta = nombrePropuesta;
        this.usuarioProponente = usuarioProponente;
        this.estadoPropuesta = estadoPropuesta;
        this.descripcion = descripcion;
        this.moduloPropuesta = moduloPropuesta;
        this.fechaCreacion = fechaCreacion;
        this.fechaLimiteEntrega = fechaLimiteEntrega;
    }
    public void addUrlDocumentoDetalles(String url) {
        this.urlsDocumentoDetalles.add(url);
    }
}
