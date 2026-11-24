package com.example.proyecto_houston_ticket.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.proyecto_houston_ticket.modelo.Rol;
import com.example.proyecto_houston_ticket.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Usuario.
 */
public class UsuarioDAO {

    private static final String TAG = "UsuarioDAO";
    private final DatabaseHelper dbHelper;

    public UsuarioDAO(Context context) {
        if (context != null) {
            this.dbHelper = new DatabaseHelper(context);
        } else {
            this.dbHelper = null;
        }
    }

    /**
     * Inserta un nuevo usuario en la base de datos.
     */
    public long insertar(Usuario usuario) {
        if (dbHelper == null || usuario == null) return -1;

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("nombre", usuario.getNombre());
            values.put("correo", usuario.getCorreo());
            values.put("password_hash", usuario.getPasswordHash());
            values.put("rol", usuario.getRol().name());
            values.put("activo", usuario.isActivo() ? 1 : 0);

            return db.insert("usuarios", null, values);
        } catch (SQLException e) {
            Log.e(TAG, "Error al insertar usuario: " + e.getMessage(), e);
            return -1;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * Busca un usuario por correo.
     */
    public Usuario buscarPorCorreo(String correo) {
        if (dbHelper == null || correo == null) return null;

        SQLiteDatabase db = null;
        Cursor cursor = null;
        Usuario usuario = null;

        try {
            db = dbHelper.getReadableDatabase();
            String[] projection = {"id", "nombre", "correo", "password_hash", "rol", "activo"};
            String selection = "correo = ?";
            String[] selectionArgs = {correo.trim().toLowerCase()};

            cursor = db.query("usuarios", projection, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                usuario = mapearUsuario(cursor);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error al buscar usuario por correo: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return usuario;
    }

    /**
     * Busca un usuario por ID.
     */
    public Usuario buscarPorId(long id) {
        if (dbHelper == null) return null;

        SQLiteDatabase db = null;
        Cursor cursor = null;
        Usuario usuario = null;

        try {
            db = dbHelper.getReadableDatabase();
            String[] projection = {"id", "nombre", "correo", "password_hash", "rol", "activo"};
            String selection = "id = ?";
            String[] selectionArgs = {String.valueOf(id)};

            cursor = db.query("usuarios", projection, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                usuario = mapearUsuario(cursor);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error al buscar usuario por ID: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return usuario;
    }

    /**
     * Comprueba la existencia de un correo.
     */
    public boolean existeCorreo(String correo) {
        if (dbHelper == null || correo == null) return false;

        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean existe = false;

        try {
            db = dbHelper.getReadableDatabase();
            String[] projection = {"id"};
            String selection = "correo = ?";
            String[] selectionArgs = {correo.trim().toLowerCase()};

            cursor = db.query("usuarios", projection, selection, selectionArgs, null, null, null);
            existe = (cursor != null && cursor.getCount() > 0);
        } catch (SQLException e) {
            Log.e(TAG, "Error al comprobar existencia de correo: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return existe;
    }

    /**
     * Obtiene todos los usuarios que tienen rol TECNICO para asignación de tickets.
     */
    public List<Usuario> obtenerTecnicos() {
        List<Usuario> lista = new ArrayList<>();
        if (dbHelper == null) return lista;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = "rol = ? AND activo = 1";
            String[] selectionArgs = {Rol.TECNICO.name()};

            cursor = db.query("usuarios", null, selection, selectionArgs, null, null, "nombre ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    lista.add(mapearUsuario(cursor));
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error al obtener lista de técnicos: " + e.getMessage(), e);
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

    private Usuario mapearUsuario(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
        String correo = cursor.getString(cursor.getColumnIndexOrThrow("correo"));
        String passwordHash = cursor.getString(cursor.getColumnIndexOrThrow("password_hash"));
        String rolStr = cursor.getString(cursor.getColumnIndexOrThrow("rol"));
        boolean activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1;

        return new Usuario(id, nombre, correo, passwordHash, Rol.deCadena(rolStr), activo);
    }
}
