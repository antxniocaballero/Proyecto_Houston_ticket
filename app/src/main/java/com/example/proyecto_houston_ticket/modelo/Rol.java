package com.example.proyecto_houston_ticket.modelo;

public enum Rol {
    CLIENTE(1),
    TECNICO(2),
    ADMIN(3);

    private final int id;

    Rol(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Rol fromId(int id) {
        for (Rol r : values()) {
            if (r.id == id) {
                return r;
            }
        }
        throw new IllegalArgumentException("ID de rol desconocido: " + id);
    }

    public static Rol fromNombre(String nombre) {
        if (nombre == null) return null;
        try {
            return Rol.valueOf(nombre.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
