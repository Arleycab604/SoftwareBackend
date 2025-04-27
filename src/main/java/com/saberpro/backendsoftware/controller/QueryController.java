package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.Dtos.InputQueryDTO;
import com.saberpro.backendsoftware.Dtos.ReporteDTO;
import com.saberpro.backendsoftware.service.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class QueryController {

    private final QueryService queryService;

    @PostMapping("/filtrar")
    public ResponseEntity<List<ReporteDTO>> filtrarReportes(
            @RequestBody InputQueryDTO inputQueryDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<ReporteDTO> resultados = queryService.filtrarDatos(inputQueryDTO, pageable);
        return ResponseEntity.ok(resultados);
    }
}