package com.ProyectoLibreria.gestion_libros.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoLibreria.gestion_libros.modelos.Libro;

import java.util.List;

@Repository
public interface LibroRepositorio extends JpaRepository<Libro, Long> {
    List<Libro> findByAutor(String autor);
    List<Libro> findByTema(String tema);
    List<Libro> findByEsPopularTrue();
}
