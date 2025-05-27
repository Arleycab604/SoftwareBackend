package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.Utils.UploadArchive;
import com.saberpro.backendsoftware.dto.EvidenciaAccionDeMejoraDTO;
import com.saberpro.backendsoftware.enums.ModulosSaberPro;
import com.saberpro.backendsoftware.model.EvidenciaAccionDeMejora;
import com.saberpro.backendsoftware.model.PropuestaMejora;
import com.saberpro.backendsoftware.model.usuarios.Docente;
import com.saberpro.backendsoftware.repository.DocenteRepository;
import com.saberpro.backendsoftware.repository.EvidenciaAccionDeMejoraRepository;
import com.saberpro.backendsoftware.repository.PropuestaMejoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvidenciaAccionMejoraService {

    private final EvidenciaAccionDeMejoraRepository evidenciaRepo;
    private final PropuestaMejoraRepository propuestaRepo;
    private final DocenteRepository docenteRepo;
    private final UploadArchive uploadArchive;

    public List<EvidenciaAccionDeMejoraDTO> findAll() {
        return evidenciaRepo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EvidenciaAccionDeMejoraDTO> findByModuloPropuesta(ModulosSaberPro modulo) {
        return evidenciaRepo.findByPropuestaMejora_ModuloPropuesta(modulo).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EvidenciaAccionDeMejoraDTO> findByPropuestaId(Long idPropuesta) {
        return evidenciaRepo.findByPropuestaMejora_idPropuestaMejora(idPropuesta).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public EvidenciaAccionDeMejoraDTO create(EvidenciaAccionDeMejoraDTO dto) throws IOException {
        EvidenciaAccionDeMejora evidencia = new EvidenciaAccionDeMejora();

        Docente docente = docenteRepo.findByUsuario_NombreUsuario(dto.getNombreDocente())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        PropuestaMejora propuesta = propuestaRepo.findById(dto.getIdPropuestaMejora())
                .orElseThrow(() -> new RuntimeException("Propuesta de mejora no encontrada"));
        if(docente.getModuloMaterias() != propuesta.getModuloPropuesta()){
            throw new RuntimeException("El docente no puede crear evidencia para esta propuesta");
        }
        evidencia.setDocente(docente);
        evidencia.setPropuestaMejora(propuesta);
        evidencia.setFechaEntrega(LocalDateTime.now());

        List<String> urls = new ArrayList<>();
        if (dto.getUrlsEvidencias() != null) {
            urls.addAll(dto.getUrlsEvidencias());
        }

        if (dto.getArchivos() != null) {
            for (MultipartFile archivo : dto.getArchivos()) {
                String url = almacenarYObtenerURL(archivo);
                urls.add(url);
            }
        }

        evidencia.setUrlsEvidencias(urls);
        return toDTO(evidenciaRepo.save(evidencia));
    }

    public EvidenciaAccionDeMejoraDTO update(Long id, EvidenciaAccionDeMejoraDTO dto) throws IOException {
        EvidenciaAccionDeMejora evidencia = evidenciaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Evidencia no encontrada"));

        Docente docente = docenteRepo.findByUsuario_NombreUsuario(dto.getNombreDocente())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        PropuestaMejora propuesta = propuestaRepo.findById(dto.getIdPropuestaMejora())
                .orElseThrow(() -> new RuntimeException("Propuesta no encontrada"));

        evidencia.setDocente(docente);
        evidencia.setPropuestaMejora(propuesta);
        evidencia.setFechaEntrega(LocalDateTime.parse(dto.getFechaEntrega()));

        List<String> urls = new ArrayList<>();
        if (dto.getUrlsEvidencias() != null) {
            urls.addAll(dto.getUrlsEvidencias());
        }

        if (dto.getArchivos() != null) {
            for (MultipartFile archivo : dto.getArchivos()) {
                String url = almacenarYObtenerURL(archivo);
                urls.add(url);
            }
        }

        evidencia.setUrlsEvidencias(urls);
        return toDTO(evidenciaRepo.save(evidencia));
    }

    public void delete(Long id) {
        evidenciaRepo.deleteById(id);
    }

    private EvidenciaAccionDeMejoraDTO toDTO(EvidenciaAccionDeMejora evidencia) {
        EvidenciaAccionDeMejoraDTO dto = new EvidenciaAccionDeMejoraDTO();
        if (evidencia != null) {
            dto.setIdPropuestaMejora(evidencia.getId());
        }

        dto.setNombreDocente(evidencia.getDocente().getUsuario().getNombreUsuario());
        dto.setIdPropuestaMejora(evidencia.getPropuestaMejora().getIdPropuestaMejora());
        dto.setFechaEntrega(evidencia.getFechaEntrega().toString());
        dto.setUrlsEvidencias(evidencia.getUrlsEvidencias());
        return dto;
    }

    private String almacenarYObtenerURL(MultipartFile archivo) throws IOException {
        Path tempFile = Files.createTempFile("evidencia-", archivo.getOriginalFilename());
        archivo.transferTo(tempFile.toFile());

        String url = uploadArchive.uploadFile(tempFile.toString(), uploadArchive.getBucketEvidencias());

        Files.delete(tempFile); // limpiar archivo temporal

        return url;
    }
}
