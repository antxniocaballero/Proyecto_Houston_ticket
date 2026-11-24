package com.example.proyecto_houston_ticket.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_houston_ticket.R;
import com.example.proyecto_houston_ticket.modelo.Ticket;
import com.example.proyecto_houston_ticket.servicio.AuthService;
import com.example.proyecto_houston_ticket.servicio.TicketService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Pantalla principal del rol CLIENTE (Sprint 2).
 * Muestra el listado de tickets propios y permite crear e inspeccionar el detalle de cada ticket.
 */
public class ClienteActivity extends AppCompatActivity {

    private TextView tvBienvenida;
    private TextView tvSinTickets;
    private RecyclerView rvTickets;
    private Button btnNuevoTicket;
    private Button btnCerrarSesion;

    private TicketService ticketService;
    private ExecutorService executorService;
    private Handler mainHandler;
    private long usuarioId;

    // Launcher para refrescar la lista tras crear un nuevo ticket
    private final ActivityResultLauncher<Intent> crearTicketLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    cargarTickets();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        ticketService = new TicketService(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        SharedPreferences prefs = getSharedPreferences("HoustonSession", MODE_PRIVATE);
        String nombre = prefs.getString("user_name", "Cliente");
        String rol = prefs.getString("user_role", "CLIENTE");
        usuarioId = prefs.getLong("user_id", -1);

        tvBienvenida = findViewById(R.id.tvBienvenidaCliente);
        tvSinTickets = findViewById(R.id.tvSinTickets);
        rvTickets = findViewById(R.id.rvTicketsCliente);
        btnNuevoTicket = findViewById(R.id.btnNuevoTicket);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionCliente);

        tvBienvenida.setText(getString(R.string.welcome_message, nombre, rol));
        rvTickets.setLayoutManager(new LinearLayoutManager(this));

        btnNuevoTicket.setOnClickListener(v -> {
            Intent intent = new Intent(ClienteActivity.this, CrearTicketActivity.class);
            crearTicketLauncher.launch(intent);
        });

        btnCerrarSesion.setOnClickListener(v -> {
            AuthService authService = new AuthService(ClienteActivity.this);
            authService.cerrarSesion();

            Intent intent = new Intent(ClienteActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        cargarTickets();
    }

    private void cargarTickets() {
        if (usuarioId == -1) return;

        executorService.execute(() -> {
            List<Ticket> tickets = ticketService.obtenerTicketsUsuario(usuarioId);

            mainHandler.post(() -> {
                if (tickets != null && !tickets.isEmpty()) {
                    tvSinTickets.setVisibility(View.GONE);
                    rvTickets.setVisibility(View.VISIBLE);

                    TicketAdapter adapter = new TicketAdapter(tickets, ticket -> {
                        Intent intent = new Intent(ClienteActivity.this, DetalleTicketActivity.class);
                        intent.putExtra(DetalleTicketActivity.EXTRA_TICKET_ID, ticket.getId());
                        startActivity(intent);
                    });
                    rvTickets.setAdapter(adapter);
                } else {
                    rvTickets.setVisibility(View.GONE);
                    tvSinTickets.setVisibility(View.VISIBLE);
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
