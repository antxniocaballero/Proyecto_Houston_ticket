package com.example.proyecto_houston_ticket.modelo;

/**
 * Enumeración que representa los tres roles posibles dentro del sistema "Houston, tenemos un ticket".
 * 
 * Roles disponibles:
 * - CLIENTE: Usuario normal que registra y consulta sus propios tickets.
 * - TECNICO: Usuario técnico asignado a la resolución y gestión de tickets.
 * - ADMIN: Administrador del sistema con control total sobre usuarios y configuración.
 */
public enum Rol {
    CLIENTE,
    TECNICO,
    ADMIN;

    /**
     * Convierte una cadena de texto al tipo Enum correspondiente de forma segura.
     *
     * @param rolStr Nombre del rol en formato String (ej: "CLIENTE", "TECNICO", "ADMIN").
     * @return El Enum Rol correspondiente, o CLIENTE por defecto si es nulo o inválido.
     */
    public static Rol deCadena(String rolStr) {
        if (rolStr == null || rolStr.trim().isEmpty()) {
            return CLIENTE;
        }
        try {
            return Rol.valueOf(rolStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return CLIENTE;
        }
    }
}
