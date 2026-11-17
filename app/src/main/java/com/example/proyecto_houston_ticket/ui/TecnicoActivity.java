package com.example.proyecto_houston_ticket.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_houston_ticket.R;

public class TecnicoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tecnico);

        SharedPreferences prefs = getSharedPreferences("HoustonPrefs", MODE_PRIVATE);
        String nombre = prefs.getString("user_nombre", "Usuario");

        TextView tvBienvenida = findViewById(R.id.tvBienvenidaTecnico);
        tvBienvenida.setText("Bienvenido, " + nombre + " (TECNICO)");

        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesionTecnico);
        btnCerrarSesion.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent intent = new Intent(TecnicoActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
