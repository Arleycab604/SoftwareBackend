package com.saberpro.backendsoftware.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PropuestaMejoraDTO {
    private String nombrePropuesta;
    private String moduloPropuesta;
    private String descripcion;
    private String usuarioProponente;
    private String fechaLimiteEntrega;

    // Nuevos archivos añadidos por el usuario
    private MultipartFile[] archivos;

    // Lista de URLs de archivos que el usuario desea mantener
    private List<String> urlsDocumentoDetalles;
}