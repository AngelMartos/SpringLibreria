package com.ProyectoLibreria.gestion_libros.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ProyectoLibreria.gestion_libros.modelos.Libro;
import com.ProyectoLibreria.gestion_libros.servicios.ServicioLibro;
import com.ProyectoLibreria.gestion_libros.Repositorio.LibroRepositorio;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/libros")
public class ControladorLibros {

    @Autowired
    private ServicioLibro servicioLibro;

    @Autowired
    private LibroRepositorio libroRepositorio;

    // Método para obtener todos los libros
    @GetMapping
    public ResponseEntity<?> obtenerTodosLosLibros() {
        try {
            List<Libro> libros = servicioLibro.getAllLibros();
            return ResponseEntity.ok(libros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener la lista de libros: " + e.getMessage());
        }
    }

    @GetMapping("/populares")
    public ResponseEntity<?> obtenerLibrosPopulares() {
        try {
            List<Libro> librosPopulares = libroRepositorio.findByEsPopularTrue();  
            if (librosPopulares.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontraron libros populares.");
            }
            return ResponseEntity.ok(librosPopulares);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener la lista de libros populares: " + e.getMessage());
        }
    }
    // Método para agregar un nuevo libro
    @PostMapping
    public ResponseEntity<?> agregarLibro(@RequestBody Libro libro) {
        try {
            Libro nuevoLibro = servicioLibro.createLibro(libro);
            return new ResponseEntity<>(nuevoLibro, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al agregar el libro: " + e.getMessage());
        }
    }

    // Método para obtener un libro por su ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerLibroPorId(@PathVariable("id") Long id) {
        try {
            Optional<Libro> libro = obtenerLibro(id); // Usamos el método privado para obtener el libro
            if (libro.isPresent()) {
                return ResponseEntity.ok(libro.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Libro con ID " + id + " no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener el libro: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Libro> actualizarLibro(@PathVariable Long id, @RequestBody Libro libro) {
        Optional<Libro> libroExistente = libroRepositorio.findById(id);
        if (libroExistente.isPresent()) {
            Libro libroActualizado = libroExistente.get();
            
            // Actualizar los campos con los valores del libro recibido en el cuerpo
            libroActualizado.setTitulo(libro.getTitulo());
            libroActualizado.setAutor(libro.getAutor());
            libroActualizado.setPrecio(libro.getPrecio());
            libroActualizado.setStock(libro.getStock());
            libroActualizado.setImagenUrl(libro.getImagenUrl());
            libroActualizado.setEsPopular(libro.isEsPopular()); // Añadir esta línea

            // Guardar el libro actualizado
            libroRepositorio.save(libroActualizado);

            return ResponseEntity.ok(libroActualizado);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLibro(@PathVariable("id") Long id) {
        boolean eliminado = servicioLibro.deleteLibro(id);
        if (eliminado) {
            return ResponseEntity.noContent().build(); // Respuesta exitosa sin contenido
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: No se encontró el libro con ID " + id);
        }
    }


    // Método para asociar una imagen a un libro
    @PutMapping("/{id}/imagen")
    public ResponseEntity<?> asociarImagenLibro(@PathVariable("id") Long id, @RequestParam("imagenUrl") String imagenUrl) {
        try {
            Optional<Libro> libroExistente = obtenerLibro(id); // Usamos el método privado para obtener el libro
            if (!libroExistente.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Libro con ID " + id + " no encontrado");
            }

            if (imagenUrl == null || imagenUrl.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("La URL de la imagen no puede estar vacía.");
            }

            Libro libro = libroExistente.get();
            libro.setImagenUrl(imagenUrl);
            servicioLibro.updateLibro(libro); // Guardamos los cambios en la base de datos
            return ResponseEntity.ok("Imagen asociada exitosamente: " + imagenUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al asociar la imagen del libro: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable("id") Long id, @RequestBody int cantidadReservada) {
        try {
            Optional<Libro> libroExistente = obtenerLibro(id);
            if (!libroExistente.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Libro con ID " + id + " no encontrado");
            }

            Libro libro = libroExistente.get();

            // Verificamos que hay suficiente stock disponible
            if (libro.getStock() < cantidadReservada) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Stock insuficiente. Disponible: " + libro.getStock());
            }

            // Restamos la cantidad reservada del stock actual
            libro.setStock(libro.getStock() - cantidadReservada);
            libroRepositorio.save(libro); // Guardamos el libro actualizado

            return ResponseEntity.ok("Stock actualizado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el stock del libro: " + e.getMessage());
        }
    }

    // Método privado para obtener un libro por ID
    private Optional<Libro> obtenerLibro(Long id) {
        return libroRepositorio.findById(id);
    }
}