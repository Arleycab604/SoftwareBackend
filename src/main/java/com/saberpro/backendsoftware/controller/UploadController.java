package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.service.CsvUploadService;
import com.saberpro.backendsoftware.service.ExcelUploadService;
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
    private final CsvUploadService csvUploadService;
    private final ExcelUploadService excelUploadService;

    @PostMapping( consumes = "multipart/form-data")
    public ResponseEntity<String> uploadCsv(
            @RequestParam("files") MultipartFile file,
            @RequestParam("year") int year,
            @RequestParam("periodo") int periodo) {
        String message = "";
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo no fue recibido.");
        }
        System.out.println("Archivo recibido: " + file.getOriginalFilename());
        try {
            if(file.getOriginalFilename().endsWith("xlsx")) {
                message= excelUploadService.uploadExcel(file, year, periodo);
            }
            else if(file.getOriginalFilename().endsWith("csv")) {
                message = csvUploadService.uploadCSV(file, year, periodo);
            }else {
                return ResponseEntity.badRequest().body("Formato de archivo no soportado. Solo se permiten archivos .csv o .xlsx");
            }
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al procesar los archivos: " + e.getMessage());
        }
    }
}