package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.dto.EvidenciaAccionDeMejoraDTO;
import com.saberpro.backendsoftware.enums.ModulosSaberPro;
import com.saberpro.backendsoftware.service.EvidenciaAccionMejoraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/SaberPro/evidencias")
@RequiredArgsConstructor
public class EvidenciaAccionMejoraController {

    private final EvidenciaAccionMejoraService evidenciaService;

    @GetMapping
    public ResponseEntity<List<EvidenciaAccionDeMejoraDTO>> getAll() {
        return ResponseEntity.ok(evidenciaService.findAll());
    }

    @GetMapping("/modulo/{idModulo}")
    public ResponseEntity<List<EvidenciaAccionDeMejoraDTO>> getByModulo(@PathVariable ModulosSaberPro modulo) {
        return ResponseEntity.ok(evidenciaService.findByModuloPropuesta(modulo));
    }

    @GetMapping("/propuesta/{idPropuesta}")
    public ResponseEntity<List<EvidenciaAccionDeMejoraDTO>> getByPropuesta(@PathVariable Long idPropuesta) {
        return ResponseEntity.ok(evidenciaService.findByPropuestaId(idPropuesta));
    }

    @PostMapping(value = "/crear", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EvidenciaAccionDeMejoraDTO> createEvidencia(
            @ModelAttribute EvidenciaAccionDeMejoraDTO dto) throws IOException {
        return ResponseEntity.ok(evidenciaService.create(dto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EvidenciaAccionDeMejoraDTO> updateEvidencia(
            @PathVariable Long id,
            @ModelAttribute EvidenciaAccionDeMejoraDTO dto) throws IOException {
        return ResponseEntity.ok(evidenciaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvidencia(@PathVariable Long id) {
        evidenciaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
