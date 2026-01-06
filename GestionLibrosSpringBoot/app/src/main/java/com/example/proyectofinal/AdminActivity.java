package com.example.proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectofinal.Clases.ApiService;
import com.example.proyectofinal.Clases.Libro;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AdminActivity extends AppCompatActivity {

    private static final int REQUEST_EDITAR_LIBRO = 1;
    private static final int REQUEST_AGREGAR_LIBRO = 2;
    private static final int REQUEST_CODE_EDITAR = 3;

    private RecyclerView recyclerView;
    private LibroAdapter libroAdapter;
    private ApiService apiService;
    private List<Libro> listaLibros;
    private boolean isAdmin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        getSupportActionBar().setTitle("Administrador");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        recyclerView = findViewById(R.id.recyclerViewLibros);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Retrofit retrofit = ConexionApi.getConexionApi();
        apiService = retrofit.create(ApiService.class);

        cargarLibros();
    }

    //Actualiza la lista
    @Override
    protected void onResume() {
        super.onResume();
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
                    Toast.makeText(AdminActivity.this, "Error al cargar los libros", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Libro>> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void configurarAdaptador() {
        LibroAdapter.OnItemClickListener listener = libro -> {
            long libroId = libro.getId();
            if (libroId <= 0) {
                Toast.makeText(AdminActivity.this, "ID de libro no válido", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(AdminActivity.this, EditarActivity.class);
            intent.putExtra("libroId", libroId);
            intent.putExtra("titulo", libro.getTitulo());
            intent.putExtra("autor", libro.getAutor());
            intent.putExtra("precio", libro.getPrecio());
            intent.putExtra("stock", libro.getStock());
            intent.putExtra("imagenUrl", libro.getImagenUrl());
            startActivityForResult(intent, REQUEST_CODE_EDITAR);  // Aquí se inicia la actividad de edición
        };

        libroAdapter = new LibroAdapter(listaLibros, listener, isAdmin);
        recyclerView.setAdapter(libroAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            boolean actualizado = data.getBooleanExtra("ACTUALIZADO", true);
            if (actualizado) {
                cargarLibros();
            }
        }
    }




    // Inflar el menú en la Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    // Manejar las opciones del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_add_book) {
            Intent intent = new Intent(AdminActivity.this, AgregarActivity.class);
            startActivityForResult(intent, REQUEST_AGREGAR_LIBRO);
            return true;

        } else if (itemId == R.id.action_view_reservations) {
            // Aquí abrimos la actividad de ver reservas
            Intent intent = new Intent(AdminActivity.this, reservasAdmin.class);
            startActivity(intent);
            return true;

        }else if (itemId == R.id.action_popular) {
            // Acción del ítem "Popular" (redirigir a PopularActivity)
            Intent intent = new Intent(AdminActivity.this, popularesActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_logout) {
            Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
