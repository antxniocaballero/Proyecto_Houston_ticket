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
 * Pantalla principal del rol CLIENTE (Esqueleto Sprint 1).
 */
public class ClienteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        SharedPreferences prefs = getSharedPreferences("HoustonSession", MODE_PRIVATE);
        String nombre = prefs.getString("user_name", "Cliente");
        String rol = prefs.getString("user_role", "CLIENTE");

        TextView tvBienvenida = findViewById(R.id.tvBienvenidaCliente);
        tvBienvenida.setText(getString(R.string.welcome_message, nombre, rol));

        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesionCliente);
        btnCerrarSesion.setOnClickListener(v -> {
            AuthService authService = new AuthService(ClienteActivity.this);
            authService.cerrarSesion();

            Intent intent = new Intent(ClienteActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
