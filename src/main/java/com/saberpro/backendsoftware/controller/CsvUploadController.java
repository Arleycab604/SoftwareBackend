package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.service.CsvUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permite acceso desde frontend (ajusta según tu origen)
public class CsvUploadController {

    private final CsvUploadService csvUploadService;

    @PostMapping(value = "/csv", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            String message = csvUploadService.uploadExcel(file);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al procesar el archivo: " + e.getMessage());
        }
    }
}
