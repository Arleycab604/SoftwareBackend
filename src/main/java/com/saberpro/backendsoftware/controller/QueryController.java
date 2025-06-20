package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.dto.InputQueryDTO;
import com.saberpro.backendsoftware.dto.ReporteDTO;
import com.saberpro.backendsoftware.service.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/SaberPro/reportes")
@RequiredArgsConstructor
public class QueryController {

    private final QueryService queryService;

    @PostMapping("/Query")
    public ResponseEntity<List<ReporteDTO>> filtrarReportes(@RequestBody InputQueryDTO inputQueryDTO) {
        List<ReporteDTO> resultados = queryService.filtrarDatos(inputQueryDTO);
        return ResponseEntity.ok(resultados);
    }
}