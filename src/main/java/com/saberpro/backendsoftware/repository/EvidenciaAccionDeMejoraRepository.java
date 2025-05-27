package com.saberpro.backendsoftware.repository;


import com.saberpro.backendsoftware.enums.ModulosSaberPro;
import com.saberpro.backendsoftware.model.EvidenciaAccionDeMejora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EvidenciaAccionDeMejoraRepository extends JpaRepository<EvidenciaAccionDeMejora, Long> {
    Optional<EvidenciaAccionDeMejora> findByPropuestaMejora_idPropuestaMejora(Long idPropuestaMejora);
    List<EvidenciaAccionDeMejora> findByPropuestaMejora_ModuloPropuesta(ModulosSaberPro modulo);

}
