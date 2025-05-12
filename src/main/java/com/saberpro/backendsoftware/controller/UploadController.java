package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.Utils._HistoricActions;
import com.saberpro.backendsoftware.service.CsvUploadService;
import com.saberpro.backendsoftware.service.ExcelUploadService;
import com.saberpro.backendsoftware.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/SaberPro/upload")
@CrossOrigin(origins = "*") //Cambiar al dominio del frontend
public class UploadController {
    private final CsvUploadService csvUploadService;
    private final ExcelUploadService excelUploadService;

    @Autowired
    private final HistoryService historyService;


    @PostMapping( consumes = "multipart/form-data")
    public ResponseEntity<String> uploadCsv(
            @RequestParam("files") MultipartFile file,
            @RequestParam("year") int year,
            @RequestParam("periodo") int periodo,
            @RequestHeader("Authorization") String authHeader){

        //Añadir los usuarios que pueden acceder a este endpoint (opcional)

        String message;
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo no fue recibido.");
        }
        String fileName = file.getOriginalFilename();
        System.out.println("Archivo recibido: " + fileName );
        System.out.println("Token recibido: " + authHeader);

        //Maneja lo de subir el historico de que acciones se han realizado.
        historyService.registrarAccion(authHeader,
                _HistoricActions.Add_reporte_Saber_pro,
                "El usuario subió un archivo: " + fileName +
                        " para el año: " + year + " y periodo: " + periodo);

        try {
            //Posiblemente implementar xls para arhivos de antes de 2007
            if(Objects.requireNonNull(fileName).endsWith("xlsx")) {
                message= excelUploadService.uploadExcel(file, year, periodo);
            }
            else if(fileName.endsWith("csv")) {
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