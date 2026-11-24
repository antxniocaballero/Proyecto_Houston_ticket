package com.example.proyecto_houston_ticket.modelo;

import java.io.Serializable;

/**
 * Modelo de datos que representa un Ticket de incidencia dentro del sistema.
 * Mapea los campos de la base de datos MySQL / SQLite y las respuestas JSON de la API.
 */
public class Ticket implements Serializable {

    private long id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String prioridad;  // Baja, Media, Alta
    private String estado;     // ABIERTO, EN_PROCESO, RESUELTO, CERRADO
    private String adjunto;    // Opcional
    private long creadoPor;    // ID del usuario cliente que crea el ticket
    private Long tecnicoId;    // ID del técnico asignado (puede ser null)
    private String fechaCreacion;
    private String fechaActualizacion;

    /**
     * Constructor por defecto.
     */
    public Ticket() {
    }

    /**
     * Constructor para la creación de un nuevo ticket desde el cliente.
     */
    public Ticket(String titulo, String descripcion, String categoria, String prioridad, long creadoPor) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.prioridad = prioridad;
        this.estado = "ABIERTO";
        this.creadoPor = creadoPor;
    }

    /**
     * Constructor completo.
     */
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

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getAdjunto() {
        return adjunto;
    }

    public void setAdjunto(String adjunto) {
        this.adjunto = adjunto;
    }

    public long getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(long creadoPor) {
        this.creadoPor = creadoPor;
    }

    public Long getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(Long tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
