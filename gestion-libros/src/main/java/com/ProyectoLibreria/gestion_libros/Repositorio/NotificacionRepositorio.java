package com.ProyectoLibreria.gestion_libros.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ProyectoLibreria.gestion_libros.modelos.Notificacion;

import java.util.List;

public interface NotificacionRepositorio extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioId(Long usuarioId);
}
