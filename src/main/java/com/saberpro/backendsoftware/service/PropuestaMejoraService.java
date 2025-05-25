package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.Utils.UploadArchive;
import com.saberpro.backendsoftware.dto.PropuestaMejoraDTO;
import com.saberpro.backendsoftware.enums.TipoUsuario;
import com.saberpro.backendsoftware.model.PropuestaMejora;
import com.saberpro.backendsoftware.enums.PropuestaMejoraState;
import com.saberpro.backendsoftware.model.UserAceptedPropose;
import com.saberpro.backendsoftware.model.Usuario;
import com.saberpro.backendsoftware.repository.PropuestaMejoraRepository;
import com.saberpro.backendsoftware.repository.UserAceptedProposeRepository;
import com.saberpro.backendsoftware.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropuestaMejoraService {
    private final UsuarioRepository usuarioRepo;
    private final UserAceptedProposeRepository userAceptedRepo;
    private final PropuestaMejoraRepository propuestaRepo;

    private final UploadArchive uploadService = UploadArchive.getInstance();


    public PropuestaMejora obtenerPorId(Long id) {
        return propuestaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Propuesta no encontrada"));
    }
    public List<PropuestaMejora> listarTodas() {
        return propuestaRepo.findAll();
    }

    public List<PropuestaMejora> listarPorEstado(PropuestaMejoraState estado) {
        return propuestaRepo.findByEstadoPropuesta(estado);
    }

    public List<PropuestaMejora> listarPorModulo(String modulo) {
        return propuestaRepo.findByModuloPropuestaIgnoreCase(modulo);
    }

    public List<PropuestaMejora> listarPorUsuario(String nombreUsuario) {
        return propuestaRepo.findByUsuarioProponente_nombreUsuario(nombreUsuario);
    }

    /**
     * Crea y guarda una nueva propuesta de mejora, subiendo el archivo a la DB

     * @param propuesta Instancia base (sin archivo aún)
     * @param localFilePaths Rutas locales de los archivos a subir
     * @return propuesta guardada
     */
    public PropuestaMejora crearPropuesta(PropuestaMejora propuesta, List<String> localFilePaths) throws IOException {
        List<String> urls = new ArrayList<>();

        for (String path : localFilePaths) {
            String url = uploadService.uploadFile(path);
            urls.add(url);
        }

        propuesta.setUrlsDocumentoDetalles(urls);
        propuesta.setFechaCreacion(LocalDateTime.now());

        // Guardar propuesta primero (para tener ID asignado)
        propuesta = propuestaRepo.save(propuesta);

        // Buscar usuarios del comité
        List<Usuario> comiteUsuarios = usuarioRepo.findByTipoDeUsuario(TipoUsuario.COMITE_DE_PROGRAMA);

        for (Usuario usuarioComite : comiteUsuarios) {
            UserAceptedPropose registro = new UserAceptedPropose();
            registro.setPropuestaMejora(propuesta);
            registro.setUsuario(usuarioComite);
            registro.setAcepted(false); // Estado inicial

            userAceptedRepo.save(registro);

            // Simulación de envío de correo
            System.out.println("Correo enviado a " + usuarioComite.getCorreo() +
                    ": Nueva propuesta '" + propuesta.getNombrePropuesta() +
                    "' requiere su revisión.");
        }

        return propuesta;
    }

    public void responderPropuesta(Long idPropuesta, String nombreUsuario, boolean acepted) {
        PropuestaMejora propuesta = propuestaRepo.findById(idPropuesta)
                .orElseThrow(() -> new RuntimeException("Propuesta no encontrada"));

        UserAceptedPropose respuesta = userAceptedRepo.findByPropuestaMejora_IdPropuestaMejoraAndUsuario_NombreUsuario(idPropuesta, nombreUsuario);
        if (respuesta == null) throw new RuntimeException("No autorizado para responder esta propuesta");

        respuesta.setAcepted(acepted);
        userAceptedRepo.save(respuesta);

        // Validar el estado general
        List<UserAceptedPropose> respuestas = userAceptedRepo.findByPropuestaMejora_IdPropuestaMejora(idPropuesta);

        boolean hayPendientes = respuestas.stream().anyMatch(r -> r.getAcepted() == null);
        long total = respuestas.size();
        long aceptados = respuestas.stream().filter(r -> Boolean.TRUE.equals(r.getAcepted())).count();
        long rechazados = respuestas.stream().filter(r -> Boolean.FALSE.equals(r.getAcepted())).count();

        if (hayPendientes) {
            propuesta.setEstadoPropuesta(PropuestaMejoraState.PENDIENTE);
        } else if (aceptados == total) {
            propuesta.setEstadoPropuesta(PropuestaMejoraState.ACEPTADA);
        } else if (rechazados == total) {
            propuesta.setEstadoPropuesta(PropuestaMejoraState.RECHAZADA);
        } else {
            propuesta.setEstadoPropuesta(PropuestaMejoraState.REQUIERE_CAMBIOS);
        }

        propuestaRepo.save(propuesta);
    }

    public PropuestaMejora modificarPropuesta(Long id, PropuestaMejoraDTO dto) {
        PropuestaMejora propuesta = propuestaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Propuesta no encontrada"));

        propuesta.setNombrePropuesta(dto.getNombrePropuesta());
        propuesta.setDescripcion(dto.getDescripcion());
        propuesta.setModuloPropuesta(dto.getModuloPropuesta());
        propuesta.setFechaLimiteEntrega(LocalDateTime.parse(dto.getFechaLimiteEntrega()));

        // Manejo de archivos
        List<String> urlsAntiguas = new ArrayList<>(propuesta.getUrlsDocumentoDetalles());
        List<String> urlsAConservar = dto.getUrlsDocumentoDetalles() != null ? dto.getUrlsDocumentoDetalles() : new ArrayList<>();

        // Eliminar archivos que ya no están
        for (String url : urlsAntiguas) {
            if (!urlsAConservar.contains(url)) {
                uploadService.eliminarArchivoDeSupabase(url);
            }
        }

        propuesta.setUrlsDocumentoDetalles(new ArrayList<>(urlsAConservar));

        // Subir nuevos archivos
        if (dto.getArchivos() != null) {
            for (MultipartFile archivo : dto.getArchivos()) {
                try {
                    File temp = File.createTempFile("temp-", archivo.getOriginalFilename());
                    archivo.transferTo(temp);

                    String nuevaUrl = UploadArchive.getInstance().uploadFile(temp.getAbsolutePath());
                    propuesta.getUrlsDocumentoDetalles().add(nuevaUrl);

                    temp.delete();
                } catch (IOException e) {
                    throw new RuntimeException("Error al subir archivo: " + archivo.getOriginalFilename(), e);
                }
            }
        }

        return propuestaRepo.save(propuesta);
    }


    public PropuestaMejora cambiarEstado(Long idPropuesta, PropuestaMejoraState nuevoEstado) {
        PropuestaMejora propuesta = propuestaRepo.findById(idPropuesta)
                .orElseThrow(() -> new RuntimeException("Propuesta no encontrada"));

        propuesta.setEstadoPropuesta(nuevoEstado);
        return propuestaRepo.save(propuesta);
    }
}