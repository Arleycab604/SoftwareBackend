package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.Dtos.UsuarioDTO;
import com.saberpro.backendsoftware.model.Usuario;
import com.saberpro.backendsoftware.repository.UsuarioRepositorio;
import com.saberpro.backendsoftware.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/SaberPro/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioController(UsuarioService usuarioService, UsuarioRepositorio usuarioRepositorio) {
        this.usuarioService = usuarioService;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @PostMapping("/registro")
    public Map<String, String> registrarUsuario(@RequestBody Usuario usuario) {
        boolean creado = usuarioService.crearUsuario(usuario);
        Map<String, String> response = new HashMap<>();
        response.put("message", creado ? "Usuario creado exitosamente." : "El nombre de usuario ya existe.");
        return response;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Usuario usuario) {
        System.out.println("Usuario datos: " + usuario);
        String token = usuarioService.login(usuario.getNombreUsuario(), usuario.getPassword());
        Map<String, String> response = new HashMap<>();
        response.put("token", token != null ? token : "Credenciales inválidas.");

        return response;
    }

    @GetMapping("/buscarPorTipo")
    public ResponseEntity<List<UsuarioDTO>> buscarUsuariosPorTipo(@RequestParam String tipoUsuario) {
        List<UsuarioDTO> usuarios = usuarioService.buscarPorTipoUsuario(tipoUsuario);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/excluirPorTipo")
    public ResponseEntity<List<UsuarioDTO>> buscarUsuariosExcluyendoTipo(@RequestParam String tipoExcluido) {
        List<UsuarioDTO> usuarios = usuarioService.buscarUsuariosExcluyendoTipo(tipoExcluido);
        return ResponseEntity.ok(usuarios);
    }
    @PutMapping("/cambiarRol")
    public ResponseEntity<String> cambiarRol(@RequestParam String nombreUsuario, @RequestParam String nuevoRol) {
        boolean actualizado = usuarioService.cambiarRolUsuario(nombreUsuario, nuevoRol);
        if (actualizado) {
            return ResponseEntity.ok("Rol actualizado exitosamente.");
        } else {
            return ResponseEntity.badRequest().body("No se pudo actualizar el rol. Verifica el nombre del usuario.");
        }
    }
}