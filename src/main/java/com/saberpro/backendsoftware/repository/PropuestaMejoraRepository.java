package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.PropuestaMejora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropuestaMejoraRepository extends JpaRepository<PropuestaMejora, Long> {
}
