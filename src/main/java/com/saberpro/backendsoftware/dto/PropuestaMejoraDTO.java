package com.saberpro.backendsoftware.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PropuestaMejoraDTO {
    private String nombrePropuesta;
    private String moduloPropuesta;
    private String descripcion;
    private Long idUsuarioProponente;
    private String fechaLimiteEntrega;
    private MultipartFile[] archivos;
}