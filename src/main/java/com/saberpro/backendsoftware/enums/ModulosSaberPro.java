package com.saberpro.backendsoftware.enums;

import java.util.List;

public enum ModulosSaberPro {
        COMUNICACION_ESCRITA,
        LECTURA_CRITICA,
        FORMULACION_DE_PROYECTOS_DE_INGENIERIA,
        COMPETENCIAS_CIUDADANAS,
        INGLÉS,
        DISEÑO_DE_SOFTWARE,
        RAZONAMIENTO_CUANTITATIVO,
        PENSAMIENTO_CIENTIFICO_MATEMATICAS_Y_ESTADISTICA;

    @Override
    public String toString() {
        return name().replace('_', ' ');
    }
}
