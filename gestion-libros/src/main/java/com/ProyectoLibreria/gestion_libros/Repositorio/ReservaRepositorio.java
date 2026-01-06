package com.ProyectoLibreria.gestion_libros.Repositorio;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoLibreria.gestion_libros.modelos.Reserva;

import java.util.List;

@Repository
public interface ReservaRepositorio extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuarioId(Long usuarioId); 
    List<Reserva> findByLibroId(Long libroId);
    
}
