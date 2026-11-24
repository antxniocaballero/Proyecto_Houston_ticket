package com.example.proyecto_houston_ticket.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_houston_ticket.R;
import com.example.proyecto_houston_ticket.servicio.TicketService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Pantalla para la creación de un nuevo ticket de incidencia.
 * Realiza la validación de campos obligatorios en el cliente antes del envío.
 */
public class CrearTicketActivity extends AppCompatActivity {

    private EditText etTitulo;
    private EditText etDescripcion;
    private Spinner spinnerCategoria;
    private Spinner spinnerPrioridad;
    private Button btnGuardar;

    private TicketService ticketService;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_ticket);

        ticketService = new TicketService(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        etTitulo = findViewById(R.id.etTituloTicket);
        etDescripcion = findViewById(R.id.etDescripcionTicket);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerPrioridad = findViewById(R.id.spinnerPrioridad);
        btnGuardar = findViewById(R.id.btnGuardarTicket);

        // Configurar opciones de Categorías
        String[] categorias = {"Software", "Hardware", "Redes", "Accesos / Permisos", "Otros"};
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);
        spinnerCategoria.setAdapter(adapterCat);

        // Configurar opciones de Prioridades
        String[] prioridades = {"Baja", "Media", "Alta"};
        ArrayAdapter<String> adapterPrio = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, prioridades);
        spinnerPrioridad.setAdapter(adapterPrio);

        btnGuardar.setOnClickListener(v -> guardarTicket());
    }

    private void guardarTicket() {
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String categoria = spinnerCategoria.getSelectedItem() != null ? spinnerCategoria.getSelectedItem().toString() : "";
        String prioridad = spinnerPrioridad.getSelectedItem() != null ? spinnerPrioridad.getSelectedItem().toString() : "";

        // Validaciones en cliente
        boolean hayError = false;

        if (titulo.isEmpty()) {
            etTitulo.setError("El título es obligatorio");
            hayError = true;
        }

        if (descripcion.isEmpty()) {
            etDescripcion.setError("La descripción es obligatoria");
            hayError = true;
        }

        if (hayError) {
            Toast.makeText(this, "Por favor, completa todos los campos obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("HoustonSession", MODE_PRIVATE);
        long usuarioId = prefs.getLong("user_id", -1);

        if (usuarioId == -1) {
            Toast.makeText(this, "Sesión no válida. Inicia sesión de nuevo.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnGuardar.setEnabled(false);

        // Ejecutar inserción/petición en segundo hilo
        executorService.execute(() -> {
            StringBuilder errorOut = new StringBuilder();
            long idInsertado = ticketService.crearTicket(titulo, descripcion, categoria, prioridad, usuarioId, errorOut);

            mainHandler.post(() -> {
                btnGuardar.setEnabled(true);
                if (idInsertado != -1) {
                    Toast.makeText(CrearTicketActivity.this, "Ticket creado con éxito (#" + idInsertado + ")", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CrearTicketActivity.this, "Error al crear ticket: " + errorOut.toString(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
