package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.Dtos.UsuarioDTO;
import com.saberpro.backendsoftware.model.Usuario;
import com.saberpro.backendsoftware.repository.UsuarioRepositorio;
import com.saberpro.backendsoftware.service.UsuarioService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/SaberPro/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioController(UsuarioService usuarioService, UsuarioRepositorio usuarioRepositorio) {
        this.usuarioService = usuarioService;
        this.usuarioRepositorio = usuarioRepositorio;
    }
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAllUsers() {
        System.out.println("Cargando usuarios...");
        List<UsuarioDTO> usuarios = usuarioService.findAllUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    //Crear usuario
    @PostMapping("/register")
    public Map<String, String> registrarUsuario(@RequestBody Usuario usuario) {
        boolean creado = usuarioService.crearUsuario(usuario);
        Map<String, String> response = new HashMap<>();
        response.put("message", creado ? "Usuario creado exitosamente." : "El nombre de usuario ya existe.");
        return response;
    }
    @DeleteMapping
    public ResponseEntity<String> eliminarUsuario(@RequestParam String nombreUsuario) {
        boolean eliminado = usuarioService.eliminarUsuario(nombreUsuario);
        if (eliminado) {
            return ResponseEntity.ok("Usuario eliminado exitosamente.");
        } else {
            return ResponseEntity.badRequest().body("No se pudo eliminar el usuario. Verifica el nombre del usuario.");
        }
    }
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Usuario usuario) {
        System.out.println("Usuario datos: " + usuario);
        String token = usuarioService.login(usuario.getNombreUsuario(), usuario.getPassword());
        Map<String, String> response = new HashMap<>();
        response.put("token", token != null ? token : "Credenciales inválidas.");
        return response;
    }

    @GetMapping("/findByRole")
    public ResponseEntity<List<UsuarioDTO>> buscarUsuariosPorTipo(@RequestParam String tipoUsuario) {
        List<UsuarioDTO> usuarios = usuarioService.buscarPorTipoUsuario(tipoUsuario);
        return ResponseEntity.ok(usuarios);
    }
    @PutMapping("/assignRole")
    public ResponseEntity<String> cambiarRol(@RequestParam String nombreUsuario, @RequestParam String nuevoRol) {
        boolean actualizado = usuarioService.cambiarRolUsuario(nombreUsuario, nuevoRol);
        if (actualizado) {
            return ResponseEntity.ok("Rol actualizado exitosamente.");
        } else {
            return ResponseEntity.badRequest().body("No se pudo actualizar el rol. Verifica el nombre del usuario.");
        }
    }
}