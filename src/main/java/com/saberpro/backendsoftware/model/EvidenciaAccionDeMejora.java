package com.saberpro.backendsoftware.model;

import com.saberpro.backendsoftware.model.usuarios.Docente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class EvidenciaAccionDeMejora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "idDocente")
    private Docente docente;
    @ElementCollection
    private List<String> urlsEvidencias = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "idPropuestaMejora")
    private PropuestaMejora propuestaMejora;
    private LocalDateTime fechaEntrega;
}
