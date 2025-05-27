package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.Utils.SupabaseProperties;
import com.saberpro.backendsoftware.Utils.UploadArchive;
import com.saberpro.backendsoftware.dto.PropuestaMejoraDTO;
import com.saberpro.backendsoftware.enums.ModulosSaberPro;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropuestaMejoraService {
    private final SupabaseProperties supabaseProperties;
    private final UsuarioRepository usuarioRepo;
    private final UserAceptedProposeRepository userAceptedRepo;
    private final PropuestaMejoraRepository propuestaRepo;

    private final UploadArchive uploadService;


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

    public List<PropuestaMejora> listarPorModulo(ModulosSaberPro modulo) {
        return propuestaRepo.findByModuloPropuesta(modulo);
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
            String url = uploadService.uploadFile(path,supabaseProperties.getBucketPropuestas());
            urls.add(url);
        }

        propuesta.setUrlsDocumentoDetalles(urls);
        propuesta.setFechaCreacion(LocalDateTime.now());
        propuesta.setEstadoPropuesta(PropuestaMejoraState.PENDIENTE);
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
        System.out.println(nombreUsuario + " responderPropuesta " + idPropuesta + " aceptada " + acepted);
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

        // Si no hay URLs enviadas, borrar todas las antiguas
        if (dto.getUrlsDocumentoDetalles() == null || dto.getUrlsDocumentoDetalles().isEmpty()) {
            for (String url : propuesta.getUrlsDocumentoDetalles()) {
                uploadService.eliminarArchivoDeSupabase(url,supabaseProperties.getBucketPropuestas());
            }
            propuesta.setUrlsDocumentoDetalles(new ArrayList<>());
        } else {
            // Conservar solo las URLs indicadas (caso si en futuro se añada UI para eso)
            List<String> urlsAntiguas = new ArrayList<>(propuesta.getUrlsDocumentoDetalles());
            List<String> urlsAConservar = dto.getUrlsDocumentoDetalles();

            for (String url : urlsAntiguas) {
                if (!urlsAConservar.contains(url)) {
                    uploadService.eliminarArchivoDeSupabase(url,supabaseProperties.getBucketPropuestas());
                }
            }
            propuesta.setUrlsDocumentoDetalles(new ArrayList<>(urlsAConservar));
        }

        // Subir nuevos archivos
        if (dto.getArchivos() != null && dto.getArchivos().length > 0) {
            for (MultipartFile archivo : dto.getArchivos()) {
                try {
                    File temp = File.createTempFile("temp-", archivo.getOriginalFilename());
                    archivo.transferTo(temp);
                    String nuevaUrl = uploadService.uploadFile(temp.getAbsolutePath(),supabaseProperties.getBucketPropuestas());
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

    public PropuestaMejoraDTO toDTO(PropuestaMejora propuesta) {
        if (propuesta == null || propuesta.getIdPropuestaMejora() == 0 || propuesta.getFechaCreacion() == null) {
            return null; // o lanzar una excepción si prefieres manejarlo con error
        }

        PropuestaMejoraDTO dto = new PropuestaMejoraDTO();
        dto.setIdPropuestaMejora(propuesta.getIdPropuestaMejora());
        dto.setNombrePropuesta(propuesta.getNombrePropuesta());
        dto.setModuloPropuesta(propuesta.getModuloPropuesta());
        dto.setEstadoPropuesta(propuesta.getEstadoPropuesta());
        dto.setDescripcion(propuesta.getDescripcion());

        for(String url : propuesta.getUrlsDocumentoDetalles()) {

        }

        if (propuesta.getUsuarioProponente() != null) {
            dto.setUsuarioProponente(propuesta.getUsuarioProponente().getNombreUsuario()); // o el campo correspondiente
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dto.setFechaCreacion(propuesta.getFechaCreacion().format(formatter));

        if (propuesta.getFechaLimiteEntrega() != null) {
            dto.setFechaLimiteEntrega(propuesta.getFechaLimiteEntrega().format(formatter));
        }

        dto.setUrlsDocumentoDetalles(propuesta.getUrlsDocumentoDetalles());

        // dto.setArchivos(null); // No se asignan archivos ya cargados (solo usados en POST/PUT desde el frontend)

        return dto;
    }
}