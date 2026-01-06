package com.ProyectoLibreria.gestion_libros.controllers;

import com.ProyectoLibreria.gestion_libros.modelos.Role;
import com.ProyectoLibreria.gestion_libros.modelos.Usuario;
import com.ProyectoLibreria.gestion_libros.servicios.ServicioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class ControladorUsuarios {

    private final ServicioUsuario servicioUsuario;

    @Autowired
    public ControladorUsuarios(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    // Endpoint para autenticar un usuario
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        try {
            Optional<Usuario> usuarioAutenticado = servicioUsuario.autenticar(usuario.getUsername(), usuario.getPassword());

            if (usuarioAutenticado.isPresent()) {
                return ResponseEntity.ok("Inicio de sesión exitoso, rol: " + usuarioAutenticado.get().getRole());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en el inicio de sesión: " + e.getMessage());
        }
    }

    // Endpoint para registrar un nuevo usuario
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        try {
            // Asignar el rol CLIENTE
            nuevoUsuario.setRole(Role.CLIENTE);  // Asegurarse de asignar el rol como CLIENTE

            Usuario usuarioGuardado = servicioUsuario.registrarUsuario(nuevoUsuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al registrar el usuario: " + e.getMessage());
        }
    }

    // Endpoint para obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Long id) {
        try {
            Optional<Usuario> usuario = servicioUsuario.obtenerUsuarioPorId(id);

            if (usuario.isPresent()) {
                return ResponseEntity.ok(usuario.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario con ID " + id + " no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener el usuario: " + e.getMessage());
        }
    }

    // Endpoint para obtener todos los usuarios (opcional)
    @GetMapping
    public ResponseEntity<?> obtenerTodosLosUsuarios() {
        try {
            java.util.List<Usuario> usuarios = servicioUsuario.obtenerTodosLosUsuarios();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener la lista de usuarios: " + e.getMessage());
        }
    }
}

