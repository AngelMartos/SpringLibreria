package com.example.proyectofinal.Clases;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/api/libros")
    Call<List<Libro>> obtenerLibros();

    @GET("/api/libros/{id}")
    Call<Libro> obtenerLibroPorId(@Path("id") long libroId);



    @POST("/api/libros")
    Call<Libro> agregarLibro(@Body Libro libro);










    @DELETE("api/libros/{id}")
    Call<Void> eliminarLibro(@Path("id") Long id);

    @PUT("api/libros/{id}")
    Call<Libro> actualizarLibro(@Path("id") Long id, @Body Libro libro);








    @POST("/api/reservas/crear")
    Call<Reserva> crearReserva(@Body Reserva reserva);

    @GET("api/reservas")
    Call<List<Reserva>> getAllReservas();

        @GET("api/reservas/usuario/{usuarioId}")
        Call<List<Reserva>> obtenerReservasPorUsuario(@Path("usuarioId") long usuarioId);





    @PUT("api/libros/{id}/stock")
    Call<Void> actualizarStock(@Path("id") Long id, @Body Integer nuevoStock);



        @GET("api/libros/populares")
        Call<List<Libro>> obtenerLibrosPopulares();



    @POST("/api/auth/login")
    Call<Usuario> login(@Body Usuario loginRequest);

    @POST("usuarios/registro")
    Call<Usuario> registrarUsuario(@Body Usuario nuevoUsuario);


}



