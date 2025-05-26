package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.dto.InputFilterYearDTO;
import com.saberpro.backendsoftware.dto.ReporteYearDTO;
import com.saberpro.backendsoftware.service.QueryYearService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/SaberPro/reporteYear")
@RequiredArgsConstructor
public class QueryYearController {

    private final QueryYearService queryYearService;

    @PostMapping("/Query")
    public ResponseEntity<List<ReporteYearDTO>> filterByYear(@RequestBody InputFilterYearDTO filter,
                                                             @RequestHeader ("Authorization") String authHeader) {
        List<ReporteYearDTO> results = queryYearService.filterByYear(filter);
        return ResponseEntity.ok(results);
    }
}
