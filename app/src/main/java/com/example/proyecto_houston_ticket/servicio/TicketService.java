package com.example.proyecto_houston_ticket.servicio;

import android.content.Context;

import com.example.proyecto_houston_ticket.data.ComentarioDAO;
import com.example.proyecto_houston_ticket.data.TicketDAO;
import com.example.proyecto_houston_ticket.data.UsuarioDAO;
import com.example.proyecto_houston_ticket.modelo.Comentario;
import com.example.proyecto_houston_ticket.modelo.EstadoTicket;
import com.example.proyecto_houston_ticket.modelo.Ticket;
import com.example.proyecto_houston_ticket.modelo.Usuario;

import java.util.List;

/**
 * Servicio encargado de gestionar las operaciones de negocio sobre los tickets y sus comentarios.
 */
public class TicketService {

    private final TicketDAO ticketDAO;
    private final ComentarioDAO comentarioDAO;
    private final UsuarioDAO usuarioDAO;

    public TicketService(Context context) {
        this.ticketDAO = new TicketDAO(context);
        this.comentarioDAO = new ComentarioDAO(context);
        this.usuarioDAO = new UsuarioDAO(context);
    }

    /**
     * Valida y crea un nuevo ticket.
     */
    public long crearTicket(String titulo, String descripcion, String categoria, String prioridad, long usuarioId, StringBuilder errorOut) {
        if (titulo == null || titulo.trim().isEmpty()) {
            errorOut.append("El título es obligatorio. ");
            return -1;
        }

        if (titulo.trim().length() > 100) {
            errorOut.append("El título no puede superar los 100 caracteres. ");
            return -1;
        }

        if (descripcion == null || descripcion.trim().isEmpty()) {
            errorOut.append("La descripción es obligatoria. ");
            return -1;
        }

        if (categoria == null || categoria.trim().isEmpty()) {
            errorOut.append("La categoría es obligatoria. ");
            return -1;
        }

        if (prioridad == null || prioridad.trim().isEmpty()) {
            errorOut.append("La prioridad es obligatoria. ");
            return -1;
        }

        Ticket nuevo = new Ticket(titulo.trim(), descripcion.trim(), categoria.trim(), prioridad.trim(), usuarioId);
        long result = ticketDAO.insertarTicket(nuevo);
        if (result == -1) {
            errorOut.append("Error en la base de datos al guardar la incidencia.");
        }
        return result;
    }

    /**
     * Edita los datos de un ticket existente.
     */
    public boolean actualizarTicket(long ticketId, String titulo, String descripcion, String categoria, String prioridad, StringBuilder errorOut) {
        if (titulo == null || titulo.trim().isEmpty()) {
            errorOut.append("El título es obligatorio. ");
            return false;
        }

        if (descripcion == null || descripcion.trim().isEmpty()) {
            errorOut.append("La descripción es obligatoria. ");
            return false;
        }

        Ticket actual = ticketDAO.obtenerTicketPorId(ticketId);
        if (actual == null) {
            errorOut.append("El ticket no existe.");
            return false;
        }

        return ticketDAO.actualizarTicket(ticketId, titulo.trim(), descripcion.trim(), categoria.trim(), prioridad.trim());
    }

    /**
     * Asigna un técnico a un ticket.
     */
    public boolean asignarTecnico(long ticketId, long tecnicoId, StringBuilder errorOut) {
        Ticket actual = ticketDAO.obtenerTicketPorId(ticketId);
        if (actual == null) {
            errorOut.append("El ticket no existe.");
            return false;
        }

        return ticketDAO.asignarTecnico(ticketId, tecnicoId);
    }

    /**
     * Avanza el estado de un ticket respetando la regla estricta:
     * ABIERTO -> EN_CURSO -> RESUELTO -> CERRADO
     */
    public boolean avanzarEstadoTicket(long ticketId, StringBuilder errorOut) {
        Ticket actual = ticketDAO.obtenerTicketPorId(ticketId);
        if (actual == null) {
            errorOut.append("El ticket no existe.");
            return false;
        }

        EstadoTicket siguiente = actual.obtenerSiguienteEstado();
        if (siguiente == null) {
            errorOut.append("El ticket ya se encuentra en estado final CERRADO.");
            return false;
        }

        return ticketDAO.actualizarEstadoTicket(ticketId, siguiente.name());
    }

    /**
     * Elimina un ticket únicamente si se encuentra en estado RESUELTO o CERRADO.
     */
    public boolean eliminarTicketResuelto(long ticketId, StringBuilder errorOut) {
        Ticket actual = ticketDAO.obtenerTicketPorId(ticketId);
        if (actual == null) {
            errorOut.append("El ticket no existe.");
            return false;
        }

        EstadoTicket estado = actual.getEstadoEnum();
        if (estado != EstadoTicket.RESUELTO && estado != EstadoTicket.CERRADO) {
            errorOut.append("Solo se pueden eliminar tickets en estado Resuelto o Cerrado.");
            return false;
        }

        return ticketDAO.eliminarTicket(ticketId);
    }

    /**
     * Agrega un nuevo comentario a un ticket.
     */
    public boolean agregarComentario(long ticketId, long usuarioId, String autorNombre, String rolAutor, String mensaje, StringBuilder errorOut) {
        if (mensaje == null || mensaje.trim().isEmpty()) {
            errorOut.append("El comentario no puede estar vacío.");
            return false;
        }

        Comentario comentario = new Comentario(ticketId, usuarioId, autorNombre, rolAutor, mensaje.trim());
        boolean ok = comentarioDAO.insertarComentario(comentario) != -1;
        if (!ok) {
            errorOut.append("Error al guardar el comentario en la base de datos.");
        }
        return ok;
    }

    /**
     * Obtiene los comentarios ordenados cronológicamente de un ticket.
     */
    public List<Comentario> obtenerComentarios(long ticketId) {
        return comentarioDAO.obtenerComentariosPorTicket(ticketId);
    }

    /**
     * Obtiene la lista de usuarios con rol TECNICO.
     */
    public List<Usuario> obtenerListaTecnicos() {
        return usuarioDAO.obtenerTecnicos();
    }

    /**
     * Obtiene los tickets de un cliente.
     */
    public List<Ticket> obtenerTicketsUsuario(long usuarioId) {
        return ticketDAO.obtenerTicketsPorUsuario(usuarioId);
    }

    /**
     * Obtiene los tickets asignados a un técnico específico.
     */
    public List<Ticket> obtenerTicketsTecnico(long tecnicoId) {
        return ticketDAO.obtenerTicketsPorTecnico(tecnicoId);
    }

    /**
     * Obtiene todos los tickets del sistema (para ADMIN).
     */
    public List<Ticket> obtenerTodosLosTickets() {
        return ticketDAO.obtenerTodosLosTickets();
    }

    /**
     * Obtiene el detalle de un ticket por su ID.
     */
    public Ticket obtenerDetalleTicket(long ticketId) {
        return ticketDAO.obtenerTicketPorId(ticketId);
    }
}
