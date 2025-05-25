package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.enums.ModulosSaberPro;
import com.saberpro.backendsoftware.model.usuarios.Docente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocenteRepository extends JpaRepository<Docente, Long> {
    List<Docente> findByModuloMaterias(ModulosSaberPro moduloMaterias);
    Optional<Docente> findByUsuario_NombreUsuario(String nombreUsuario);
}
