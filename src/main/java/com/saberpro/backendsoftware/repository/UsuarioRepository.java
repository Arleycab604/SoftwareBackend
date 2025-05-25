package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.enums.TipoUsuario;
import com.saberpro.backendsoftware.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
   Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    List<Usuario> findByFechaFinRolBefore(LocalDate fecha);
    List<Usuario> findByTipoDeUsuario(TipoUsuario tipoDeUsuario);
    List<Usuario> findByTipoDeUsuarioNot(TipoUsuario tipoDeUsuario);

}