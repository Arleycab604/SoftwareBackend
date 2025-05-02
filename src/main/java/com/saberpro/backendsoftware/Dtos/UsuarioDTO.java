package com.saberpro.backendsoftware.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioDTO {
    private String nombreUsuario;
    private String tipoDeUsuario;
    private String correo;
}