package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.enums.AccionHistorico;
import com.saberpro.backendsoftware.model.History;
import com.saberpro.backendsoftware.model.Usuario;
import com.saberpro.backendsoftware.repository.HistoryRepository;
import com.saberpro.backendsoftware.repository.UsuarioRepository;
import com.saberpro.backendsoftware.security.JwtUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final UsuarioRepository usuarioRepositorio;

    public void registrarAccion(String token, AccionHistorico accion, String detalles) {
        try {
            Map<String, Object> json = JwtUtil.getInstance().decodeToken(token);

            String nombre = (String) json.get("sub");

            Optional<Usuario> usuario = usuarioRepositorio.findByNombreUsuario(nombre);
            if (usuario.isPresent()) {
                subirAccion(usuario.get(), accion, detalles);
            } else {
                System.err.println("Usuario no encontrado: " + nombre);
            }

        } catch (Exception e) {
            System.err.println("Error al procesar token JWT: " + e.getMessage());
        }
    }

    private void subirAccion(Usuario usuario, AccionHistorico accion, String detalles) {
        History history = new History(
                usuario,
                usuario.getTipoDeUsuario().toString(),
                LocalDate.now(),
                accion,
                detalles
        );
        historyRepository.save(history);
    }
}
