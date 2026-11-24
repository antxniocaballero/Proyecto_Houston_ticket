package com.example.proyecto_houston_ticket.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_houston_ticket.R;
import com.example.proyecto_houston_ticket.modelo.Comentario;
import com.example.proyecto_houston_ticket.modelo.EstadoTicket;
import com.example.proyecto_houston_ticket.modelo.Rol;
import com.example.proyecto_houston_ticket.modelo.Ticket;
import com.example.proyecto_houston_ticket.modelo.Usuario;
import com.example.proyecto_houston_ticket.servicio.TicketService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Pantalla para la visualización detallada, asignación de técnico, eliminación y comentarios de un ticket.
 */
public class DetalleTicketActivity extends AppCompatActivity {

    public static final String EXTRA_TICKET_ID = "extra_ticket_id";

    private TextView tvId, tvEstado, tvTitulo, tvCategoria, tvPrioridad, tvTecnico, tvFecha, tvDescripcion, tvSinComentarios;
    private Button btnAvanzarEstado, btnEditarTicket, btnEliminarTicket, btnAsignarTecnico, btnEnviarComentario;
    private Spinner spinnerTecnicos;
    private LinearLayout layoutAsignarTecnico;
    private RecyclerView rvComentarios;
    private EditText etNuevoComentario;

    private TicketService ticketService;
    private ExecutorService executorService;
    private Handler mainHandler;

    private long ticketId;
    private Ticket ticketActual;
    private String rolUsuario;
    private String nombreUsuario;
    private long usuarioId;
    private List<Usuario> listaTecnicos = new ArrayList<>();

    private final ActivityResultLauncher<Intent> editarLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    cargarDetalleTicket();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ticket);

        Toolbar toolbar = findViewById(R.id.toolbarDetalle);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        ticketService = new TicketService(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        SharedPreferences prefs = getSharedPreferences("HoustonSession", MODE_PRIVATE);
        usuarioId = prefs.getLong("user_id", -1);
        nombreUsuario = prefs.getString("user_name", "Usuario");
        rolUsuario = prefs.getString("user_role", Rol.CLIENTE.name());

        tvId = findViewById(R.id.tvDetalleId);
        tvEstado = findViewById(R.id.tvDetalleEstado);
        tvTitulo = findViewById(R.id.tvDetalleTitulo);
        tvCategoria = findViewById(R.id.tvDetalleCategoria);
        tvPrioridad = findViewById(R.id.tvDetallePrioridad);
        tvTecnico = findViewById(R.id.tvDetalleTecnico);
        tvFecha = findViewById(R.id.tvDetalleFecha);
        tvDescripcion = findViewById(R.id.tvDetalleDescripcion);
        tvSinComentarios = findViewById(R.id.tvSinComentarios);

        layoutAsignarTecnico = findViewById(R.id.layoutAsignarTecnico);
        spinnerTecnicos = findViewById(R.id.spinnerTecnicos);
        btnAsignarTecnico = findViewById(R.id.btnAsignarTecnico);

        btnAvanzarEstado = findViewById(R.id.btnAvanzarEstado);
        btnEditarTicket = findViewById(R.id.btnEditarTicket);
        btnEliminarTicket = findViewById(R.id.btnEliminarTicket);

        rvComentarios = findViewById(R.id.rvComentarios);
        etNuevoComentario = findViewById(R.id.etNuevoComentario);
        btnEnviarComentario = findViewById(R.id.btnEnviarComentario);

        rvComentarios.setLayoutManager(new LinearLayoutManager(this));

        btnAvanzarEstado.setOnClickListener(v -> intentarAvanzarEstado());

        btnEditarTicket.setOnClickListener(v -> {
            Intent intent = new Intent(DetalleTicketActivity.this, EditarTicketActivity.class);
            intent.putExtra(EditarTicketActivity.EXTRA_TICKET_ID, ticketId);
            editarLauncher.launch(intent);
        });

        btnEliminarTicket.setOnClickListener(v -> confirmarEliminacionTicket());

        btnAsignarTecnico.setOnClickListener(v -> intentarAsignarTecnico());

        btnEnviarComentario.setOnClickListener(v -> enviarComentario());

        ticketId = getIntent().getLongExtra(EXTRA_TICKET_ID, -1);
        if (ticketId != -1) {
            cargarDetalleTicket();
            cargarComentarios();
            if ("ADMIN".equals(rolUsuario)) {
                cargarListaTecnicos();
            }
        } else {
            Toast.makeText(this, "Ticket no encontrado.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void cargarDetalleTicket() {
        executorService.execute(() -> {
            ticketActual = ticketService.obtenerDetalleTicket(ticketId);

            mainHandler.post(() -> {
                if (ticketActual != null) {
                    tvId.setText("Ticket #" + ticketActual.getId());
                    tvEstado.setText(ticketActual.getEstado());
                    tvTitulo.setText(ticketActual.getTitulo());
                    tvCategoria.setText("Categoría: " + ticketActual.getCategoria());
                    tvPrioridad.setText("Prioridad: " + ticketActual.getPrioridad());
                    tvFecha.setText("Creado el: " + (ticketActual.getFechaCreacion() != null ? ticketActual.getFechaCreacion() : "N/A"));
                    tvDescripcion.setText(ticketActual.getDescripcion());

                    if (ticketActual.getTecnicoId() != null) {
                        tvTecnico.setText("Técnico ID: #" + ticketActual.getTecnicoId());
                    } else {
                        tvTecnico.setText("Técnico asignado: Sin asignar");
                    }

                    configurarBotonesAccion();
                } else {
                    Toast.makeText(DetalleTicketActivity.this, "No se encontraron datos del ticket.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void configurarBotonesAccion() {
        if (ticketActual == null) return;

        EstadoTicket estadoActual = ticketActual.getEstadoEnum();
        EstadoTicket siguienteEstado = ticketActual.obtenerSiguienteEstado();

        // 1. Configurar botón de Avanzar Estado
        if (siguienteEstado != null) {
            btnAvanzarEstado.setVisibility(View.VISIBLE);
            switch (siguienteEstado) {
                case EN_CURSO:
                    btnAvanzarEstado.setText("Pasar a En curso");
                    break;
                case RESUELTO:
                    btnAvanzarEstado.setText("Marcar como Resuelto");
                    break;
                case CERRADO:
                    btnAvanzarEstado.setText("Cerrar Ticket");
                    break;
            }
        } else {
            btnAvanzarEstado.setVisibility(View.GONE);
        }

        // 2. Configurar botón de Editar Ticket
        if ("CLIENTE".equals(rolUsuario)) {
            btnEditarTicket.setVisibility(ticketActual.esEditablePorCliente() ? View.VISIBLE : View.GONE);
        } else {
            btnEditarTicket.setVisibility(estadoActual != EstadoTicket.CERRADO ? View.VISIBLE : View.GONE);
        }

        // 3. Configurar botón de Eliminar Ticket (Solo en estado RESUELTO o CERRADO)
        if (estadoActual == EstadoTicket.RESUELTO || estadoActual == EstadoTicket.CERRADO) {
            btnEliminarTicket.setVisibility(View.VISIBLE);
        } else {
            btnEliminarTicket.setVisibility(View.GONE);
        }

        // 4. Mostrar sección de asignación de técnico sólo para Administradores
        if ("ADMIN".equals(rolUsuario)) {
            layoutAsignarTecnico.setVisibility(View.VISIBLE);
        } else {
            layoutAsignarTecnico.setVisibility(View.GONE);
        }
    }

    private void cargarListaTecnicos() {
        executorService.execute(() -> {
            listaTecnicos = ticketService.obtenerListaTecnicos();

            mainHandler.post(() -> {
                if (listaTecnicos != null && !listaTecnicos.isEmpty()) {
                    List<String> nombresTecnicos = new ArrayList<>();
                    for (Usuario u : listaTecnicos) {
                        nombresTecnicos.add(u.getNombre() + " (" + u.getCorreo() + ")");
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DetalleTicketActivity.this, android.R.layout.simple_spinner_dropdown_item, nombresTecnicos);
                    spinnerTecnicos.setAdapter(adapter);
                }
            });
        });
    }

    private void intentarAsignarTecnico() {
        if (spinnerTecnicos.getSelectedItemPosition() < 0 || listaTecnicos.isEmpty()) {
            Toast.makeText(this, "No hay técnicos disponibles.", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario tecnicoSeleccionado = listaTecnicos.get(spinnerTecnicos.getSelectedItemPosition());
        btnAsignarTecnico.setEnabled(false);

        executorService.execute(() -> {
            StringBuilder errorOut = new StringBuilder();
            boolean exito = ticketService.asignarTecnico(ticketId, tecnicoSeleccionado.getId(), errorOut);

            mainHandler.post(() -> {
                btnAsignarTecnico.setEnabled(true);
                if (exito) {
                    Toast.makeText(DetalleTicketActivity.this, "Técnico asignado con éxito.", Toast.LENGTH_SHORT).show();
                    cargarDetalleTicket();
                } else {
                    Toast.makeText(DetalleTicketActivity.this, "Error al asignar: " + errorOut.toString(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void confirmarEliminacionTicket() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Ticket")
                .setMessage("¿Estás seguro de que deseas eliminar este ticket? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> ejecutarEliminacionTicket())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void ejecutarEliminacionTicket() {
        executorService.execute(() -> {
            StringBuilder errorOut = new StringBuilder();
            boolean exito = ticketService.eliminarTicketResuelto(ticketId, errorOut);

            mainHandler.post(() -> {
                if (exito) {
                    Toast.makeText(DetalleTicketActivity.this, "Ticket eliminado correctamente.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(DetalleTicketActivity.this, "Error al eliminar: " + errorOut.toString(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void cargarComentarios() {
        executorService.execute(() -> {
            List<Comentario> comentarios = ticketService.obtenerComentarios(ticketId);

            mainHandler.post(() -> {
                if (comentarios != null && !comentarios.isEmpty()) {
                    tvSinComentarios.setVisibility(View.GONE);
                    rvComentarios.setVisibility(View.VISIBLE);
                    ComentarioAdapter adapter = new ComentarioAdapter(comentarios);
                    rvComentarios.setAdapter(adapter);
                } else {
                    rvComentarios.setVisibility(View.GONE);
                    tvSinComentarios.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void enviarComentario() {
        String mensaje = etNuevoComentario.getText().toString().trim();
        if (mensaje.isEmpty()) {
            etNuevoComentario.setError("Escribe un mensaje");
            return;
        }

        btnEnviarComentario.setEnabled(false);

        executorService.execute(() -> {
            StringBuilder errorOut = new StringBuilder();
            boolean exito = ticketService.agregarComentario(ticketId, usuarioId, nombreUsuario, rolUsuario, mensaje, errorOut);

            mainHandler.post(() -> {
                btnEnviarComentario.setEnabled(true);
                if (exito) {
                    etNuevoComentario.setText("");
                    Toast.makeText(DetalleTicketActivity.this, "Comentario enviado.", Toast.LENGTH_SHORT).show();
                    cargarComentarios();
                } else {
                    Toast.makeText(DetalleTicketActivity.this, "Error: " + errorOut.toString(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void intentarAvanzarEstado() {
        if (ticketActual == null || !ticketActual.puedeAvanzarEstado()) return;

        btnAvanzarEstado.setEnabled(false);

        executorService.execute(() -> {
            StringBuilder errorOut = new StringBuilder();
            boolean exito = ticketService.avanzarEstadoTicket(ticketId, errorOut);

            mainHandler.post(() -> {
                btnAvanzarEstado.setEnabled(true);
                if (exito) {
                    Toast.makeText(DetalleTicketActivity.this, "Estado actualizado con éxito.", Toast.LENGTH_SHORT).show();
                    cargarDetalleTicket();
                } else {
                    Toast.makeText(DetalleTicketActivity.this, "Error: " + errorOut.toString(), Toast.LENGTH_LONG).show();
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
