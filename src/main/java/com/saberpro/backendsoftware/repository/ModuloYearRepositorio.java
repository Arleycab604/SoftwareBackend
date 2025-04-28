package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.ModuloYear;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuloYearRepositorio extends JpaRepository<ModuloYear, Long> {
    void deleteByYearAndPeriodo(int year, int periodo);
}