package com.example.proyecto_houston_ticket.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.proyecto_houston_ticket.R;
import com.example.proyecto_houston_ticket.modelo.Ticket;
import com.example.proyecto_houston_ticket.servicio.TicketService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Pantalla para la edición de los datos de un ticket existente.
 */
public class EditarTicketActivity extends AppCompatActivity {

    public static final String EXTRA_TICKET_ID = "extra_ticket_id";

    private EditText etTitulo, etDescripcion;
    private Spinner spinnerCategoria, spinnerPrioridad;
    private Button btnGuardarCambios, btnCancelar;

    private TicketService ticketService;
    private ExecutorService executorService;
    private Handler mainHandler;

    private long ticketId;
    private Ticket ticketActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ticket);

        Toolbar toolbar = findViewById(R.id.toolbarEditar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        ticketService = new TicketService(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        etTitulo = findViewById(R.id.etEditarTitulo);
        etDescripcion = findViewById(R.id.etEditarDescripcion);
        spinnerCategoria = findViewById(R.id.spinnerEditarCategoria);
        spinnerPrioridad = findViewById(R.id.spinnerEditarPrioridad);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnCancelar = findViewById(R.id.btnCancelarEditar);

        String[] categorias = {"Software", "Hardware", "Redes", "Accesos / Permisos", "Otros"};
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);
        spinnerCategoria.setAdapter(adapterCat);

        String[] prioridades = {"Baja", "Media", "Alta"};
        ArrayAdapter<String> adapterPrio = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, prioridades);
        spinnerPrioridad.setAdapter(adapterPrio);

        ticketId = getIntent().getLongExtra(EXTRA_TICKET_ID, -1);
        if (ticketId == -1) {
            Toast.makeText(this, "Ticket no válido.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnCancelar.setOnClickListener(v -> finish());
        btnGuardarCambios.setOnClickListener(v -> guardarCambios());

        cargarDatosTicket();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void cargarDatosTicket() {
        executorService.execute(() -> {
            ticketActual = ticketService.obtenerDetalleTicket(ticketId);

            mainHandler.post(() -> {
                if (ticketActual != null) {
                    etTitulo.setText(ticketActual.getTitulo());
                    etDescripcion.setText(ticketActual.getDescripcion());

                    String cat = ticketActual.getCategoria();
                    ArrayAdapter adapterCat = (ArrayAdapter) spinnerCategoria.getAdapter();
                    if (cat != null && adapterCat != null) {
                        int pos = adapterCat.getPosition(cat);
                        if (pos >= 0) spinnerCategoria.setSelection(pos);
                    }

                    String prio = ticketActual.getPrioridad();
                    ArrayAdapter adapterPrio = (ArrayAdapter) spinnerPrioridad.getAdapter();
                    if (prio != null && adapterPrio != null) {
                        int pos = adapterPrio.getPosition(prio);
                        if (pos >= 0) spinnerPrioridad.setSelection(pos);
                    }
                } else {
                    Toast.makeText(EditarTicketActivity.this, "No se encontró el ticket.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void guardarCambios() {
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String categoria = spinnerCategoria.getSelectedItem() != null ? spinnerCategoria.getSelectedItem().toString() : "";
        String prioridad = spinnerPrioridad.getSelectedItem() != null ? spinnerPrioridad.getSelectedItem().toString() : "";

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
            Toast.makeText(this, "Por favor, completa los campos obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardarCambios.setEnabled(false);

        executorService.execute(() -> {
            StringBuilder errorOut = new StringBuilder();
            boolean exito = ticketService.actualizarTicket(ticketId, titulo, descripcion, categoria, prioridad, errorOut);

            mainHandler.post(() -> {
                btnGuardarCambios.setEnabled(true);
                if (exito) {
                    Toast.makeText(EditarTicketActivity.this, "Ticket actualizado con éxito.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditarTicketActivity.this, "Error al actualizar: " + errorOut.toString(), Toast.LENGTH_LONG).show();
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
