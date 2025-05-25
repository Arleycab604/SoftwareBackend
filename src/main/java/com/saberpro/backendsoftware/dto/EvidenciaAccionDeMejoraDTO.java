package com.saberpro.backendsoftware.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Data
public class EvidenciaAccionDeMejoraDTO {

    private String nombreDocente;
    private Long idPropuestaMejora;
    private String fechaEntrega;
    private MultipartFile[] archivos;
    private List<String> urlsEvidencias;
}
