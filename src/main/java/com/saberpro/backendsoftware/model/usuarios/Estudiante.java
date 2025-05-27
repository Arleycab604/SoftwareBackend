package com.saberpro.backendsoftware.model.usuarios;

import com.saberpro.backendsoftware.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Estudiante {
    @Id
    private Long documento;
    private String tipoDocumento;

    @OneToOne
    @JoinColumn(name = "nombre_estudiante", referencedColumnName = "nombreUsuario")
    private Usuario usuario;
    private String tipoDeEvaluado;
    private String ciudad;


    public Estudiante() {
        documento = 0L;
        tipoDocumento = "";

        tipoDeEvaluado = "";
        ciudad = "";
    }
    public Estudiante(Long documento, String tipoDocumento, Usuario user, String tipoDeEvaluado, String ciudad) {
        this.documento = documento;
        this.tipoDocumento = tipoDocumento;
        this.usuario = user;
        this.tipoDeEvaluado = tipoDeEvaluado;
        this.ciudad = ciudad;
    }
    public String toString() {
        return "Estudiante{" +
                "documento=" + documento +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", nombreEstudiante='" + usuario.getNombreUsuario() + '\'' +
                ", tipoDeEvaluado='" + tipoDeEvaluado + '\'' +
                ", ciudad='" + ciudad + '\'' +
                '}';
    }
}