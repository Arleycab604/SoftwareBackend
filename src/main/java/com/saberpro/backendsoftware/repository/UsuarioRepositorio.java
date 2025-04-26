package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
}
