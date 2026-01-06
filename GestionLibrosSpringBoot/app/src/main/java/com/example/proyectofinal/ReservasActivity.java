package com.example.proyectofinal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectofinal.Clases.ApiService;
import com.example.proyectofinal.Clases.Libro;
import com.example.proyectofinal.Clases.Reserva;
import com.example.proyectofinal.Clases.ReservasResponse;
import com.example.proyectofinal.Clases.Usuario;
import com.example.proyectofinal.ConexionApi;
import com.example.proyectofinal.R;
import com.example.proyectofinal.ReservaAdapter;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReservasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReservaAdapter reservaAdapter;
    private ApiService apiService;
    private List<Reserva> listaReservas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservas);

        getSupportActionBar().setTitle("Reservas");


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_revert);
        }



        recyclerView = findViewById(R.id.reservasRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener el objeto Usuario completo de las SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuario", null);  // Obtener el objeto JSON guardado

        if (usuarioJson != null) {
            // Convertir el objeto JSON de vuelta a un objeto Usuario
            Gson gson = new Gson();
            Usuario usuario = gson.fromJson(usuarioJson, Usuario.class);

            // Si el objeto Usuario se carga correctamente, obtener sus reservas
            if (usuario != null) {
                // Configuración Retrofit
                Retrofit retrofit = ConexionApi.getConexionApi();
                apiService = retrofit.create(ApiService.class);

                obtenerReservas(usuario.getId());  // Usar el usuarioId del objeto Usuario
            }
        } else {
            Toast.makeText(this, "No se ha encontrado el usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private void obtenerReservas(long usuarioId) {
        // Hacer la solicitud para obtener las reservas del usuario
        Call<List<Reserva>> call = apiService.obtenerReservasPorUsuario(usuarioId);
        call.enqueue(new Callback<List<Reserva>>() {
            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaReservas = response.body();

                    // Calcular el precio de cada reserva y añadirlo al adaptador
                    for (Reserva reserva : listaReservas) {
                        Libro libro = reserva.getLibro();
                        int cantidad = reserva.getCantidad();
                        double precioTotal = libro.getPrecio() * cantidad;
                        reserva.setPrecioTotal(precioTotal);  // Asumiendo que tienes un setter en Reserva para el precio total
                    }

                    // Configurar el adaptador con la lista de reservas actualizada
                    configurarAdaptador();
                } else {
                    // Si la respuesta no es exitosa, loguea el código y el cuerpo de error
                    try {
                        String errorResponse = response.errorBody() != null ? response.errorBody().string() : "Respuesta vacía";
                        Log.e("API_ERROR", "Error al cargar las reservas: " + errorResponse);
                    } catch (IOException e) {
                        Log.e("API_ERROR", "Error al leer la respuesta de error: " + e.getMessage());
                    }
                    Toast.makeText(ReservasActivity.this, "Error al cargar las reservas: " + response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {
                // En caso de fallo de conexión, loguea el error completo
                Log.e("API_ERROR", "Fallo de conexión: " + t.getMessage(), t);
                Toast.makeText(ReservasActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void calcularYMostrarPrecioTotal() {
        // Iterar sobre las reservas recibidas
        for (Reserva reserva : listaReservas) {
            // Obtener el libro y la cantidad
            Libro libro = reserva.getLibro();
            int cantidad = reserva.getCantidad();

            // Verificar si el libro tiene un precio válido
            if (libro != null && libro.getPrecio() > 0) {
                double precioLibro = libro.getPrecio(); // Obtener el precio del libro
                double precioTotal = precioLibro * cantidad; // Calcular el precio total

                // Mostrar el precio total en un Toast (puedes usar otro componente como un TextView)
                String mensajePrecioTotal = "Precio total para el libro '" + libro.getTitulo() + "': " + precioTotal + " unidades";
                Log.d("PrecioTotal", mensajePrecioTotal);
                Toast.makeText(ReservasActivity.this, mensajePrecioTotal, Toast.LENGTH_LONG).show();
            } else {
                // Si el libro no tiene precio válido, mostrar un mensaje de error
                Toast.makeText(ReservasActivity.this, "El libro no tiene un precio válido", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home || id == R.id.action_back) { // ID del icono de navegación y el botón de menú
            // Volver a ClienteActivity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void configurarAdaptador() {
        // Configurar el adaptador para mostrar las reservas
        reservaAdapter = new ReservaAdapter(listaReservas);
        recyclerView.setAdapter(reservaAdapter);
    }
}
