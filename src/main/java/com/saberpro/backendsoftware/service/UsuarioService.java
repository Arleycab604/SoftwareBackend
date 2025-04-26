package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.model.Usuario;
import com.saberpro.backendsoftware.repository.UsuarioRepositorio;
import com.saberpro.backendsoftware.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UsuarioService(UsuarioRepositorio usuarioRepositorio, JwtUtil jwtUtil) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public boolean crearUsuario(Usuario usuario) {
        if (usuarioRepositorio.findByNombreUsuario(usuario.getNombreUsuario()).isPresent()) {
            return false;
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepositorio.save(usuario);
        return true;
    }

    public String login(String nombreUsuario, String password) {
        System.out.println("Buscando usuario: " + nombreUsuario);
        Optional<Usuario> userOpt = usuarioRepositorio.findByNombreUsuario(nombreUsuario);

        if (userOpt.isPresent()) {
            System.out.println("Usuario encontrado. Verificando contraseña...");
            boolean matches = passwordEncoder.matches(password, userOpt.get().getPassword());
            System.out.println("¿Contraseña válida?: " + matches);

            if (matches) {
                System.out.println("Login exitoso para usuario: " + nombreUsuario);
                return jwtUtil.generateToken(nombreUsuario);
            } else {
                System.out.println("Contraseña incorrecta para usuario: " + nombreUsuario);
            }
        } else {
            System.out.println("Usuario no encontrado: " + nombreUsuario);
        }

        return null;
    }
}
