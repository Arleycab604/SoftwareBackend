package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.ReporteYear;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReporteYearRepositorio extends JpaRepository<ReporteYear, Integer> {
    void deleteByYearAndPeriodo(int year, int periodo);
}