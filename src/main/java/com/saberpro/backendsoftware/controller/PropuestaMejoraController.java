package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.Utils.CorreosService;
import com.saberpro.backendsoftware.Utils.UploadArchive;
import com.saberpro.backendsoftware.dto.PropuestaMejoraDTO;
import com.saberpro.backendsoftware.enums.ModulosSaberPro;
import com.saberpro.backendsoftware.enums.PropuestaMejoraState;
import com.saberpro.backendsoftware.model.PropuestaMejora;
import com.saberpro.backendsoftware.model.Usuario;
import com.saberpro.backendsoftware.security.JwtUtil;
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
@RequestMapping("/SaberPro/propuestas")
@RequiredArgsConstructor
public class PropuestaMejoraController {

    private final PropuestaMejoraService propuestaService;
    private final UsuarioRepository usuarioRepo;
    private final UploadArchive uploadArchive;
    private final CorreosService correosService;
    private final JwtUtil jwtService;

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPropuestaPorId(@PathVariable Long id) {
        PropuestaMejora propuesta = propuestaService.obtenerPorId(id);
        return ResponseEntity.ok(propuesta);
    }
    @GetMapping
    public ResponseEntity<List<PropuestaMejoraDTO>> listarTodas() {
        return ResponseEntity.ok(propuestaService.listarTodas().stream()
                .filter(p -> p.getIdPropuestaMejora() != 0 && p.getFechaCreacion() != null)
                .map(propuestaService::toDTO)
                .toList());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PropuestaMejoraDTO>> listarPorEstado(@PathVariable PropuestaMejoraState estado) {
        return ResponseEntity.ok(propuestaService.listarPorEstado(estado).stream()
                .map(propuestaService::toDTO).toList());
    }

    @GetMapping("/modulo/{modulo}")
    public ResponseEntity<List<PropuestaMejoraDTO>> listarPorModulo(@PathVariable ModulosSaberPro modulo) {
        return ResponseEntity.ok(propuestaService.listarPorModulo(modulo).stream()
                .map(propuestaService::toDTO).toList());
    }

    @GetMapping("/usuario/{nombreUsuario}")
    public ResponseEntity<List<PropuestaMejoraDTO>> listarPorUsuario(@PathVariable String nombreUsuario) {
        return ResponseEntity.ok(propuestaService.listarPorUsuario(nombreUsuario).stream()
                .map(propuestaService::toDTO).toList());
    }

    //Enviar correo de creado a comite de programa
    @PostMapping(value = "/crear", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> crearPropuesta(
            @ModelAttribute PropuestaMejoraDTO request,
            @RequestHeader("Authorization") String authHeader) throws IOException {
        System.out.println("procesando solicitud de creación de propuesta" + request);
        // Buscar usuario proponente
        Usuario usuario;
        if (request.getUsuarioProponente() == "null" || request.getUsuarioProponente() == null) {
            usuario = usuarioRepo.findByNombreUsuario("DECANO")
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        } else {
            usuario = usuarioRepo.findByNombreUsuario(String.valueOf(request.getUsuarioProponente()))
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }

        List<String> rutasTemporales = new ArrayList<>();
        for (MultipartFile archivo : request.getArchivos()) {
            File tempFile = File.createTempFile("upload_", "_" + archivo.getOriginalFilename());
            archivo.transferTo(tempFile);
            rutasTemporales.add(tempFile.getAbsolutePath());
        }

        PropuestaMejora propuesta = new PropuestaMejora(
                request.getNombrePropuesta(),
                usuario,
                null,
                request.getDescripcion(),
                request.getModuloPropuesta(),
                LocalDateTime.now(),
                LocalDateTime.parse(request.getFechaLimiteEntrega())
        );

        PropuestaMejora creada = propuestaService.crearPropuesta(propuesta, rutasTemporales);

        if (creada != null) {
            System.out.println("Propuesta creada y enviando correo a comite de programa");
            correosService.notificarCreacionPropuesta(creada.getNombrePropuesta());
        }

        // Eliminar archivos temporales
        for (String ruta : rutasTemporales) {
            File tempFile = new File(ruta);
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
        return ResponseEntity.ok(creada);
    }

    //enviar correo de modificado a comite de programa
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
            byte[] archivo = uploadArchive.downloadFile(fileName, uploadArchive.getBucketPropuestas());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());

            return new ResponseEntity<>(archivo, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/aceptar")
    public ResponseEntity<?> aceptarPropuesta(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        System.out.println("ID a enviar: " + id);
        System.out.println(authHeader);
        String nombreUsuario = extraerNombreUsuario(authHeader);
        propuestaService.responderPropuesta(id, nombreUsuario, true);

        return ResponseEntity.ok("Propuesta aceptada por " + nombreUsuario);
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazarPropuesta(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String nombreUsuario = extraerNombreUsuario(authHeader);
        propuestaService.responderPropuesta(id, nombreUsuario, false);

        return ResponseEntity.ok("Propuesta rechazada por " + nombreUsuario);
    }

    private String extraerNombreUsuario(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token de autorización inválido");
        }
        String token = authHeader.substring(7); // Remove "Bearer "
        return jwtService.extractUsername(token); // Método típico en servicio de JWT
    }

    @PostMapping("/{id}/requiere-cambios")
    public ResponseEntity<?> requiereCambiosPropuesta(@PathVariable Long id) {
        PropuestaMejora actualizada = propuestaService.cambiarEstado(id, PropuestaMejoraState.REQUIERE_CAMBIOS);
        return ResponseEntity.ok(actualizada);
    }


}
