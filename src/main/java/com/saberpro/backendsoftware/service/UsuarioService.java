package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.Dtos.UsuarioDTO;
import com.saberpro.backendsoftware.model.Usuario;
import com.saberpro.backendsoftware.repository.UsuarioRepositorio;
import com.saberpro.backendsoftware.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<Usuario> userOpt = usuarioRepositorio.findByNombreUsuario(nombreUsuario);

        if (userOpt.isPresent()) {
            Usuario usuario = userOpt.get();
            if (passwordEncoder.matches(password, usuario.getPassword())) {
                return jwtUtil.generateToken(nombreUsuario, usuario.getTipoDeUsuario());
            }
        }
        return null;
    }
    public boolean cambiarRolUsuario(String nombreUsuario, String nuevoRol) {
        return usuarioRepositorio.findByNombreUsuario(nombreUsuario)
                .map(usuario -> {
                    usuario.setTipoDeUsuario(nuevoRol);
                    usuarioRepositorio.save(usuario);
                    return true;
                })
                .orElse(false);
    }

    public List<UsuarioDTO> buscarUsuariosExcluyendoTipo(String tipoExcluido) {
        return usuarioRepositorio.findByTipoDeUsuarioNot(tipoExcluido)
                .stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> buscarPorTipoUsuario(String tipoUsuario) {
        return usuarioRepositorio.findByTipoDeUsuario(tipoUsuario)
                .stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }

    private UsuarioDTO convertirAUsuarioDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getNombreUsuario(),
                usuario.getTipoDeUsuario(),
                usuario.getCorreo()
        );
    }
}