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
import androidx.appcompat.widget.Toolbar;

import com.example.proyecto_houston_ticket.R;
import com.example.proyecto_houston_ticket.servicio.AuthService;
import com.example.proyecto_houston_ticket.util.Validador;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Pantalla de registro público para nuevos usuarios.
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
    private Button btnVolverLogin;
    private TextView tvGoToLogin;

    private AuthService authService;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Toolbar toolbar = findViewById(R.id.toolbarRegistro);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        authService = new AuthService(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        etNombre = findViewById(R.id.etNombreRegistro);
        etCorreo = findViewById(R.id.etCorreoRegistro);
        etPassword = findViewById(R.id.etPasswordRegistro);
        etRepetirPassword = findViewById(R.id.etRepetirPasswordRegistro);

        tvErrorNombre = findViewById(R.id.tvErrorNombre);
        tvErrorCorreo = findViewById(R.id.tvErrorCorreo);
        tvErrorPassword = findViewById(R.id.tvErrorPassword);
        tvErrorRepetirPassword = findViewById(R.id.tvErrorRepetirPassword);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnVolverLogin = findViewById(R.id.btnVolverLogin);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegistrar.setOnClickListener(v -> intentarRegistro());
        btnVolverLogin.setOnClickListener(v -> finish());
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

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

        executorService.execute(() -> {
            StringBuilder errorOut = new StringBuilder();
            boolean exito = authService.registrarCliente(nombre, correo, password, repetirPassword, errorOut);

            mainHandler.post(() -> {
                btnRegistrar.setEnabled(true);
                if (exito) {
                    Toast.makeText(RegistroActivity.this, R.string.success_registration, Toast.LENGTH_LONG).show();
                    finish();
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
