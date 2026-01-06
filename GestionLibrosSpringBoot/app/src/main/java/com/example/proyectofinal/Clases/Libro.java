package com.example.proyectofinal.Clases;

import java.io.Serializable;

public class Libro implements Serializable {
    private long id;
    private String titulo;
    private String autor;
    private String tema;
    private int stock;
    private double precio;
    private boolean esPopular;
    private String imagenUrl;

    public Libro(String titulo, String autor, double precio, int stock, String imagenUrl) {
        this.titulo = titulo;
        this.autor = autor;
        this.precio = precio;
        this.stock = stock;
        this.imagenUrl = imagenUrl;
    }

    // Constructor con id (si también necesitas el id al actualizar un libro)
    public Libro(long id, String titulo, String autor, double precio, int stock, String imagenUrl) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.precio = precio;
        this.stock = stock;
        this.imagenUrl = imagenUrl;
    }

    public Libro() {

    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isEsPopular() {
        return esPopular;
    }

    public void setEsPopular(boolean esPopular) {
        this.esPopular = esPopular;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}
