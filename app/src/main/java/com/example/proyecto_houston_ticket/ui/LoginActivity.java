package com.example.proyecto_houston_ticket.ui;

import android.content.Intent;
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
import com.example.proyecto_houston_ticket.modelo.Usuario;
import com.example.proyecto_houston_ticket.servicio.AuthService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Pantalla de inicio de sesión.
 * Permite ingresar correo y contraseña, realizando las operaciones de BD en un hilo secundario.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etCorreo;
    private EditText etPassword;
    private TextView tvErrorLogin;
    private Button btnLogin;
    private TextView tvGoToRegistro;

    private AuthService authService;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authService = new AuthService(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // Referencias a los componentes del layout
        etCorreo = findViewById(R.id.etCorreoLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        tvErrorLogin = findViewById(R.id.tvErrorLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegistro = findViewById(R.id.tvGoToRegistro);

        // Evento botón de inicio de sesión
        btnLogin.setOnClickListener(v -> intentarLogin());

        // Evento enlace hacia pantalla de registro
        tvGoToRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Procesa el login de manera asíncrona fuera del hilo principal.
     */
    private void intentarLogin() {
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString();

        tvErrorLogin.setVisibility(View.GONE);

        if (correo.isEmpty() || password.isEmpty()) {
            tvErrorLogin.setText(R.string.error_fields_required);
            tvErrorLogin.setVisibility(View.VISIBLE);
            return;
        }

        btnLogin.setEnabled(false);

        // Ejecutar consulta a la base de datos en hilo secundario
        executorService.execute(() -> {
            try {
                Usuario usuario = authService.login(correo, password);

                // Volver al hilo principal para actualizar la UI
                mainHandler.post(() -> {
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Bienvenido " + usuario.getNombre(), Toast.LENGTH_SHORT).show();

                    // Redirigir a la pantalla según el rol
                    Class<?> destino = authService.obtenerActividadPorRol(usuario.getRol());
                    Intent intent = new Intent(LoginActivity.this, destino);
                    startActivity(intent);
                    finish();
                });

            } catch (IllegalStateException e) {
                mainHandler.post(() -> {
                    btnLogin.setEnabled(true);
                    tvErrorLogin.setText(e.getMessage());
                    tvErrorLogin.setVisibility(View.VISIBLE);
                });
            } catch (IllegalArgumentException e) {
                mainHandler.post(() -> {
                    btnLogin.setEnabled(true);
                    tvErrorLogin.setText(R.string.error_invalid_credentials);
                    tvErrorLogin.setVisibility(View.VISIBLE);
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    btnLogin.setEnabled(true);
                    tvErrorLogin.setText("Error inesperado en el inicio de sesión.");
                    tvErrorLogin.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
