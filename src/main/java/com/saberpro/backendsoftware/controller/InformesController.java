package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.Utils.UploadArchive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Exception; // Importar S3Exception

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/SaberPro/informes")
@RequiredArgsConstructor
public class InformesController {

    private final UploadArchive uploadArchive;
    private static final String BUCKET_INFORMES = "informes"; // Considera hacer esto configurable a través de propiedades

    @PostMapping("/upload")
    public ResponseEntity<String> uploadInformes(@RequestParam("files") List<MultipartFile> files) {
        if (files.isEmpty()) {
            return ResponseEntity.badRequest().body("No se seleccionaron archivos para subir.");
        }

        List<File> tempFiles = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                if (!"application/pdf".equals(file.getContentType())) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("Solo se permiten archivos PDF.");
                }

                File tempFile = File.createTempFile("informe_", "_" + file.getOriginalFilename());
                file.transferTo(tempFile);
                tempFiles.add(tempFile);

                uploadArchive.uploadFile(tempFile.getAbsolutePath(), BUCKET_INFORMES);
                System.out.println("Archivo subido correctamente: " + file.getOriginalFilename());
            }
            return ResponseEntity.ok("¡Archivos subidos exitosamente!");
        } catch (IOException | S3Exception e) { // Captura S3Exception también
            System.err.println("Error al subir archivos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir archivos: " + e.getMessage());
        } finally {
            for (File tempFile : tempFiles) {
                try {
                    Files.deleteIfExists(tempFile.toPath());
                } catch (IOException e) {
                    System.err.println("Error al eliminar archivo temporal " + tempFile.getName() + ": " + e.getMessage());
                }
            }
        }
    }


    @GetMapping("/list")
    public ResponseEntity<List<String>> listInformes() {
        try {
            List<String> fileNames = uploadArchive.listFiles(BUCKET_INFORMES);
            return ResponseEntity.ok(fileNames);
        } catch (S3Exception e) { // Captura la S3Exception específica
            System.err.println("Error al listar archivos: " + e.awsErrorDetails().errorMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // Devuelve un cuerpo nulo en caso de error, como el original
        } catch (Exception e) { // Reserva para otras excepciones inesperadas
            System.err.println("Error inesperado al listar archivos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadInforme(@PathVariable String fileName) {
        try {
            byte[] archivo = uploadArchive.downloadFile(fileName, BUCKET_INFORMES);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // O inferir según la extensión del nombre del archivo si es necesario
            headers.setContentDisposition(org.springframework.http.ContentDisposition.attachment().filename(fileName).build());

            return new ResponseEntity<>(archivo, headers, HttpStatus.OK);

        } catch (IOException | S3Exception e) { // Captura S3Exception también
            System.err.println("Error al descargar archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteInforme(@PathVariable String fileName) {
        try {
            uploadArchive.eliminarArchivoDeSupabase(fileName, BUCKET_INFORMES);
            return ResponseEntity.ok("Archivo '" + fileName + "' eliminado exitosamente.");
        } catch (S3Exception e) { // Captura la S3Exception específica
            System.err.println("Error al eliminar archivo: " + e.awsErrorDetails().errorMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar archivo: " + e.awsErrorDetails().errorMessage()); // Proporciona el mensaje de error de S3
        } catch (Exception e) { // Reserva para otras excepciones inesperadas
            System.err.println("Error inesperado al eliminar archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado al eliminar archivo: " + e.getMessage());
        }
    }
}