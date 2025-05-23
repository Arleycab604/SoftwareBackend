package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.model.Estudiante;
import com.saberpro.backendsoftware.repository.EstudianteRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;

//Unused
public class EstudianteController {
    private final EstudianteRepository repositorio;

    public EstudianteController(EstudianteRepository repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping("/{documento}")
    public Optional<Estudiante> getEstudiante(@PathVariable Long documento) {
        return repositorio.findById(documento);
    }
}
