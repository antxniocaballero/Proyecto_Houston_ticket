package com.example.proyecto_houston_ticket.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_houston_ticket.R;
import com.example.proyecto_houston_ticket.servicio.AuthService;

/**
 * Pantalla principal del rol ADMIN (Esqueleto Sprint 1).
 */
public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        SharedPreferences prefs = getSharedPreferences("HoustonSession", MODE_PRIVATE);
        String nombre = prefs.getString("user_name", "Administrador");
        String rol = prefs.getString("user_role", "ADMIN");

        TextView tvBienvenida = findViewById(R.id.tvBienvenidaAdmin);
        tvBienvenida.setText(getString(R.string.welcome_message, nombre, rol));

        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesionAdmin);
        btnCerrarSesion.setOnClickListener(v -> {
            AuthService authService = new AuthService(AdminActivity.this);
            authService.cerrarSesion();

            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
