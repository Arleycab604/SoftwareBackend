package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.model.Usuario;
import com.saberpro.backendsoftware.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public Map<String, String> registrarUsuario(@RequestBody Usuario usuario) {
        boolean creado = usuarioService.crearUsuario(usuario);
        Map<String, String> response = new HashMap<>();
        response.put("message", creado ? "Usuario creado exitosamente." : "El nombre de usuario ya existe.");
        return response;
    }

    /*
    {
     "nombreUsuario": "ABELLA BETANCOURT JORGE ANDRES",
     "password": "f6bf8dae"

    }
    */
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Usuario usuario) {
        String token = usuarioService.login(usuario.getNombreUsuario(), usuario.getPassword());
        Map<String, String> response = new HashMap<>();
        response.put("token", token != null ? token : "Credenciales inválidas.");
        return response;
    }
}
