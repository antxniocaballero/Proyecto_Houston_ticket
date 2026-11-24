package com.example.proyecto_houston_ticket.modelo;

import java.io.Serializable;

/**
 * Modelo de datos que representa un Ticket de incidencia dentro del sistema.
 */
public class Ticket implements Serializable {

    private long id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String prioridad;  // Baja, Media, Alta
    private String estado;     // Abierto, En curso, Resuelto, Cerrado
    private String adjunto;    // Opcional
    private long creadoPor;    // ID del usuario cliente que crea el ticket
    private Long tecnicoId;    // ID del técnico asignado
    private String fechaCreacion;
    private String fechaActualizacion;

    public Ticket() {
    }

    public Ticket(String titulo, String descripcion, String categoria, String prioridad, long creadoPor) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.prioridad = prioridad;
        this.estado = EstadoTicket.ABIERTO.name();
        this.creadoPor = creadoPor;
    }

    public Ticket(long id, String titulo, String descripcion, String categoria, String prioridad,
                  String estado, String adjunto, long creadoPor, Long tecnicoId,
                  String fechaCreacion, String fechaActualizacion) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.prioridad = prioridad;
        this.estado = estado;
        this.adjunto = adjunto;
        this.creadoPor = creadoPor;
        this.tecnicoId = tecnicoId;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    /**
     * Obtiene el enum de estado correspondiente.
     */
    public EstadoTicket getEstadoEnum() {
        return EstadoTicket.deCadena(this.estado);
    }

    /**
     * Obtiene el siguiente estado permitido en la máquina de estados.
     */
    public EstadoTicket obtenerSiguienteEstado() {
        return getEstadoEnum().obtenerSiguienteEstado();
    }

    /**
     * Indica si el ticket puede avanzar hacia el siguiente estado.
     */
    public boolean puedeAvanzarEstado() {
        return obtenerSiguienteEstado() != null;
    }

    /**
     * Indica si un cliente puede editar los datos del ticket (sólo en estado ABIERTO).
     */
    public boolean esEditablePorCliente() {
        return getEstadoEnum() == EstadoTicket.ABIERTO;
    }

    // Getters y Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getAdjunto() { return adjunto; }
    public void setAdjunto(String adjunto) { this.adjunto = adjunto; }

    public long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(long creadoPor) { this.creadoPor = creadoPor; }

    public Long getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Long tecnicoId) { this.tecnicoId = tecnicoId; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
