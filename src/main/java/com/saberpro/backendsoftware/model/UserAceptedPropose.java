package com.saberpro.backendsoftware.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class UserAceptedPropose {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "idPropuestaMejora")
    private PropuestaMejora propuestaMejora;
    @ManyToOne
    @JoinColumn(name = "nombreUsuario")
    private Usuario usuario;

    private Boolean acepted;

    public boolean isAcepted() {
        return acepted != null && acepted;
    }
}
