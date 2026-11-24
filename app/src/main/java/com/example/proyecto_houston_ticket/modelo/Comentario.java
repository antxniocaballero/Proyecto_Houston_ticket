package com.example.proyecto_houston_ticket.modelo;

import java.io.Serializable;

/**
 * Modelo de datos que representa un Comentario o nota de seguimiento en un ticket.
 */
public class Comentario implements Serializable {

    private long id;
    private long ticketId;
    private long usuarioId;
    private String autorNombre;
    private String rolAutor;
    private String mensaje;
    private String fechaCreacion;

    public Comentario() {
    }

    public Comentario(long ticketId, long usuarioId, String autorNombre, String rolAutor, String mensaje) {
        this.ticketId = ticketId;
        this.usuarioId = usuarioId;
        this.autorNombre = autorNombre;
        this.rolAutor = rolAutor;
        this.mensaje = mensaje;
    }

    public Comentario(long id, long ticketId, long usuarioId, String autorNombre, String rolAutor, String mensaje, String fechaCreacion) {
        this.id = id;
        this.ticketId = ticketId;
        this.usuarioId = usuarioId;
        this.autorNombre = autorNombre;
        this.rolAutor = rolAutor;
        this.mensaje = mensaje;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getTicketId() { return ticketId; }
    public void setTicketId(long ticketId) { this.ticketId = ticketId; }

    public long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(long usuarioId) { this.usuarioId = usuarioId; }

    public String getAutorNombre() { return autorNombre; }
    public void setAutorNombre(String autorNombre) { this.autorNombre = autorNombre; }

    public String getRolAutor() { return rolAutor; }
    public void setRolAutor(String rolAutor) { this.rolAutor = rolAutor; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
