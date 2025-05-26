package com.saberpro.backendsoftware.dto;

import com.saberpro.backendsoftware.enums.ModulosSaberPro;
import com.saberpro.backendsoftware.enums.PropuestaMejoraState;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PropuestaMejoraDTO {
    private String nombrePropuesta;
    private ModulosSaberPro moduloPropuesta;
    private String descripcion;
    private String usuarioProponente;
    private String fechaCreacion;
    private String fechaLimiteEntrega;
    private PropuestaMejoraState estadoPropuesta;
    // Nuevos archivos añadidos por el usuario
    private MultipartFile[] archivos;

    // Lista de URLs de archivos que el usuario desea mantener
    private List<String> urlsDocumentoDetalles;
}