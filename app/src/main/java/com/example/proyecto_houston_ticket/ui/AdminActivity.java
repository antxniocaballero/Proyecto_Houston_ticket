package com.example.proyecto_houston_ticket.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
 * Pantalla principal del rol ADMIN (Sprint 3).
 * Muestra la totalidad de los tickets del sistema para gestión y administración global.
 */
public class AdminActivity extends AppCompatActivity {

    private TextView tvBienvenida, tvSinTickets;
    private RecyclerView rvTickets;
    private Button btnCerrarSesion;

    private TicketService ticketService;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        ticketService = new TicketService(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        SharedPreferences prefs = getSharedPreferences("HoustonSession", MODE_PRIVATE);
        String nombre = prefs.getString("user_name", "Administrador");
        String rol = prefs.getString("user_role", "ADMIN");

        tvBienvenida = findViewById(R.id.tvBienvenidaAdmin);
        tvSinTickets = findViewById(R.id.tvSinTicketsAdmin);
        rvTickets = findViewById(R.id.rvTicketsAdmin);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionAdmin);

        tvBienvenida.setText(getString(R.string.welcome_message, nombre, rol));
        rvTickets.setLayoutManager(new LinearLayoutManager(this));

        btnCerrarSesion.setOnClickListener(v -> {
            AuthService authService = new AuthService(AdminActivity.this);
            authService.cerrarSesion();

            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTodosLosTickets();
    }

    private void cargarTodosLosTickets() {
        executorService.execute(() -> {
            List<Ticket> tickets = ticketService.obtenerTodosLosTickets();

            mainHandler.post(() -> {
                if (tickets != null && !tickets.isEmpty()) {
                    tvSinTickets.setVisibility(View.GONE);
                    rvTickets.setVisibility(View.VISIBLE);

                    TicketAdapter adapter = new TicketAdapter(tickets, ticket -> {
                        Intent intent = new Intent(AdminActivity.this, DetalleTicketActivity.class);
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
