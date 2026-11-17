package com.example.proyecto_houston_ticket.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_houston_ticket.R;
import com.example.proyecto_houston_ticket.servicio.AuthService;
import com.example.proyecto_houston_ticket.util.Validador;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Pantalla de registro público para nuevos usuarios (se les asigna el rol CLIENTE).
 * Realiza las operaciones de verificación e inserción en la BD en un hilo secundario.
 */
public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre;
    private EditText etCorreo;
    private EditText etPassword;
    private EditText etRepetirPassword;

    private TextView tvErrorNombre;
    private TextView tvErrorCorreo;
    private TextView tvErrorPassword;
    private TextView tvErrorRepetirPassword;

    private Button btnRegistrar;
    private TextView tvGoToLogin;

    private AuthService authService;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        authService = new AuthService(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // Referencias a los componentes de la interfaz
        etNombre = findViewById(R.id.etNombreRegistro);
        etCorreo = findViewById(R.id.etCorreoRegistro);
        etPassword = findViewById(R.id.etPasswordRegistro);
        etRepetirPassword = findViewById(R.id.etRepetirPasswordRegistro);

        tvErrorNombre = findViewById(R.id.tvErrorNombre);
        tvErrorCorreo = findViewById(R.id.tvErrorCorreo);
        tvErrorPassword = findViewById(R.id.tvErrorPassword);
        tvErrorRepetirPassword = findViewById(R.id.tvErrorRepetirPassword);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Eventos
        btnRegistrar.setOnClickListener(v -> intentarRegistro());
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    /**
     * Valida los campos en el hilo principal y ejecuta la inserción en la BD en hilo secundario.
     */
    private void intentarRegistro() {
        limpiarErrores();

        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString();
        String repetirPassword = etRepetirPassword.getText().toString();

        boolean esValido = true;

        if (nombre.isEmpty()) {
            tvErrorNombre.setText(R.string.error_fields_required);
            tvErrorNombre.setVisibility(View.VISIBLE);
            esValido = false;
        }

        if (!Validador.esCorreoValido(correo)) {
            tvErrorCorreo.setText(R.string.error_email_invalid);
            tvErrorCorreo.setVisibility(View.VISIBLE);
            esValido = false;
        }

        if (!Validador.esPasswordValida(password)) {
            tvErrorPassword.setText(R.string.error_password_length);
            tvErrorPassword.setVisibility(View.VISIBLE);
            esValido = false;
        }

        if (!password.equals(repetirPassword)) {
            tvErrorRepetirPassword.setText(R.string.error_password_mismatch);
            tvErrorRepetirPassword.setVisibility(View.VISIBLE);
            esValido = false;
        }

        if (!esValido) {
            return;
        }

        btnRegistrar.setEnabled(false);

        // Operación de base de datos en segundo hilo
        executorService.execute(() -> {
            StringBuilder errorOut = new StringBuilder();
            boolean exito = authService.registrarCliente(nombre, correo, password, repetirPassword, errorOut);

            mainHandler.post(() -> {
                btnRegistrar.setEnabled(true);
                if (exito) {
                    Toast.makeText(RegistroActivity.this, R.string.success_registration, Toast.LENGTH_LONG).show();
                    finish(); // Volver al Login
                } else {
                    tvErrorCorreo.setText(errorOut.toString());
                    tvErrorCorreo.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void limpiarErrores() {
        tvErrorNombre.setVisibility(View.GONE);
        tvErrorCorreo.setVisibility(View.GONE);
        tvErrorPassword.setVisibility(View.GONE);
        tvErrorRepetirPassword.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
