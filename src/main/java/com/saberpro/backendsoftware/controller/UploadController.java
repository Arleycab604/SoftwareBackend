package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.service.CsvUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/SaberPro/upload")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permite acceso desde frontend (ajusta según tu origen)
public class UploadController {
    @Autowired
    private final CsvUploadService csvUploadService;

    @PostMapping(value = "/csv", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadCsv(
            @RequestParam("files") MultipartFile file,
            @RequestParam("year") int year,
            @RequestParam("periodo") int periodo) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo no fue recibido.");
        }
        System.out.println("Archivo recibido: " + file.getOriginalFilename());
        try {
            String message = csvUploadService.uploadExcel(file, year, periodo);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al procesar los archivos: " + e.getMessage());
        }
    }
}