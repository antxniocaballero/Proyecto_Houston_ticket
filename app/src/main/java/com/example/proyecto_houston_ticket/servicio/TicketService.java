package com.example.proyecto_houston_ticket.servicio;

import android.content.Context;

import com.example.proyecto_houston_ticket.data.TicketDAO;
import com.example.proyecto_houston_ticket.modelo.Ticket;

import java.util.List;

/**
 * Servicio encargado de gestionar las operaciones de negocio sobre los tickets.
 */
public class TicketService {

    private final TicketDAO ticketDAO;

    public TicketService(Context context) {
        this.ticketDAO = new TicketDAO(context);
    }

    /**
     * Valida y crea un nuevo ticket.
     *
     * @param titulo Título del ticket (obligatorio).
     * @param descripcion Descripción detallada (obligatoria).
     * @param categoria Categoría seleccionada (obligatoria).
     * @param prioridad Prioridad seleccionada (obligatoria).
     * @param usuarioId ID del usuario creador (obligatorio).
     * @param errorOut StringBuilder para mensajes de error.
     * @return El ID del ticket creado o -1 si falla.
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
        return ticketDAO.insertarTicket(nuevo);
    }

    /**
     * Obtiene la lista de tickets del usuario especificado.
     */
    public List<Ticket> obtenerTicketsUsuario(long usuarioId) {
        return ticketDAO.obtenerTicketsPorUsuario(usuarioId);
    }

    /**
     * Obtiene el detalle de un ticket por su ID.
     */
    public Ticket obtenerDetalleTicket(long ticketId) {
        return ticketDAO.obtenerTicketPorId(ticketId);
    }
}
