package com.saberpro.backendsoftware.repository;


import com.saberpro.backendsoftware.model.EvidenciaAccionDeMejora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvidenciaAccionDeMejoraRepository extends JpaRepository<EvidenciaAccionDeMejora, Long> {

}
