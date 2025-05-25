package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.UserAceptedPropose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAceptedProposeRepository extends JpaRepository<UserAceptedPropose, Long> {
    List<UserAceptedPropose>  findByPropuestaMejora_IdPropuestaMejora(long idPropuestaMejora);

    UserAceptedPropose findByPropuestaMejora_IdPropuestaMejoraAndUsuario_NombreUsuario(Long idPropuesta, String nombreUsuario);
}
