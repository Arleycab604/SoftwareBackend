package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.usuarios.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
}