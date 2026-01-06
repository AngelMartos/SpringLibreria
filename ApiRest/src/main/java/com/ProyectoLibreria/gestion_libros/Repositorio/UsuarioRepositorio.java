package com.ProyectoLibreria.gestion_libros.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoLibreria.gestion_libros.modelos.Usuario;
import com.ProyectoLibreria.gestion_libros.modelos.Role; // Asegúrate de importar el enum

import java.util.List;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Usuario findByUsername(String username);
    
    // Cambia String por Role
    List<Usuario> findByRole(Role role);
}
