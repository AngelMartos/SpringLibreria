package com.ProyectoLibreria.gestion_libros.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ProyectoLibreria.gestion_libros.Repositorio.LibroRepositorio;
import com.ProyectoLibreria.gestion_libros.modelos.Libro;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioLibro {

    @Autowired
    private LibroRepositorio libroRepositorio;

    // Obtener todos los libros
    public List<Libro> getAllLibros() {
        return libroRepositorio.findAll();
    }

    public Libro getLibroById(Long id) {
        return libroRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Libro no encontrado"));
    }


    // Crear un nuevo libro
    public Libro createLibro(Libro libro) {
        return libroRepositorio.save(libro);
    }

    // Eliminar un libro por su ID
    public boolean deleteLibro(Long id) {
        if (libroRepositorio.existsById(id)) {
            libroRepositorio.deleteById(id);
            return true; // Libro eliminado con éxito
        }
        return false; // Libro no encontrado
    }

    // Actualizar el stock de un libro
    public void actualizarStock(long libroId, int nuevoStock) {
        Libro libro = libroRepositorio.findById(libroId).orElseThrow(() -> new RuntimeException("Libro no encontrado")); // Lanza una excepción si no se encuentra
        libro.setStock(nuevoStock);
        libroRepositorio.save(libro); // Guardamos el libro con el nuevo stock
    }

    public Libro updateLibro(Libro libro) {
        return libroRepositorio.findById(libro.getId())
                .map(libroExistente -> {
                    // Actualizar los campos con los valores del libro recibido
                    libroExistente.setTitulo(libro.getTitulo());
                    libroExistente.setAutor(libro.getAutor());
                    libroExistente.setPrecio(libro.getPrecio());
                    libroExistente.setStock(libro.getStock());
                    libroExistente.setImagenUrl(libro.getImagenUrl());
                    return libroRepositorio.save(libroExistente);
                })
                .orElseThrow(() -> new RuntimeException("Libro no encontrado para actualizar"));
    }


}
