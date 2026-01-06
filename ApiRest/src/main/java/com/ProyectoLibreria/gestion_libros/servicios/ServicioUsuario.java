package com.ProyectoLibreria.gestion_libros.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ProyectoLibreria.gestion_libros.Repositorio.UsuarioRepositorio;
import com.ProyectoLibreria.gestion_libros.modelos.Role;
import com.ProyectoLibreria.gestion_libros.modelos.Usuario;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioUsuario {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    // Método para autenticar al usuario
    public Optional<Usuario> autenticar(String username, String password) {
        Usuario usuario = usuarioRepositorio.findByUsername(username);

        // Verificamos si el usuario existe y si la contraseña coincide
        if (usuario != null && usuario.getPassword().equals(password)) {
            return Optional.of(usuario); // Autenticación exitosa
        }

        return Optional.empty(); // Autenticación fallida
    }

    // Método para registrar un nuevo usuario
    public Usuario registrarUsuario(Usuario nuevoUsuario) {
        // Verificar si el usuario ya existe
        Usuario usuarioExistente = usuarioRepositorio.findByUsername(nuevoUsuario.getUsername());
        if (usuarioExistente != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario ya existe");
        }

        // Asignar rol CLIENTE
        nuevoUsuario.setRole(Role.CLIENTE);  // Asignar el rol como CLIENTE
        return usuarioRepositorio.save(nuevoUsuario);
    }

    // Método para obtener un usuario por ID
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepositorio.findById(id);
    }

    // Método para obtener todos los usuarios (opcional)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepositorio.findAll();
    }
}

