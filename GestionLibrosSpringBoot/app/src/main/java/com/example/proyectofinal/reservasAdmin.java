package com.example.proyectofinal;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectofinal.Clases.ApiService;
import com.example.proyectofinal.Clases.Libro;
import com.example.proyectofinal.Clases.Reserva;
import com.example.proyectofinal.R;
import com.example.proyectofinal.ReservaAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class reservasAdmin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReservaAdapter reservaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservas_admin);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewReservas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener las reservas de la API
        obtenerReservas();
    }

    private void obtenerReservas() {
        ApiService apiService = ConexionApi.getApiService();
        Call<List<Reserva>> call = apiService.getAllReservas();

        call.enqueue(new Callback<List<Reserva>>() {
            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Iterar sobre las reservas recibidas y calcular el precio total
                    List<Reserva> reservas = response.body();
                    for (Reserva reserva : reservas) {
                        Libro libro = reserva.getLibro();
                        int cantidad = reserva.getCantidad();

                        // Calcular el precio total
                        if (libro != null && libro.getPrecio() > 0) {
                            double precioTotal = libro.getPrecio() * cantidad;
                            reserva.setPrecioTotal(precioTotal);  // Asume que tienes un setter para el precio total en la clase Reserva
                        }
                    }

                    // Establecer las reservas en el adaptador
                    reservaAdapter = new ReservaAdapter(reservas);
                    recyclerView.setAdapter(reservaAdapter);
                } else {
                    Toast.makeText(reservasAdmin.this, "Error al obtener reservas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {
                Toast.makeText(reservasAdmin.this, "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Regresa a la actividad anterior cuando se toca la flecha
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
