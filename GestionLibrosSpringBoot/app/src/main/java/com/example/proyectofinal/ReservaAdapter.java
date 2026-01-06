package com.example.proyectofinal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectofinal.Clases.Reserva;

import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    private List<Reserva> reservas;

    // Constructor
    public ReservaAdapter(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    @Override
    public ReservaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflar el layout del item de reserva
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserva, parent, false);
        return new ReservaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReservaViewHolder holder, int position) {
        Reserva reserva = reservas.get(position);

        holder.tituloTextView.setText("Título del libro: " + reserva.getLibro().getTitulo());
        holder.cantidadTextView.setText("Cantidad: " + reserva.getCantidad());
        holder.usuarioTextView.setText("Usuario: " + reserva.getUsuario().getUsername());

        holder.precioFinalTextView.setText("Precio Final: " + reserva.getPrecioTotal() +"€ ");
    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }

    public static class ReservaViewHolder extends RecyclerView.ViewHolder {

        TextView tituloTextView, cantidadTextView, usuarioTextView, precioFinalTextView;

        public ReservaViewHolder(View itemView) {
            super(itemView);
            tituloTextView = itemView.findViewById(R.id.tituloTextView);
            cantidadTextView = itemView.findViewById(R.id.cantidadTextView);
            usuarioTextView = itemView.findViewById(R.id.usuarioTextView);
            precioFinalTextView = itemView.findViewById(R.id.precioFinalTextView);
        }
    }
}
