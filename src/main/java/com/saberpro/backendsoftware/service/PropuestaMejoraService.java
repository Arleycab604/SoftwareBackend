package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.Utils.UploadArchive;
import com.saberpro.backendsoftware.dto.PropuestaMejoraDTO;
import com.saberpro.backendsoftware.model.PropuestaMejora;
import com.saberpro.backendsoftware.enums.PropuestaMejoraState;
import com.saberpro.backendsoftware.repository.PropuestaMejoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropuestaMejoraService {

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
        return propuestaRepo.findByEstado(estado);
    }

    public List<PropuestaMejora> listarPorModulo(String modulo) {
        return propuestaRepo.findByModuloPropuestaIgnoreCase(modulo);
    }

    public List<PropuestaMejora> listarPorUsuario(Long idUsuario) {
        return propuestaRepo.findByUsuarioProponente_Id(idUsuario);
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

        return propuestaRepo.save(propuesta);
    }
    public PropuestaMejora modificarPropuesta(Long id, PropuestaMejoraDTO dto) {
        PropuestaMejora propuesta = propuestaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Propuesta no encontrada"));

        propuesta.setNombrePropuesta(dto.getNombrePropuesta());
        propuesta.setDescripcion(dto.getDescripcion());
        propuesta.setModuloPropuesta(dto.getModuloPropuesta());
        propuesta.setFechaLimiteEntrega(LocalDateTime.parse(dto.getFechaLimiteEntrega()));
        // No se cambian archivos aquí; para eso usar otro endpoint

        return propuestaRepo.save(propuesta);
    }

    public PropuestaMejora cambiarEstado(Long idPropuesta, PropuestaMejoraState nuevoEstado) {
        PropuestaMejora propuesta = propuestaRepo.findById(idPropuesta)
                .orElseThrow(() -> new RuntimeException("Propuesta no encontrada"));

        propuesta.setEstadoPropuesta(nuevoEstado);
        return propuestaRepo.save(propuesta);
    }
}