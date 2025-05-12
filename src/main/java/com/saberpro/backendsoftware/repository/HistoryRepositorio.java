package com.saberpro.backendsoftware.repository;

import com.saberpro.backendsoftware.model.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepositorio extends JpaRepository<History, Long> {
}
