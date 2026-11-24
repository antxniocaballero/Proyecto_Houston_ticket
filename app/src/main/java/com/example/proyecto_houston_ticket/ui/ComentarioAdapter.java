package com.example.proyecto_houston_ticket.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_houston_ticket.R;
import com.example.proyecto_houston_ticket.modelo.Comentario;

import java.util.List;

/**
 * Adaptador para RecyclerView que muestra la lista de comentarios de un ticket.
 */
public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder> {

    private final List<Comentario> listaComentarios;

    public ComentarioAdapter(List<Comentario> listaComentarios) {
        this.listaComentarios = listaComentarios;
    }

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario, parent, false);
        return new ComentarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = listaComentarios.get(position);
        holder.bind(comentario);
    }

    @Override
    public int getItemCount() {
        return listaComentarios != null ? listaComentarios.size() : 0;
    }

    static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAutor;
        private final TextView tvFecha;
        private final TextView tvTexto;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAutor = itemView.findViewById(R.id.tvComentarioAutor);
            tvFecha = itemView.findViewById(R.id.tvComentarioFecha);
            tvTexto = itemView.findViewById(R.id.tvComentarioTexto);
        }

        public void bind(Comentario comentario) {
            String autorInfo = comentario.getAutorNombre() + " (" + comentario.getRolAutor() + ")";
            tvAutor.setText(autorInfo);
            tvFecha.setText(comentario.getFechaCreacion() != null ? comentario.getFechaCreacion() : "");
            tvTexto.setText(comentario.getMensaje());
        }
    }
}
