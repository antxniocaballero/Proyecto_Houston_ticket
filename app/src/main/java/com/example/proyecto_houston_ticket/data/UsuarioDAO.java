package com.example.proyecto_houston_ticket.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.proyecto_houston_ticket.modelo.Rol;
import com.example.proyecto_houston_ticket.modelo.Usuario;

public class UsuarioDAO {
    private final DatabaseHelper dbHelper;

    public UsuarioDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public Usuario buscarPorEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"id_usuario", "nombre", "email", "password_hash", "id_rol", "activo", "fecha_registro"};
        String selection = "email = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query("usuario", columns, selection, selectionArgs, null, null, null);
        Usuario usuario = null;

        if (cursor != null && cursor.moveToFirst()) {
            usuario = new Usuario();
            usuario.setIdUsuario(cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")));
            usuario.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
            usuario.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            usuario.setPasswordHash(cursor.getString(cursor.getColumnIndexOrThrow("password_hash")));
            usuario.setRol(Rol.fromId(cursor.getInt(cursor.getColumnIndexOrThrow("id_rol"))));
            usuario.setActivo(cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1);
            usuario.setFechaRegistro(cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro")));
            cursor.close();
        }
        db.close();
        return usuario;
    }

    public boolean registrar(Usuario usuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", usuario.getNombre());
        values.put("email", usuario.getEmail());
        values.put("password_hash", usuario.getPasswordHash());
        values.put("id_rol", usuario.getRol().getId());
        values.put("activo", usuario.isActivo() ? 1 : 0);

        long result = db.insert("usuario", null, values);
        db.close();
        return result != -1;
    }
}
