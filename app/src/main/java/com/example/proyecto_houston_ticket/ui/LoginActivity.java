package com.example.proyecto_houston_ticket.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_houston_ticket.R;
import com.example.proyecto_houston_ticket.modelo.Usuario;
import com.example.proyecto_houston_ticket.servicio.AuthService;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private TextView tvErrorLogin, tvGoToRegistro;
    private Button btnLogin;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authService = new AuthService(this);

        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        tvErrorLogin = findViewById(R.id.tvErrorLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegistro = findViewById(R.id.tvGoToRegistro);

        // Comprobar si ya hay sesión activa
        SharedPreferences prefs = getSharedPreferences("HoustonPrefs", MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        String rol = prefs.getString("user_rol", "");
        if (userId != -1) {
            redirigirPorRol(rol);
            return;
        }

        btnLogin.setOnClickListener(v -> intentarLogin());

        tvGoToRegistro.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
        });
    }

    private void intentarLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        tvErrorLogin.setVisibility(View.GONE);

        try {
            Usuario usuario = authService.autenticar(email, password);
            if (usuario != null) {
                // Guardar sesión en SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences("HoustonPrefs", MODE_PRIVATE).edit();
                editor.putLong("user_id", usuario.getIdUsuario());
                editor.putString("user_nombre", usuario.getNombre());
                editor.putString("user_rol", usuario.getRol().name());
                editor.apply();

                redirigirPorRol(usuario.getRol().name());
            } else {
                tvErrorLogin.setVisibility(View.VISIBLE);
                tvErrorLogin.setText("Email o contraseña incorrectos.");
            }
        } catch (IllegalStateException e) {
            tvErrorLogin.setVisibility(View.VISIBLE);
            tvErrorLogin.setText(e.getMessage());
        }
    }

    private void redirigirPorRol(String rol) {
        Intent intent;
        switch (rol) {
            case "CLIENTE":
                intent = new Intent(LoginActivity.this, ClienteActivity.class);
                break;
            case "TECNICO":
                intent = new Intent(LoginActivity.this, TecnicoActivity.class);
                break;
            case "ADMIN":
                intent = new Intent(LoginActivity.this, AdminActivity.class);
                break;
            default:
                return;
        }
        startActivity(intent);
        finish();
    }
}
