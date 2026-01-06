package com.example.proyectofinal.Clases;

import java.io.Serializable;

public class Reserva implements Serializable {

    private Long id;
    private long libroid;

    private double precioTotal;



    private long usuarioid;
    private Libro libro;  // Ahora es un objeto Libro completo
    private Usuario usuario;  // Ahora es un objeto Usuario completo
    private int cantidad;

    public Reserva(Libro libro, Usuario usuario, int cantidad) {
        this.libro = libro;
        this.usuario = usuario;
        this.cantidad = cantidad;
    }

    public Reserva() {

    }

    // Getters y Setters
    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public long getLibroId() {
        return libroid;
    }

    public long getUsuarioId() {
        return usuarioid;
    }


}
