package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.Utils.UploadArchive;
import com.saberpro.backendsoftware.dto.PropuestaMejoraDTO;
import com.saberpro.backendsoftware.enums.PropuestaMejoraState;
import com.saberpro.backendsoftware.model.PropuestaMejora;
import com.saberpro.backendsoftware.model.Usuario;
import com.saberpro.backendsoftware.repository.UsuarioRepository;
import com.saberpro.backendsoftware.service.PropuestaMejoraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/propuestas")
@RequiredArgsConstructor
public class PropuestaMejoraController {

    private final PropuestaMejoraService propuestaService;
    private final UsuarioRepository usuarioRepo;

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPropuestaPorId(@PathVariable Long id) {
        PropuestaMejora propuesta = propuestaService.obtenerPorId(id);
        return ResponseEntity.ok(propuesta);
    }
    @GetMapping
    public ResponseEntity<?> listarTodas() {
        return ResponseEntity.ok(propuestaService.listarTodas());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> listarPorEstado(@PathVariable PropuestaMejoraState estado) {
        return ResponseEntity.ok(propuestaService.listarPorEstado(estado));
    }

    @GetMapping("/modulo/{modulo}")
    public ResponseEntity<?> listarPorModulo(@PathVariable String modulo) {
        return ResponseEntity.ok(propuestaService.listarPorModulo(modulo));
    }

    @GetMapping("/usuario/{nombreUsuario}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable String nombreUsuario) {
        return ResponseEntity.ok(propuestaService.listarPorUsuario(nombreUsuario));
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearPropuesta(
            @ModelAttribute PropuestaMejoraDTO request) throws IOException {

        // Buscar usuario proponente
        Usuario usuario = usuarioRepo.findById(String.valueOf(request.getUsuarioProponente()))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<String> rutasTemporales = new ArrayList<>();
        for (MultipartFile archivo : request.getArchivos()) {
            File tempFile = File.createTempFile("upload_", "_" + archivo.getOriginalFilename());
            archivo.transferTo(tempFile);
            rutasTemporales.add(tempFile.getAbsolutePath());
        }

        PropuestaMejora propuesta = new PropuestaMejora(
                request.getNombrePropuesta(),
                usuario,
                PropuestaMejoraState.PENDIENTE,
                request.getDescripcion(),
                request.getModuloPropuesta(),
                LocalDateTime.now(),
                LocalDateTime.parse(request.getFechaLimiteEntrega())
        );

        PropuestaMejora creada = propuestaService.crearPropuesta(propuesta, rutasTemporales);

        return ResponseEntity.ok(creada);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PropuestaMejora> modificarPropuesta(
            @PathVariable Long id,
            @RequestPart("dto") PropuestaMejoraDTO dto) {

        PropuestaMejora actualizada = propuestaService.modificarPropuesta(id, dto);
        return ResponseEntity.ok(actualizada);
    }
    @GetMapping("/documento/download/{fileName}")
    public ResponseEntity<byte[]> descargarDesdeSupabase(@PathVariable String fileName) {
        try {
            byte[] archivo = UploadArchive.getInstance().downloadFile(fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());

            return new ResponseEntity<>(archivo, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/{id}/aceptar")
    public ResponseEntity<?> aceptarPropuesta(@PathVariable Long id) {
        PropuestaMejora actualizada = propuestaService.cambiarEstado(id, PropuestaMejoraState.ACEPTADA);
        return ResponseEntity.ok(actualizada);
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazarPropuesta(@PathVariable Long id) {
        PropuestaMejora actualizada = propuestaService.cambiarEstado(id, PropuestaMejoraState.RECHAZADA);
        return ResponseEntity.ok(actualizada);
    }

    @PostMapping("/{id}/requiere-cambios")
    public ResponseEntity<?> requiereCambiosPropuesta(@PathVariable Long id) {
        PropuestaMejora actualizada = propuestaService.cambiarEstado(id, PropuestaMejoraState.REQUIERE_CAMBIOS);
        return ResponseEntity.ok(actualizada);
    }


}
