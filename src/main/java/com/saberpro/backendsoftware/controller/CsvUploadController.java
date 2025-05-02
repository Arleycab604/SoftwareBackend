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
    public ResponseEntity<String> uploadCsv(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("year") int year,
            @RequestParam("periodo") int periodo) {
        StringBuilder responseMessage = new StringBuilder();
        try {
            for (MultipartFile file : files) {
                // Procesar el archivo sin validar el nombre
                String message = csvUploadService.uploadExcel(file, year, periodo);
                responseMessage.append(message).append("\n");
            }
            return ResponseEntity.ok(responseMessage.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al procesar los archivos: " + e.getMessage());
        }
    }
}