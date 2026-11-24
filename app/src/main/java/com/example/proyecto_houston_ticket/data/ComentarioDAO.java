package com.example.proyecto_houston_ticket.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.proyecto_houston_ticket.modelo.Comentario;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la gestión de comentarios en la base de datos.
 */
public class ComentarioDAO {

    private static final String TAG = "ComentarioDAO";
    private final DatabaseHelper dbHelper;

    public ComentarioDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Inserta un nuevo comentario asociado a un ticket.
     */
    public long insertarComentario(Comentario comentario) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("ticket_id", comentario.getTicketId());
            values.put("usuario_id", comentario.getUsuarioId());
            values.put("autor_nombre", comentario.getAutorNombre());
            values.put("rol_autor", comentario.getRolAutor());
            values.put("mensaje", comentario.getMensaje());

            return db.insert("comentarios", null, values);
        } catch (SQLException e) {
            Log.e(TAG, "Error al insertar comentario: " + e.getMessage(), e);
            return -1;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * Obtiene todos los comentarios pertenecientes a un ticket ordenados por fecha.
     */
    public List<Comentario> obtenerComentariosPorTicket(long ticketId) {
        List<Comentario> lista = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = "ticket_id = ?";
            String[] selectionArgs = {String.valueOf(ticketId)};
            String orderBy = "fecha_creacion ASC";

            cursor = db.query("comentarios", null, selection, selectionArgs, null, null, orderBy);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                    long tId = cursor.getLong(cursor.getColumnIndexOrThrow("ticket_id"));
                    long uId = cursor.getLong(cursor.getColumnIndexOrThrow("usuario_id"));
                    String autorNombre = cursor.getString(cursor.getColumnIndexOrThrow("autor_nombre"));
                    String rolAutor = cursor.getString(cursor.getColumnIndexOrThrow("rol_autor"));
                    String mensaje = cursor.getString(cursor.getColumnIndexOrThrow("mensaje"));
                    String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_creacion"));

                    lista.add(new Comentario(id, tId, uId, autorNombre, rolAutor, mensaje, fecha));
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error al obtener comentarios: " + e.getMessage(), e);
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
}
