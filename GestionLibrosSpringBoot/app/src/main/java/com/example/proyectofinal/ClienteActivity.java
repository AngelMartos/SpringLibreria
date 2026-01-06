package com.example.proyectofinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectofinal.Clases.ApiService;
import com.example.proyectofinal.Clases.Libro;
import com.example.proyectofinal.Clases.Usuario;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ClienteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LibroAdapter libroAdapter;
    private ApiService apiService;
    private List<Libro> listaLibros;
    private boolean isAdmin;
    private Button reservaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        //Que salga el Usuario
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuario", null);

        if (usuarioJson != null) {
            Gson gson = new Gson();
            Usuario loggedInUser = gson.fromJson(usuarioJson, Usuario.class);

            getSupportActionBar().setTitle(loggedInUser.getUsername());
        }


        reservaButton = findViewById(R.id.reservaButton);

        reservaButton.setOnClickListener(view -> {
            Intent intent = new Intent(ClienteActivity.this, ReservasActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerViewLibros);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        isAdmin = getIntent().getBooleanExtra("isAdmin", false); // Verifica si es admin

        Retrofit retrofit = ConexionApi.getConexionApi();
        apiService = retrofit.create(ApiService.class);

        cargarLibros();
    }

    private void cargarLibros() {
        Call<List<Libro>> call = apiService.obtenerLibros();
        call.enqueue(new Callback<List<Libro>>() {
            @Override
            public void onResponse(Call<List<Libro>> call, Response<List<Libro>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaLibros = response.body();
                    configurarAdaptador();
                } else {
                    Toast.makeText(ClienteActivity.this, "Error al cargar los libros", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Libro>> call, Throwable t) {
                Toast.makeText(ClienteActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void configurarAdaptador() {
        LibroAdapter.OnItemClickListener listener = libro -> {
            // Si es un administrador, se abre la actividad de editar, de lo contrario, solo se muestran los detalles
            Intent intent;
            if (isAdmin) {
                intent = new Intent(ClienteActivity.this, EditarActivity.class);
            } else {
                intent = new Intent(ClienteActivity.this, DetalleLibroActivity.class);
            }
            intent.putExtra("libroId", libro.getId());
            intent.putExtra("titulo", libro.getTitulo());
            intent.putExtra("autor", libro.getAutor());
            intent.putExtra("precio", libro.getPrecio());
            intent.putExtra("stock", libro.getStock());
            intent.putExtra("imagenUrl", libro.getImagenUrl());
            startActivity(intent);
        };

        libroAdapter = new LibroAdapter(listaLibros, listener, isAdmin);
        recyclerView.setAdapter(libroAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem backItem = menu.findItem(R.id.action_back);
        if (backItem != null) {
            backItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_logout) {
            // Acción de cerrar sesión
            Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();

            // Navegar a la LoginActivity y finalizar la actividad actual
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Finaliza la actividad actual
            return true;

        } else if (itemId == R.id.action_carrito) {
            // Acción del carrito (redirigir a ReservasActivity)
            Intent intent = new Intent(ClienteActivity.this, ReservasActivity.class);
            startActivity(intent);
            return true;

        } else if (itemId == R.id.action_popular) {
            // Acción del ítem "Popular" (redirigir a PopularActivity)
            Intent intent = new Intent(ClienteActivity.this, popularesActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}