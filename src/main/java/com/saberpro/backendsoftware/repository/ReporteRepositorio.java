package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.Modulo;
import com.saberpro.backendsoftware.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReporteRepositorio extends JpaRepository<Reporte, String> {
    List<Reporte> findByYearAndPeriodo(int year,int periodo);
}