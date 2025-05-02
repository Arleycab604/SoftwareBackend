package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.Dtos.InputQueryDTO;
import com.saberpro.backendsoftware.Dtos.ReporteDTO;
import com.saberpro.backendsoftware.service.QueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class QueryController {

    private final QueryService queryService;

    @Operation(summary = "Filtrar reportes", description = "Filtra reportes según los criterios proporcionados en el cuerpo de la solicitud.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reportes filtrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReporteDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })

    @PostMapping("/filtrar")
    public ResponseEntity<List<ReporteDTO>> filtrarReportes(
            @RequestBody(description = "Criterios de filtrado", required = true)
            InputQueryDTO inputQueryDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<ReporteDTO> resultados = queryService.filtrarDatos(inputQueryDTO, pageable);
        return ResponseEntity.ok(resultados);
    }
}