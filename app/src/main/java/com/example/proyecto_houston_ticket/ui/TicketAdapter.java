package com.example.proyecto_houston_ticket.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_houston_ticket.R;
import com.example.proyecto_houston_ticket.modelo.Ticket;

import java.util.List;

/**
 * Adaptador para RecyclerView que muestra el listado de tickets.
 */
public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    public interface OnTicketClickListener {
        void onTicketClick(Ticket ticket);
    }

    private final List<Ticket> listaTickets;
    private final OnTicketClickListener listener;

    public TicketAdapter(List<Ticket> listaTickets, OnTicketClickListener listener) {
        this.listaTickets = listaTickets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = listaTickets.get(position);
        holder.bind(ticket, listener);
    }

    @Override
    public int getItemCount() {
        return listaTickets != null ? listaTickets.size() : 0;
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitulo;
        private final TextView tvEstado;
        private final TextView tvCategoria;
        private final TextView tvPrioridad;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvItemTitulo);
            tvEstado = itemView.findViewById(R.id.tvItemEstado);
            tvCategoria = itemView.findViewById(R.id.tvItemCategoria);
            tvPrioridad = itemView.findViewById(R.id.tvItemPrioridad);
        }

        public void bind(final Ticket ticket, final OnTicketClickListener listener) {
            tvTitulo.setText(ticket.getTitulo());
            tvEstado.setText(ticket.getEstado());
            tvCategoria.setText("Categoría: " + ticket.getCategoria());
            tvPrioridad.setText("Prioridad: " + ticket.getPrioridad());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTicketClick(ticket);
                }
            });
        }
    }
}
