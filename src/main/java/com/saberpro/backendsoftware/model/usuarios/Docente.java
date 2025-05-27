package com.saberpro.backendsoftware.model.usuarios;

import com.saberpro.backendsoftware.enums.ModulosSaberPro;
import com.saberpro.backendsoftware.enums.TipoUsuario;
import com.saberpro.backendsoftware.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Docente {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private long idDocente;

    @JoinColumn(name ="nombreDocente" , referencedColumnName = "nombreUsuario")
    @OneToOne(cascade = CascadeType.ALL)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModulosSaberPro moduloMaterias;
}
