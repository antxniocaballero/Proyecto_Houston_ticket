package com.example.proyecto_houston_ticket.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.proyecto_houston_ticket.modelo.Ticket;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la gestión de tickets en la base de datos local SQLite.
 */
public class TicketDAO {

    private static final String TAG = "TicketDAO";
    private final DatabaseHelper dbHelper;

    public TicketDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Inserta un nuevo ticket en la base de datos local.
     */
    public long insertarTicket(Ticket ticket) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("titulo", ticket.getTitulo());
            values.put("descripcion", ticket.getDescripcion());
            values.put("categoria", ticket.getCategoria());
            values.put("prioridad", ticket.getPrioridad());
            values.put("estado", ticket.getEstado() != null ? ticket.getEstado() : "ABIERTO");
            values.put("adjunto", ticket.getAdjunto());
            values.put("creado_por", ticket.getCreadoPor());
            if (ticket.getTecnicoId() != null) {
                values.put("tecnico_id", ticket.getTecnicoId());
            }

            return db.insert("tickets", null, values);
        } catch (SQLException e) {
            Log.e(TAG, "Error al insertar ticket: " + e.getMessage(), e);
            return -1;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * Obtiene todos los tickets creados por un usuario específico (para clientes).
     */
    public List<Ticket> obtenerTicketsPorUsuario(long usuarioId) {
        List<Ticket> lista = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = "creado_por = ?";
            String[] selectionArgs = {String.valueOf(usuarioId)};
            String orderBy = "fecha_creacion DESC";

            cursor = db.query("tickets", null, selection, selectionArgs, null, null, orderBy);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    lista.add(mapearTicket(cursor));
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error al obtener tickets del usuario: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return lista;
    }

    /**
     * Obtiene los datos detallados de un ticket por su ID.
     */
    public Ticket obtenerTicketPorId(long ticketId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Ticket ticket = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = "id = ?";
            String[] selectionArgs = {String.valueOf(ticketId)};

            cursor = db.query("tickets", null, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                ticket = mapearTicket(cursor);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error al obtener ticket por ID: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return ticket;
    }

    /**
     * Convierte una fila del cursor a un objeto Ticket.
     */
    private Ticket mapearTicket(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
        String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
        String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
        String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));
        String prioridad = cursor.getString(cursor.getColumnIndexOrThrow("prioridad"));
        String estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"));
        String adjunto = cursor.getString(cursor.getColumnIndexOrThrow("adjunto"));
        long creadoPor = cursor.getLong(cursor.getColumnIndexOrThrow("creado_por"));

        Long tecnicoId = null;
        int colTecnico = cursor.getColumnIndexOrThrow("tecnico_id");
        if (!cursor.isNull(colTecnico)) {
            tecnicoId = cursor.getLong(colTecnico);
        }

        String fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow("fecha_creacion"));
        String fechaActualizacion = cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion"));

        return new Ticket(id, titulo, descripcion, categoria, prioridad, estado, adjunto, creadoPor, tecnicoId, fechaCreacion, fechaActualizacion);
    }
}
