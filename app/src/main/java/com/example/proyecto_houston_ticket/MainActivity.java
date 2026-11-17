package com.example.proyecto_houston_ticket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_houston_ticket.ui.AdminActivity;
import com.example.proyecto_houston_ticket.ui.ClienteActivity;
import com.example.proyecto_houston_ticket.ui.LoginActivity;
import com.example.proyecto_houston_ticket.ui.RegistroActivity;
import com.example.proyecto_houston_ticket.ui.TecnicoActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Comprobar si hay sesión activa para redirigir directamente al panel correspondiente
        SharedPreferences prefs = getSharedPreferences("HoustonPrefs", MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        if (userId != -1) {
            String rol = prefs.getString("user_rol", "CLIENTE");
            redirigirPorRol(rol);
            return;
        }

        setContentView(R.layout.activity_main);

        Button btnIrLogin = findViewById(R.id.btnIrLogin);
        Button btnIrRegistro = findViewById(R.id.btnIrRegistro);

        btnIrLogin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));

        btnIrRegistro.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegistroActivity.class)));
    }

    private void redirigirPorRol(String rol) {
        Intent intent;
        switch (rol) {
            case "CLIENTE":
                intent = new Intent(MainActivity.this, ClienteActivity.class);
                break;
            case "TECNICO":
                intent = new Intent(MainActivity.this, TecnicoActivity.class);
                break;
            case "ADMIN":
                intent = new Intent(MainActivity.this, AdminActivity.class);
                break;
            default:
                intent = new Intent(MainActivity.this, LoginActivity.class);
                break;
        }
        startActivity(intent);
        finish();
    }
}
