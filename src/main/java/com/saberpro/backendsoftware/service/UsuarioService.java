package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.dto.UsuarioDTO;
import com.saberpro.backendsoftware.enums.TipoUsuario;
import com.saberpro.backendsoftware.model.Usuario;
import com.saberpro.backendsoftware.repository.UsuarioRepository;
import com.saberpro.backendsoftware.security.JwtUtil;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service

public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final Map<String, String> codigosRecuperacion = new HashMap<>();

    public UsuarioService(UsuarioRepository usuarioRepository, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    @Transactional
    public boolean crearUsuario(Usuario usuario) {
        if (usuarioRepository.findByNombreUsuario(usuario.getNombreUsuario()).isPresent()) {
            return false;
        }

        String plainPassword = generateRandomPassword();
        usuario.setPassword(passwordEncoder.encode(plainPassword));

        // Guardar en el archivo
        savePasswordToFile(usuario.getNombreUsuario(), plainPassword);

        usuarioRepository.save(usuario);
        return true;
    }
    public void guardarCodigoRecuperacion(String nombreUsuario, String codigo) {
        codigosRecuperacion.put(nombreUsuario, codigo);
    }

    public boolean verificarCodigo(String nombreUsuario, String codigoIngresado) {
        if (codigoIngresado.equals(codigosRecuperacion.get(nombreUsuario))){
            codigosRecuperacion.remove(nombreUsuario);
            return true;
        }
        return false;
    }
    @Transactional
    public boolean eliminarUsuario(String nombreUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);
        if (usuarioOpt.isPresent()) {
            usuarioRepository.delete(usuarioOpt.get());
            return true;
        }
        return false;
    }

    @Transactional
    public String login(String nombreUsuario, String password) {
        Optional<Usuario> userOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);
        if (userOpt.isPresent()) {
            Usuario usuario = userOpt.get();
            if (passwordEncoder.matches(password, usuario.getPassword())) {
                return jwtUtil.generateToken(nombreUsuario, usuario.getTipoDeUsuario().toString());
            }
        }
        return null;
    }
    @Transactional
    public boolean cambiarRolUsuario(String nombreUsuario, TipoUsuario nuevoRol) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .map(usuario -> {
                    usuario.setTipoDeUsuario(nuevoRol);
                    usuarioRepository.save(usuario);
                    return true;
                })
                .orElse(false);
    }
    @Transactional
    public List<UsuarioDTO> buscarUsuariosExcluyendoTipo(TipoUsuario tipoExcluido) {
        return usuarioRepository.findByTipoDeUsuarioNot(tipoExcluido)
                .stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public List<UsuarioDTO> buscarPorTipoUsuario(TipoUsuario tipoUsuario) {
        return usuarioRepository.findByTipoDeUsuario(tipoUsuario)
                .stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public Optional<UsuarioDTO> buscarPorNombreUsuario(String nombre){
        return usuarioRepository.findByNombreUsuario(nombre)
                .map(this::convertirAUsuarioDTO);
    }

    @Transactional
    public List<UsuarioDTO> findAllUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }
    private UsuarioDTO convertirAUsuarioDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getNombreUsuario(),
                usuario.getTipoDeUsuario().toString(),
                usuario.getCorreo()
        );
    }


    private void savePasswordToFile(String nombreUsuario, String plainPassword) {
        try {
            String line = nombreUsuario + " : " + plainPassword + System.lineSeparator();
            Files.write(Paths.get("passwords.txt"), line.getBytes(), Files.exists(Paths.get("passwords.txt"))
                    ? StandardOpenOption.APPEND
                    : StandardOpenOption.CREATE);
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
    @Transactional
    public boolean cambiarContrasena(String nombreUsuario, String nuevaContrasena) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String hashedPassword = passwordEncoder.encode(nuevaContrasena);
            usuario.setPassword(hashedPassword);
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }

    @Transactional
    public void actualizarRolesExpirados() {
        List<Usuario> expirados = usuarioRepository.findByFechaFinRolBefore(LocalDate.now());
        for (Usuario usuario : expirados) {
            usuario.setTipoDeUsuario(TipoUsuario.DOCENTE);
            usuario.setFechaFinRol(null); // opcional: limpiar para no volver a procesar
        }
        usuarioRepository.saveAll(expirados);
    }
}