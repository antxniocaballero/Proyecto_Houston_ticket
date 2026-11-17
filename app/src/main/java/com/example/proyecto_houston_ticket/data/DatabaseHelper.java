package com.example.proyecto_houston_ticket.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.proyecto_houston_ticket.util.PasswordUtil;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "houston_db.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ROL_TABLE = "CREATE TABLE rol (" +
                "id_rol INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL UNIQUE" +
                ");";

        String CREATE_USUARIO_TABLE = "CREATE TABLE usuario (" +
                "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password_hash TEXT NOT NULL, " +
                "id_rol INTEGER NOT NULL, " +
                "activo INTEGER NOT NULL DEFAULT 1, " +
                "fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (id_rol) REFERENCES rol(id_rol)" +
                ");";

        db.execSQL(CREATE_ROL_TABLE);
        db.execSQL(CREATE_USUARIO_TABLE);

        // Insert roles
        db.execSQL("INSERT INTO rol (id_rol, nombre) VALUES (1, 'CLIENTE');");
        db.execSQL("INSERT INTO rol (id_rol, nombre) VALUES (2, 'TECNICO');");
        db.execSQL("INSERT INTO rol (id_rol, nombre) VALUES (3, 'ADMIN');");

        // Insert default users
        String adminHash = PasswordUtil.hashPassword("Admin1234");
        String tecnicoHash = PasswordUtil.hashPassword("Tecnico1234");
        String clienteHash = PasswordUtil.hashPassword("Cliente1234");

        db.execSQL("INSERT INTO usuario (nombre, email, password_hash, id_rol, activo) VALUES ('Administrador', 'admin@houston.local', '" + adminHash + "', 3, 1);");
        db.execSQL("INSERT INTO usuario (nombre, email, password_hash, id_rol, activo) VALUES ('Técnico Soporte', 'tecnico@houston.local', '" + tecnicoHash + "', 2, 1);");
        db.execSQL("INSERT INTO usuario (nombre, email, password_hash, id_rol, activo) VALUES ('Cliente Demo', 'cliente@houston.local', '" + clienteHash + "', 1, 1);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuario;");
        db.execSQL("DROP TABLE IF EXISTS rol;");
        onCreate(db);
    }
}
