package com.example.proyectofinal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectofinal.Clases.Libro;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LibroAdapter extends RecyclerView.Adapter<LibroAdapter.LibroViewHolder> {

    private List<Libro> librosList;
    private OnItemClickListener listener;
    private boolean isAdmin;

    // Constructor para inicializar la lista, el listener y isAdmin
    public LibroAdapter(List<Libro> librosList, OnItemClickListener listener, boolean isAdmin) {
        this.librosList = librosList;
        this.listener = listener;
        this.isAdmin = isAdmin; // Inicializamos isAdmin
    }

    @NonNull
    @Override
    public LibroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_libro, parent, false);
        return new LibroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibroViewHolder holder, int position) {
        Libro libro = librosList.get(position);
        holder.tituloTextView.setText(libro.getTitulo());
        holder.autorTextView.setText(libro.getAutor());

        // Mostrar el precio
        holder.precioTextView.setText("€" + libro.getPrecio());

        // Verificar si la URL de la imagen no es nula ni vacía
        if (libro.getImagenUrl() != null && !libro.getImagenUrl().isEmpty()) {
            Picasso.get()
                    .load(libro.getImagenUrl())
                    .into(holder.imagenImageView);
            holder.imagenImageView.setVisibility(View.VISIBLE);
        } else {
            holder.imagenImageView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent;
            // Verificar si estamos en AdminActivity o ClienteActivity
            if (isAdmin) {
                // Si estamos en AdminActivity, abrir EditarActivity
                intent = new Intent(v.getContext(), EditarActivity.class);
            } else {
                // Si estamos en ClienteActivity, abrir DetalleLibroActivity
                intent = new Intent(v.getContext(), DetalleLibroActivity.class);
            }

            // Pasar los datos del libro
            intent.putExtra("titulo", libro.getTitulo());
            intent.putExtra("autor", libro.getAutor());
            intent.putExtra("imagenUrl", libro.getImagenUrl());
            intent.putExtra("precio", libro.getPrecio());
            intent.putExtra("stock", libro.getStock());
            intent.putExtra("tema", libro.getTema());
            intent.putExtra("libroId", libro.getId());
            intent.putExtra("esPopular", libro.isEsPopular());

            // Iniciar la actividad correspondiente
            v.getContext().startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return librosList.size();
    }

    // ViewHolder para los elementos del RecyclerView
    public static class LibroViewHolder extends RecyclerView.ViewHolder {
        TextView tituloTextView, autorTextView, precioTextView; // Añadido el precio
        ImageView imagenImageView;

        public LibroViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloTextView = itemView.findViewById(R.id.titulo);
            autorTextView = itemView.findViewById(R.id.autor);
            imagenImageView = itemView.findViewById(R.id.imageViewLibro);
            precioTextView = itemView.findViewById(R.id.precio); // Inicializando el precio
        }
    }


    // Interfaz para manejar clics en los elementos
    public interface OnItemClickListener {
        void onItemClick(Libro libro);
    }

    // En el adaptador
    public void actualizarLibro(Libro libroActualizado) {
        for (int i = 0; i < librosList.size(); i++) {
            if (librosList.get(i).getId() == libroActualizado.getId()) {
                librosList.set(i, libroActualizado);
                notifyItemChanged(i);  // Notificar al adaptador que un solo item ha cambiado
                break;
            }
        }
    }



    // Método para actualizar la lista de libros y notificar al adaptador
    public void actualizarLista(List<Libro> nuevosLibros) {
        this.librosList = nuevosLibros;
        notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }
}
