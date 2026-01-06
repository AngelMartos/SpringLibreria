package com.ProyectoLibreria.gestion_libros.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ProyectoLibreria.gestion_libros.Repositorio.NotificacionRepositorio;
import com.ProyectoLibreria.gestion_libros.modelos.Notificacion;

import java.util.List;

@Service
public class ServicioNotificacion {

    @Autowired
    private NotificacionRepositorio notificacionRepositorio;

    // Crear una nueva notificación
    public Notificacion crearNotificacion(Notificacion notificacion) {
        return notificacionRepositorio.save(notificacion);
    }

    // Obtener todas las notificaciones
    public List<Notificacion> obtenerNotificaciones() {
        return notificacionRepositorio.findAll();
    }

    // Obtener notificaciones por usuario
    public List<Notificacion> obtenerNotificacionesPorUsuario(Long usuarioId) {
        return notificacionRepositorio.findByUsuarioId(usuarioId);
    }

    // Marcar notificación como leída
    public Notificacion marcarComoLeida(Long id) {
        Notificacion notificacion = notificacionRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notificacion.setLeido(true);
        return notificacionRepositorio.save(notificacion);
    }

    // Eliminar una notificación
    public boolean eliminarNotificacion(Long id) {
        if (notificacionRepositorio.existsById(id)) {
            notificacionRepositorio.deleteById(id);
            return true; // Retorna true si la eliminación fue exitosa
        }
        return false; // Retorna false si no se encontró la notificación
    }
}

