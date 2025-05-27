package com.saberpro.backendsoftware.Utils.Scheduled;

import com.saberpro.backendsoftware.service.UsuarioService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RolScheduler {

    private final UsuarioService usuarioService;

    public RolScheduler(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Scheduled(cron = "0 0 0 * * *") // cada día a medianoche
    public void ejecutarActualizacionRoles() {
        usuarioService.actualizarRolesExpirados();
    }
}
