package com.ProyectoLibreria.gestion_libros.servicios;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ProyectoLibreria.gestion_libros.Repositorio.UsuarioRepositorio;
import com.ProyectoLibreria.gestion_libros.modelos.Usuario;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    public Usuario authenticate(String username, String password) {
        Usuario usuario = usuarioRepositorio.findByUsername(username);
        if (usuario != null && usuario.getPassword().equals(password)) {
            return usuario;
        }
        return null; // Credenciales incorrectas
    }
}
