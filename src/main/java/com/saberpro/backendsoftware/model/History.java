package com.saberpro.backendsoftware.model;

import com.saberpro.backendsoftware.enums.AccionHistorico;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class History {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long idHistory;
    //Usuario que realiza la accion
    @ManyToOne
    @JoinColumn(name = "nombreUsuario", referencedColumnName = "nombreUsuario")
    private Usuario usuario;
    //Rol que el usuario tenia en esa fecha
    private String rolHistorico;
    private LocalDate fechaAccion;
    private String accion;
    //Accion realizada por el usuario
    //Ejemplo: Usuario(nombre) ha {creado un usuario, añadido un reporte para el (periodo), eliminado un usuario, etc} en (Fecha).
    private String detalles;

    public History() {
        rolHistorico = "";
        fechaAccion = LocalDate.now();
        accion = "";
        detalles = "";
    }
    public History(Usuario usuario,
                   String rolHistorico,
                   LocalDate fechaAccion,
                   AccionHistorico accion,
                   String detalles) {

        this.usuario = usuario;
        this.rolHistorico = rolHistorico;
        this.fechaAccion = fechaAccion;
        this.accion = accion.toString();
        this.detalles = detalles;
    }
}
