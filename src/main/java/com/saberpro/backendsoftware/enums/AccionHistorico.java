package com.saberpro.backendsoftware.enums;

public enum AccionHistorico {
    Crear_usuario,
    Cambiar_rol_usuario,
    Eliminar_usuario,
    Add_reporte_Saber_pro,
    Sobreescribir_reporte_Saber_pro,
    Add_reporte_acciones_de_mejora;

    @Override
    public String toString() {
        return name().replace('_', ' ');
    }
}
