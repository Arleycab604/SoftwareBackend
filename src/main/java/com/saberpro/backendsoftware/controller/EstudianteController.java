package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.model.Estudiante;
import com.saberpro.backendsoftware.repository.EstudianteRepositorio;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;


public class EstudianteController {
    private final EstudianteRepositorio repositorio;

    public EstudianteController(EstudianteRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping("/{documento}")
    public Optional<Estudiante> getEstudiante(@PathVariable Long documento) {
        return repositorio.findById(documento) //Remover
                .filter(u -> u instanceof Estudiante)
                .map(u -> (Estudiante) u);
    }
}
