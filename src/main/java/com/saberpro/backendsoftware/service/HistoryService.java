package com.saberpro.backendsoftware.service;

import com.saberpro.backendsoftware.Utils._HistoricActions;
import com.saberpro.backendsoftware.model.History;
import com.saberpro.backendsoftware.model.Usuario;
import com.saberpro.backendsoftware.repository.HistoryRepositorio;
import com.saberpro.backendsoftware.repository.UsuarioRepositorio;
import com.saberpro.backendsoftware.security.util.JwtUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@RequiredArgsConstructor
public class HistoryService {

    private static HistoryService instance;
    private HistoryRepositorio historyRepositorio;
    private UsuarioRepositorio usuarioRepositorio;


    public void registrarAccion(String token, _HistoricActions accion, String detalles) {
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

    private void subirAccion(Usuario usuario, _HistoricActions accion, String detalles) {
        History history = new History(
                usuario,
                usuario.getTipoDeUsuario(),
                LocalDate.now(),
                accion,
                detalles
        );
        historyRepositorio.save(history);
    }
}
