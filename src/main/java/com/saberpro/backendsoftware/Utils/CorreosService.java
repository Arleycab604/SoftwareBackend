package com.saberpro.backendsoftware.Utils;

import com.saberpro.backendsoftware.enums.TipoUsuario;
import com.saberpro.backendsoftware.model.PropuestaMejora;
import com.saberpro.backendsoftware.model.Usuario;
import com.saberpro.backendsoftware.model.usuarios.Docente;
import com.saberpro.backendsoftware.repository.DocenteRepository;
import com.saberpro.backendsoftware.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.scheduling.annotation.Async;
@Service
@RequiredArgsConstructor
public class CorreosService {
    private final UsuarioRepository usuarioRepository;
    private final DocenteRepository docenteRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Async
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

        // Usamos el método general
        enviarCorreo(asunto, cuerpo, List.of(usuario.getCorreo()));

        return codigo;
    }
    @Async
    public void notificarInicioSesion(String nombreUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);
        if (usuarioOpt.isEmpty()) return;

        Usuario usuario = usuarioOpt.get();
        String correoDestino = usuario.getCorreo();

        String asunto = "🔐 Inicio de sesión detectado - SaberPro";
        String cuerpo = String.format("""
        Hola %s,

        Se ha detectado un inicio de sesión en tu cuenta en SaberPro.

        Fecha y hora: %s

        Si no fuiste tú, cambia tu contraseña inmediatamente o contacta al soporte.

        Atentamente,
        Equipo SaberPro
        """, usuario.getNombreUsuario(), java.time.LocalDateTime.now());

        enviarCorreo(asunto, cuerpo, List.of(correoDestino));
    }
    @Async
    public void notificarAsignacionAccionMejora(PropuestaMejora propuesta) {
        List<String> correosDocentes = docenteRepository.findByModuloMaterias(propuesta.getModuloPropuesta())
                .stream()
                .map(Docente::getUsuario)
                .map(Usuario::getCorreo)
                .toList();

        String asunto = "📌 Nueva Acción de Mejora Asignada - SaberPro";
        String cuerpo = String.format("""
        Estimado docente,

        Se ha asignado una nueva acción de mejora relacionada con la propuesta: "%s".

        Descripción:
        %s

        Por favor, ingrese al sistema SaberPro para revisar y dar seguimiento a la acción asignada.

        Saludos,
        SaberPro
        """, propuesta.getNombrePropuesta(), propuesta.getDescripcion());

        enviarCorreo(asunto, cuerpo, correosDocentes);
    }
    @Async
    public void notificarCreacionPropuesta(String nombrePropuesta) {
        List<String> correosComite = usuarioRepository.findByTipoDeUsuario(TipoUsuario.COMITE_DE_PROGRAMA)
                .stream()
                .map(Usuario::getCorreo)
                .toList();

        String asunto = "📄 Nueva Propuesta de Mejora Creada - SaberPro";
        String cuerpo = String.format("""
        Estimado comité,

        Se ha creado una nueva propuesta de mejora: "%s".

        Por favor, ingrese al sistema SaberPro para revisarla y posteriormente confirmarla o rechazarla.

        Saludos,
        SaberPro
        """, nombrePropuesta);

        enviarCorreo(asunto, cuerpo, correosComite);
    }
    @Async
    public void notificarModificacionPropuesta(String nombrePropuesta) {
        List<String> correosComite = usuarioRepository.findByTipoDeUsuario(TipoUsuario.COMITE_DE_PROGRAMA)
                .stream()
                .map(Usuario::getCorreo)
                .toList();

        String asunto = "✏️ Propuesta de Mejora Modificada";
        String cuerpo = String.format("""
        Estimado comité,

        La propuesta de mejora titulada: "%s" ha sido modificada.

        Por favor, revise los cambios en el sistema SaberPro para emitir su veredicto actualizado.

        Saludos,
        SaberPro
        """, nombrePropuesta);

        enviarCorreo(asunto, cuerpo, correosComite);
    }

    @Async
    public void enviarCorreo(String asunto, String cuerpo, List<String> destinatarios) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

            helper.setTo(destinatarios.toArray(new String[0]));
            helper.setSubject(asunto);
            helper.setText(cuerpo, false);

            mailSender.send(mensaje);
        } catch (Exception e) {
            System.out.println("Error al enviar correo: " + e.getMessage());
        }
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
