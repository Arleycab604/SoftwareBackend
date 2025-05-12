package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.Dtos.UsuarioDTO;
import com.saberpro.backendsoftware.model.Usuario;
import com.saberpro.backendsoftware.repository.UsuarioRepositorio;
import com.saberpro.backendsoftware.security.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private final UsuarioRepositorio usuarioRepositorio;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final Map<String, String> codigosRecuperacion = new HashMap<>();

    public UsuarioService(UsuarioRepositorio usuarioRepositorio, JwtUtil jwtUtil) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    @Transactional
    public boolean crearUsuario(Usuario usuario) {
        if (usuarioRepositorio.findByNombreUsuario(usuario.getNombreUsuario()).isPresent()) {
            return false;
        }

        String plainPassword = generateRandomPassword();
        usuario.setPassword(passwordEncoder.encode(plainPassword));

        // Guardar en el archivo
        savePasswordToFile(usuario.getNombreUsuario(), plainPassword);

        usuarioRepositorio.save(usuario);
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
    public Optional<UsuarioDTO> buscarPorNombreUsuario(String nombre){
        return usuarioRepositorio.findByNombreUsuario(nombre)
                .map(this::convertirAUsuarioDTO);
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
    public String enviarCorreoRecuperacion(String nombreUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByNombreUsuario(nombreUsuario);
        if (usuarioOpt.isEmpty()) return "";

        Usuario usuario = usuarioOpt.get();
        String codigo = generarCodigoRecuperacion();

        String asunto = "🔐 Recuperación de Contraseña - SaberPro";
        String cuerpo = String.format("""
            Estimado/a %s,

            Has solicitado recuperar tu contraseña. Usa el siguiente código para continuar con el proceso:

            👉 Código de recuperación: %s

            Si no solicitaste esta recuperación, puedes ignorar este mensaje.

            Atentamente,
            El equipo de SaberPro.
            """, usuario.getNombreUsuario(), codigo);

        // Simulación de envío de correo
        System.out.printf("Enviando correo a: %s%nAsunto: %s%nCuerpo:\n%s%n", usuario.getCorreo(), asunto, cuerpo);

        // Aquí podrías guardar el código temporal en la base de datos o un cache para verificación posterior
        return codigo;
    }
    private String generarCodigoRecuperacion() {
        final String DIGITS = "0123456789";
        final int LENGTH = 6;
        SecureRandom random = new SecureRandom();
        return IntStream.range(0, LENGTH)
                .mapToObj(i -> String.valueOf(DIGITS.charAt(random.nextInt(DIGITS.length()))))
                .collect(Collectors.joining());
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
    @Transactional
    public boolean cambiarContrasena(String nombreUsuario, String nuevaContrasena) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByNombreUsuario(nombreUsuario);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String hashedPassword = passwordEncoder.encode(nuevaContrasena);
            usuario.setPassword(hashedPassword);
            usuarioRepositorio.save(usuario);
            return true;
        }
        return false;
    }

    @Transactional
    public void actualizarRolesExpirados() {
        List<Usuario> expirados = usuarioRepositorio.findByFechaFinRolBefore(LocalDate.now());
        for (Usuario usuario : expirados) {
            usuario.setTipoDeUsuario("DOCENTE");
            usuario.setFechaFinRol(null); // opcional: limpiar para no volver a procesar
        }
        usuarioRepositorio.saveAll(expirados);
    }
}