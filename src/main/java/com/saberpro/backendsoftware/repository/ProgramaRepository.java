package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.Programa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramaRepository extends JpaRepository<Programa, Integer> {
}