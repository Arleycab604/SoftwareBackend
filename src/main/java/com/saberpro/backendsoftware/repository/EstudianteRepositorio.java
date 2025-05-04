package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.stereotype.Repository;
@Repository
public interface EstudianteRepositorio extends JpaRepository<Estudiante, Long> {
    List<Estudiante> findByProgramaSniesId(int sniesId);
}