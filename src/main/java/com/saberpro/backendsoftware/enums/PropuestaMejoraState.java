package com.saberpro.backendsoftware.enums;

public enum PropuestaMejoraState {
    RECHAZADA,
    PENDIENTE,
    REQUIERE_CAMBIOS,
    ACEPTADA;

    @Override
    public String toString() {
        return name().replace('_', ' ');
    }
}
