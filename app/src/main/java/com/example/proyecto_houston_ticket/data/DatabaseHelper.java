package com.example.proyecto_houston_ticket.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.proyecto_houston_ticket.util.PasswordUtil;

/**
 * Administrador de la base de datos SQLite para la aplicación "Houston, tenemos un ticket".
 * Maneja la creación de tablas, claves foráneas e inserción de datos iniciales de prueba.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "houston_ticket.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Activa el soporte de claves foráneas en SQLite (PRAGMA foreign_keys = ON).
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Crea la estructura de tablas e inserta los datos semilla iniciales.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tabla de Usuarios
        String SQL_CREATE_TABLA_USUARIOS = "CREATE TABLE usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "correo TEXT NOT NULL UNIQUE, " +
                "password_hash TEXT NOT NULL, " +
                "rol TEXT NOT NULL, " +
                "activo INTEGER NOT NULL DEFAULT 1" +
                ");";

        // 2. Tabla de Tickets
        String SQL_CREATE_TABLA_TICKETS = "CREATE TABLE tickets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo VARCHAR(100) NOT NULL, " +
                "descripcion TEXT NOT NULL, " +
                "categoria TEXT NOT NULL, " +
                "prioridad TEXT NOT NULL, " +
                "estado TEXT NOT NULL DEFAULT 'ABIERTO', " +
                "adjunto TEXT, " +
                "creado_por INTEGER NOT NULL, " +
                "tecnico_id INTEGER, " +
                "fecha_creacion TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "fecha_actualizacion TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (creado_por) REFERENCES usuarios(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (tecnico_id) REFERENCES usuarios(id) ON DELETE SET NULL" +
                ");";

        db.execSQL(SQL_CREATE_TABLA_USUARIOS);
        db.execSQL(SQL_CREATE_TABLA_TICKETS);

        // 3. Inserción de datos iniciales de prueba (Usuarios con hashes SHA-256 con sal)
        String passClienteHash = PasswordUtil.generarHash("Cliente123");
        String passTecnicoHash = PasswordUtil.generarHash("Tecnico123");
        String passAdminHash = PasswordUtil.generarHash("Admin123");

        db.execSQL("INSERT INTO usuarios (nombre, correo, password_hash, rol, activo) VALUES " +
                "('Juan Cliente', 'cliente@houston.local', '" + passClienteHash + "', 'CLIENTE', 1);");

        db.execSQL("INSERT INTO usuarios (nombre, correo, password_hash, rol, activo) VALUES " +
                "('Carlos Técnico', 'tecnico@houston.local', '" + passTecnicoHash + "', 'TECNICO', 1);");

        db.execSQL("INSERT INTO usuarios (nombre, correo, password_hash, rol, activo) VALUES " +
                "('Ana Administradora', 'admin@houston.local', '" + passAdminHash + "', 'ADMIN', 1);");

        // 4. Inserción de 3 tickets de prueba con distintos estados
        db.execSQL("INSERT INTO tickets (titulo, descripcion, categoria, prioridad, estado, creado_por, tecnico_id) VALUES " +
                "('Fallo en pantalla de inicio', 'La pantalla se queda congelada al iniciar sesión.', 'Software', 'Alta', 'ABIERTO', 1, NULL);");

        db.execSQL("INSERT INTO tickets (titulo, descripcion, categoria, prioridad, estado, creado_por, tecnico_id) VALUES " +
                "('Teclado no responde', 'La tecla Enter dejó de funcionar tras la actualización.', 'Hardware', 'Media', 'EN_PROCESO', 1, 2);");

        db.execSQL("INSERT INTO tickets (titulo, descripcion, categoria, prioridad, estado, creado_por, tecnico_id) VALUES " +
                "('Restablecimiento de correo', 'Solicitud de reinicio de contraseña de correo corporativo.', 'Redes', 'Baja', 'RESUELTO', 1, 2);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tickets;");
        db.execSQL("DROP TABLE IF EXISTS usuarios;");
        onCreate(db);
    }
}
