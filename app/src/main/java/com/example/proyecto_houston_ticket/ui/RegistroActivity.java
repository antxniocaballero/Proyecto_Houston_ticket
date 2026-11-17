package com.example.proyecto_houston_ticket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_houston_ticket.R;
import com.example.proyecto_houston_ticket.servicio.AuthService;

public class RegistroActivity extends AppCompatActivity {
    private EditText etNombre, etEmail, etPassword, etRepetirPassword;
    private TextView tvErrorRegistro, tvGoToLogin;
    private Button btnRegistrar;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        authService = new AuthService(this);

        etNombre = findViewById(R.id.etNombreRegistro);
        etEmail = findViewById(R.id.etEmailRegistro);
        etPassword = findViewById(R.id.etPasswordRegistro);
        etRepetirPassword = findViewById(R.id.etRepetirPasswordRegistro);
        tvErrorRegistro = findViewById(R.id.tvErrorRegistro);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegistrar.setOnClickListener(v -> intentarRegistro());

        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void intentarRegistro() {
        String nombre = etNombre.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String repetirPassword = etRepetirPassword.getText().toString();

        StringBuilder mensajeError = new StringBuilder();
        boolean exito = authService.registrarCliente(nombre, email, password, repetirPassword, mensajeError);

        if (exito) {
            Toast.makeText(this, "Cuenta creada, ya puedes entrar", Toast.LENGTH_LONG).show();
            finish();
        } else {
            tvErrorRegistro.setVisibility(View.VISIBLE);
            tvErrorRegistro.setText(mensajeError.toString());
        }
    }
}
