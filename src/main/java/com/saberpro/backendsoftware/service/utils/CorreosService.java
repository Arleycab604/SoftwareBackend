package com.saberpro.backendsoftware.service.utils;

import com.saberpro.backendsoftware.model.Usuario;
import com.saberpro.backendsoftware.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CorreosService {
    private final UsuarioRepository usuarioRepository;
    @Autowired
    private JavaMailSender mailSender;

    public String enviarCorreoRecuperacion(String nombreUsuario) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);
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

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);
            helper.setTo(usuario.getCorreo());
            helper.setSubject(asunto);
            helper.setText(cuerpo);

            mailSender.send(mensaje);
        } catch (Exception e) {
            System.out.println("Error al enviar el correo: " + e.getMessage());
            return "";
        }

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
}
