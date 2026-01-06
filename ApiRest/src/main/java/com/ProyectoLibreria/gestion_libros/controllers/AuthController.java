package com.ProyectoLibreria.gestion_libros.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ProyectoLibreria.gestion_libros.modelos.LoginRequest;
import com.ProyectoLibreria.gestion_libros.modelos.Usuario;
import com.ProyectoLibreria.gestion_libros.servicios.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Autenticar el usuario con nombre de usuario y contraseña
        Usuario usuario = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        // Devolver el objeto Usuario completo, incluyendo el rol
        return ResponseEntity.ok(usuario);
    }
}


