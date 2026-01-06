package com.ProyectoLibreria.gestion_libros.controllers;

import com.ProyectoLibreria.gestion_libros.Repositorio.ReservaRepositorio;
import com.ProyectoLibreria.gestion_libros.modelos.Reserva;
import com.ProyectoLibreria.gestion_libros.servicios.ServicioReserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
public class ControladorReservas {

    @Autowired
    private ServicioReserva servicioReserva;
    @Autowired
    private ReservaRepositorio reservaRepositorio;  // Inyección del repositorio

    // Obtener todas las reservas
    @GetMapping
    public ResponseEntity<?> getAllReservas() {
        try {
            List<Reserva> reservas = servicioReserva.getAllReservas();
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener las reservas: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorUsuario(@PathVariable long usuarioId) {
        List<Reserva> reservas = reservaRepositorio.findByUsuarioId(usuarioId);
        if (reservas != null && !reservas.isEmpty()) {
            return ResponseEntity.ok(reservas);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    // Obtener una reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservaById(@PathVariable Long id) {
        try {
            Optional<Reserva> reserva = servicioReserva.getReservaById(id);
            if (reserva.isPresent()) {
                return ResponseEntity.ok(reserva.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Reserva con ID " + id + " no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener la reserva: " + e.getMessage());
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<Reserva> crearReserva(@RequestBody Reserva reserva) {
        // Verificar que los objetos Usuario y Libro sean no nulos
        if (reserva.getUsuario() == null || reserva.getLibro() == null) {
            return ResponseEntity.badRequest().body(null); // Los objetos no pueden ser nulos
        }

        // Guardar la reserva con los objetos completos de Usuario y Libro
        reservaRepositorio.save(reserva);

        return ResponseEntity.ok(reserva);
    }

    // Actualizar una reserva
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReserva(@PathVariable Long id, @RequestBody Reserva reservaDetails) {
        try {
            Reserva updatedReserva = servicioReserva.updateReserva(id, reservaDetails);
            if (updatedReserva != null) {
                return ResponseEntity.ok(updatedReserva);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Reserva con ID " + id + " no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la reserva: " + e.getMessage());
        }
    }

    // Eliminar una reserva
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReserva(@PathVariable Long id) {
        try {
            if (servicioReserva.deleteReserva(id)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Reserva con ID " + id + " no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la reserva: " + e.getMessage());
        }
    }
}
