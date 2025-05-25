package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.dto.UsuarioDTO;
import com.saberpro.backendsoftware.enums.AccionHistorico;
import com.saberpro.backendsoftware.enums.TipoUsuario;
import com.saberpro.backendsoftware.model.Usuario;
import com.saberpro.backendsoftware.service.HistoryService;
import com.saberpro.backendsoftware.service.UsuarioService;
import com.saberpro.backendsoftware.Utils.CorreosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/SaberPro/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final HistoryService historyService;
    private final CorreosService correosService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAllUsers() {
        System.out.println("Cargando usuarios...");
        List<UsuarioDTO> usuarios = usuarioService.findAllUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    //Crear usuario
    @PostMapping("/register")
    public ResponseEntity<String>  registrarUsuario(
            @RequestBody Usuario usuario,//Nombre usuario y correo
            @RequestHeader ("Authorization") String authHeader) {
        boolean creado = usuarioService.crearUsuario(usuario);

        if (creado){
            historyService.registrarAccion(authHeader,
                    AccionHistorico.Crear_usuario,
                    "Se ha creado un usuario: " + usuario.getNombreUsuario());

            return ResponseEntity.ok( "Usuario creado exitosamente.");
        }else{
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe.");
        }
    }

    @DeleteMapping
    public ResponseEntity<String> eliminarUsuario(
            @RequestParam String nombreUsuario,
            @RequestHeader ("Authorization") String authHeader) {

        boolean eliminado = usuarioService.eliminarUsuario(nombreUsuario);

        if (eliminado) {
            historyService.registrarAccion(authHeader,
                AccionHistorico.Eliminar_usuario,
                "Se ha eliminado el usuario: " + nombreUsuario);

            return ResponseEntity.ok("Usuario eliminado exitosamente.");
        } else {
            return ResponseEntity.badRequest().body("No se pudo eliminar el usuario. Verifica el nombre del usuario.");
        }
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Usuario usuario) {
        //Enviar mensaje al correo del usuario sobre inicio de sesion
        System.out.println("Usuario datos: " + usuario);

        String token = usuarioService.login(usuario.getNombreUsuario(), usuario.getPassword());
        Map<String, String> response = new HashMap<>();
        response.put("token", token != null ? token : "Credenciales inválidas.");
        if(token != null){
            System.out.println("Enviando correo de inicio de sesión a: " + usuario.getNombreUsuario());
            correosService.notificarInicioSesion(usuario.getNombreUsuario());
        }
        return response;
    }
    @GetMapping("/recoverPassword")
    public ResponseEntity<String> recuperarContrasena(@RequestParam String nombreUsuario) {
        String codigo = correosService.enviarCorreoRecuperacion(nombreUsuario);
        if (!codigo.isEmpty()) {
            usuarioService.guardarCodigoRecuperacion(nombreUsuario, codigo); // método nuevo
            return ResponseEntity.ok("Se ha enviado un código de recuperación al correo del usuario.");
        } else {
            return ResponseEntity.badRequest().body("Usuario no encontrado o error al enviar el correo.");
        }
    }
    @GetMapping("/verifyCode")
    public ResponseEntity<String> verificarCodigo(@RequestParam String nombreUsuario, @RequestParam String codigo) {
        boolean valido = usuarioService.verificarCodigo(nombreUsuario, codigo);
        if (valido) {
            return ResponseEntity.ok("Código válido.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código inválido.");
        }
    }
    @PostMapping("/changePassword")
    public ResponseEntity<?> cambiarContrasena(@RequestBody Map<String, String> datos) {
        String usuario = datos.get("nombreUsuario");
        String nueva = datos.get("nuevaContrasena");

        if (usuario == null || nueva == null || nueva.length() < 6) {
            return ResponseEntity.badRequest().body("Datos inválidos o contraseña muy corta.");
        }

        boolean actualizada = usuarioService.cambiarContrasena(usuario, nueva);
        if (actualizada) {
            return ResponseEntity.ok("Contraseña cambiada con éxito.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }
    }
    @GetMapping("/findByRole")
    public ResponseEntity<List<UsuarioDTO>> buscarUsuariosPorTipo(@RequestParam TipoUsuario tipoUsuario) {
        List<UsuarioDTO> usuarios = usuarioService.buscarPorTipoUsuario(tipoUsuario);
        return ResponseEntity.ok(usuarios);
    }
    @GetMapping("/findByName")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorNombre(@RequestParam String nombreUsuario) {
        Optional<UsuarioDTO> usuario = usuarioService.buscarPorNombreUsuario(nombreUsuario);
        return ResponseEntity.ok(usuario.orElse(null));
    }

    @PutMapping("/assignRole")
    public ResponseEntity<String> cambiarRol(
            @RequestParam String nombreUsuario,
            @RequestParam TipoUsuario nuevoRol,
            @RequestHeader ("Authorization") String authHeader) {

        boolean actualizado = usuarioService.cambiarRolUsuario(nombreUsuario, nuevoRol);
        if (actualizado) {
            historyService.registrarAccion(authHeader,
                    AccionHistorico.Cambiar_rol_usuario,
                    "Se ha cambiado el rol del usuario: " + nombreUsuario + " a: " + nuevoRol);
            return ResponseEntity.ok("Rol actualizado exitosamente.");
        } else {
            return ResponseEntity.badRequest().body("No se pudo actualizar el rol. Verifica el nombre del usuario.");
        }
    }
}