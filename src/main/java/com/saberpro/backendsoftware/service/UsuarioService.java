package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.Dtos.UsuarioDTO;
import com.saberpro.backendsoftware.model.Usuario;
import com.saberpro.backendsoftware.repository.UsuarioRepositorio;
import com.saberpro.backendsoftware.security.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class UsuarioService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UsuarioService(UsuarioRepositorio usuarioRepositorio, JwtUtil jwtUtil) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.jwtUtil = jwtUtil;
        // Fuerza 12
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    @Transactional
    public boolean crearUsuario(Usuario usuario) {
        if (usuarioRepositorio.findByNombreUsuario(usuario.getNombreUsuario()).isPresent()) {
            return false;
        }

        String plainPassword = generateRandomPassword();
        String hashedPassword = passwordEncoder.encode(plainPassword);
        usuario.setPassword(hashedPassword);

        // Guardar en el archivo
        savePasswordToFile(usuario.getNombreUsuario(), plainPassword);

        usuarioRepositorio.save(usuario);
        return true;
    }

    @Transactional
    public boolean eliminarUsuario(String nombreUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByNombreUsuario(nombreUsuario);
        if (usuarioOpt.isPresent()) {
            usuarioRepositorio.delete(usuarioOpt.get());
            return true;
        }
        return false;
    }

    @Transactional
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
    @Transactional
    public boolean cambiarRolUsuario(String nombreUsuario, String nuevoRol) {
        return usuarioRepositorio.findByNombreUsuario(nombreUsuario)
                .map(usuario -> {
                    usuario.setTipoDeUsuario(nuevoRol);
                    usuarioRepositorio.save(usuario);
                    return true;
                })
                .orElse(false);
    }
    @Transactional
    public List<UsuarioDTO> buscarUsuariosExcluyendoTipo(String tipoExcluido) {
        return usuarioRepositorio.findByTipoDeUsuarioNot(tipoExcluido)
                .stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public List<UsuarioDTO> buscarPorTipoUsuario(String tipoUsuario) {
        return usuarioRepositorio.findByTipoDeUsuario(tipoUsuario)
                .stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public List<UsuarioDTO> findAllUsuarios() {
        return usuarioRepositorio.findAll()
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

    private void savePasswordToFile(String nombreUsuario, String plainPassword) {
        try {
            String line = nombreUsuario + " : " + plainPassword + System.lineSeparator();
            Files.write(Paths.get("passwords.txt"), line.getBytes(), Files.exists(Paths.get("passwords.txt"))
                    ? java.nio.file.StandardOpenOption.APPEND
                    : java.nio.file.StandardOpenOption.CREATE);
        } catch (IOException e) {
            // Puedes registrar esto si usas algún logger
            System.err.println("Error guardando contraseña en archivo: " + e.getMessage());
        }
    }

    //PASAR A USER SERVICE
    private String generateRandomPassword() {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789*#@!$%&/=?¿¡+_-.,:;{}[]()ñÑ";
        final int PASSWORD_LENGTH = 15;
        final SecureRandom random = new SecureRandom();
        return IntStream.range(0, PASSWORD_LENGTH)
                .mapToObj(i -> String.valueOf(CHARACTERS.charAt(random.nextInt(CHARACTERS.length()))))
                .collect(Collectors.joining());
    }
}