package com.example.proyecto_houston_ticket.modelo;

/**
 * Modelo de datos que representa a un usuario dentro del sistema.
 * Mapea directamente la estructura de la tabla 'usuarios' en la base de datos SQLite.
 */
public class Usuario {
    
    // Identificador único del usuario (autoincremental en BD)
    private long id;
    
    // Nombre completo del usuario
    private String nombre;
    
    // Correo electrónico (único en el sistema)
    private String correo;
    
    // Hash de la contraseña cifrada (nunca se almacena texto plano)
    private String passwordHash;
    
    // Rol asignado al usuario (CLIENTE, TECNICO, ADMIN)
    private Rol rol;
    
    // Estado de la cuenta (true = activo, false = inactivo/bloqueado)
    private boolean activo;

    /**
     * Constructor por defecto (vacío).
     */
    public Usuario() {
    }

    /**
     * Constructor completo con todos los campos.
     *
     * @param id Identificador en la BD.
     * @param nombre Nombre del usuario.
     * @param correo Correo electrónico.
     * @param passwordHash Hash de la contraseña.
     * @param rol Rol del usuario.
     * @param activo Estado activo/inactivo.
     */
    public Usuario(long id, String nombre, String correo, String passwordHash, Rol rol, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = activo;
    }

    /**
     * Constructor secundario sin ID (útil para nuevos registros antes de insertar en la BD).
     */
    public Usuario(String nombre, String correo, String passwordHash, Rol rol, boolean activo) {
        this.nombre = nombre;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = activo;
    }

    // --- Getters y Setters ---

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
