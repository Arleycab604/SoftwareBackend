package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.Dtos.InputQueryDTO;
import com.saberpro.backendsoftware.Dtos.ReporteDTO;
import com.saberpro.backendsoftware.service.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class QueryController {

    private final QueryService queryService;

    @PostMapping("/filtrar")
    public ResponseEntity<List<ReporteDTO>> filtrarReportes(@RequestBody InputQueryDTO inputQueryDTO) {
        List<ReporteDTO> resultados = queryService.filtrarDatos(inputQueryDTO);
        return ResponseEntity.ok(resultados);
    }
}