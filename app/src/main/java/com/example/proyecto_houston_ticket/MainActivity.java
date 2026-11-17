package com.example.proyecto_houston_ticket;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_houston_ticket.modelo.Rol;
import com.example.proyecto_houston_ticket.servicio.AuthService;
import com.example.proyecto_houston_ticket.ui.LoginActivity;

/**
 * Pantalla inicial de entrada (Launcher).
 * Establece el layout visual con indicador de carga para evitar pantallas en negro
 * y comprueba si existe una sesión previa guardada para redirigir según corresponda.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 1. Establecer el contenido de la vista inmediatamente (evita pantalla en negro)
        setContentView(R.layout.activity_main);

        AuthService authService = new AuthService(this);

        // 2. Comprobar sesión activa y redirigir
        if (authService.haySesionActiva()) {
            Rol rolSesion = authService.obtenerRolSesion();
            Class<?> destino = authService.obtenerActividadPorRol(rolSesion);

            Intent intent = new Intent(MainActivity.this, destino);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        // Finalizar MainActivity para que el usuario no vuelva atrás pulsando "Volver"
        finish();
    }
}
