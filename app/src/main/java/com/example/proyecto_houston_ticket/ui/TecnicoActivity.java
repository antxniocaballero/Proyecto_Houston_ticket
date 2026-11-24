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
 * Pantalla principal del rol TECNICO.
 * Muestra únicamente los tickets asignados expresamente a su id_usuario.
 */
public class TecnicoActivity extends AppCompatActivity {

    private TextView tvSinTickets;
    private RecyclerView rvTickets;

    private TicketService ticketService;
    private ExecutorService executorService;
    private Handler mainHandler;
    private long tecnicoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tecnico);

        ticketService = new TicketService(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        SharedPreferences prefs = getSharedPreferences("HoustonSession", MODE_PRIVATE);
        String nombre = prefs.getString("user_name", "Técnico");
        String rol = prefs.getString("user_role", "TECNICO");
        tecnicoId = prefs.getLong("user_id", -1);

        TextView tvBienvenida = findViewById(R.id.tvBienvenidaTecnico);
        tvSinTickets = findViewById(R.id.tvSinTicketsTecnico);
        rvTickets = findViewById(R.id.rvTicketsTecnico);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesionTecnico);

        tvBienvenida.setText(getString(R.string.welcome_message, nombre, rol));
        rvTickets.setLayoutManager(new LinearLayoutManager(this));

        btnCerrarSesion.setOnClickListener(v -> {
            AuthService authService = new AuthService(TecnicoActivity.this);
            authService.cerrarSesion();

            Intent intent = new Intent(TecnicoActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTicketsAsignados();
    }

    private void cargarTicketsAsignados() {
        if (tecnicoId == -1) return;

        executorService.execute(() -> {
            // Filtrado estricto: Obtiene sólo los tickets donde tecnico_id == id_usuario_logueado
            List<Ticket> tickets = ticketService.obtenerTicketsTecnico(tecnicoId);

            mainHandler.post(() -> {
                if (tickets != null && !tickets.isEmpty()) {
                    tvSinTickets.setVisibility(View.GONE);
                    rvTickets.setVisibility(View.VISIBLE);

                    TicketAdapter adapter = new TicketAdapter(tickets, ticket -> {
                        Intent intent = new Intent(TecnicoActivity.this, DetalleTicketActivity.class);
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
