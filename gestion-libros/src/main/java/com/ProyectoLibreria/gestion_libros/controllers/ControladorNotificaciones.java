package com.ProyectoLibreria.gestion_libros.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ProyectoLibreria.gestion_libros.modelos.Notificacion;
import com.ProyectoLibreria.gestion_libros.servicios.ServicioNotificacion;

@RestController
@RequestMapping("/notificaciones")
public class ControladorNotificaciones {

    private final ServicioNotificacion servicioNotificacion;

    @Autowired
    public ControladorNotificaciones(ServicioNotificacion servicioNotificacion) {
        this.servicioNotificacion = servicioNotificacion;
    }

    // Obtener todas las notificaciones
    @GetMapping
    public ResponseEntity<?> obtenerNotificaciones() {
        try {
            List<Notificacion> notificaciones = servicioNotificacion.obtenerNotificaciones();
            return ResponseEntity.ok(notificaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener las notificaciones: " + e.getMessage());
        }
    }

    // Crear una nueva notificación
    @PostMapping
    public ResponseEntity<?> crearNotificacion(@RequestBody Notificacion notificacion) {
        try {
            Notificacion nuevaNotificacion = servicioNotificacion.crearNotificacion(notificacion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaNotificacion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear la notificación: " + e.getMessage());
        }
    }

    // Marcar notificación como leída
    @PutMapping("/{id}/marcar-leida")
    public ResponseEntity<?> marcarComoLeida(@PathVariable Long id) {
        try {
            Notificacion notificacionActualizada = servicioNotificacion.marcarComoLeida(id);
            if (notificacionActualizada != null) {
                return ResponseEntity.ok(notificacionActualizada);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Notificación con ID " + id + " no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al marcar la notificación como leída: " + e.getMessage());
        }
    }
}

