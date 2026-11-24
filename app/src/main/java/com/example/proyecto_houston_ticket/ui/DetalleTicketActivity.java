package com.example.proyecto_houston_ticket.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_houston_ticket.R;
import com.example.proyecto_houston_ticket.modelo.Ticket;
import com.example.proyecto_houston_ticket.servicio.TicketService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Pantalla para la visualización del detalle de un ticket específico.
 */
public class DetalleTicketActivity extends AppCompatActivity {

    public static final String EXTRA_TICKET_ID = "extra_ticket_id";

    private TextView tvId, tvEstado, tvTitulo, tvCategoria, tvPrioridad, tvFecha, tvDescripcion;
    private Button btnVolver;

    private TicketService ticketService;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ticket);

        ticketService = new TicketService(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        tvId = findViewById(R.id.tvDetalleId);
        tvEstado = findViewById(R.id.tvDetalleEstado);
        tvTitulo = findViewById(R.id.tvDetalleTitulo);
        tvCategoria = findViewById(R.id.tvDetalleCategoria);
        tvPrioridad = findViewById(R.id.tvDetallePrioridad);
        tvFecha = findViewById(R.id.tvDetalleFecha);
        tvDescripcion = findViewById(R.id.tvDetalleDescripcion);
        btnVolver = findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(v -> finish());

        long ticketId = getIntent().getLongExtra(EXTRA_TICKET_ID, -1);
        if (ticketId != -1) {
            cargarDetalleTicket(ticketId);
        } else {
            Toast.makeText(this, "Ticket no encontrado.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void cargarDetalleTicket(long ticketId) {
        executorService.execute(() -> {
            Ticket ticket = ticketService.obtenerDetalleTicket(ticketId);

            mainHandler.post(() -> {
                if (ticket != null) {
                    tvId.setText("Ticket #" + ticket.getId());
                    tvEstado.setText(ticket.getEstado());
                    tvTitulo.setText(ticket.getTitulo());
                    tvCategoria.setText("Categoría: " + ticket.getCategoria());
                    tvPrioridad.setText("Prioridad: " + ticket.getPrioridad());
                    tvFecha.setText("Creado el: " + (ticket.getFechaCreacion() != null ? ticket.getFechaCreacion() : "N/A"));
                    tvDescripcion.setText(ticket.getDescripcion());
                } else {
                    Toast.makeText(DetalleTicketActivity.this, "No se pudieron cargar los datos del ticket.", Toast.LENGTH_SHORT).show();
                    finish();
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
