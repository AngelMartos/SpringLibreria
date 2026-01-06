package com.example.proyectofinal;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectofinal.Clases.ApiService;
import com.example.proyectofinal.Clases.Libro;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class popularesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LibroAdapter libroAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_populares);

        getSupportActionBar().setTitle("Libros Populares");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPopulares);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener los libros populares de la API
        obtenerLibrosPopulares();
    }

    private void obtenerLibrosPopulares() {
        ApiService apiService = ConexionApi.getApiService();
        Call<List<Libro>> call = apiService.obtenerLibrosPopulares();

        call.enqueue(new Callback<List<Libro>>() {
            @Override
            public void onResponse(Call<List<Libro>> call, Response<List<Libro>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Establecer los libros populares en el adaptador
                    libroAdapter = new LibroAdapter(response.body(), null, false); // Cambia según el uso de isAdmin
                    recyclerView.setAdapter(libroAdapter);
                } else {
                    Toast.makeText(popularesActivity.this, "No se encontraron libros populares", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Libro>> call, Throwable t) {
                Toast.makeText(popularesActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
