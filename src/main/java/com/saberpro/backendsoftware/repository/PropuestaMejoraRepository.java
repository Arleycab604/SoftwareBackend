package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.enums.PropuestaMejoraState;
import com.saberpro.backendsoftware.model.PropuestaMejora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropuestaMejoraRepository extends JpaRepository<PropuestaMejora, Long> {
    List<PropuestaMejora> findByEstadoPropuesta(PropuestaMejoraState estado);
    List<PropuestaMejora> findByModuloPropuestaIgnoreCase(String modulo);
    List<PropuestaMejora> findByUsuarioProponente_nombreUsuario(String nombreUsuario);

}
