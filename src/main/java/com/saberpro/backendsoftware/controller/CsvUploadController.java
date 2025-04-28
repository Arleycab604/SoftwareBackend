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
    public ResponseEntity<String> uploadCsv(@RequestParam("files") MultipartFile[] files) {
        StringBuilder responseMessage = new StringBuilder();
        try {
            for (MultipartFile file : files) {
                // Validar el formato del nombre del archivo
                String fileName = file.getOriginalFilename();
                if (fileName == null || !fileName.matches("\\d{4}-\\d{1,2}\\.csv")) {
                    return ResponseEntity.badRequest().body("El archivo " + fileName + " no cumple con el formato requerido (año-periodo.csv).");
                }
                // Validar las columnas específicas (puedes implementar esta lógica en el servicio)
                String message = csvUploadService.uploadExcel(file);
                responseMessage.append(message).append("\n");
            }
            return ResponseEntity.ok(responseMessage.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al procesar los archivos: " + e.getMessage());
        }
    }
}