package com.example.proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectofinal.Clases.ApiService;
import com.example.proyectofinal.Clases.Libro;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditarActivity extends AppCompatActivity {

    private EditText edtTitulo, edtAutor, edtPrecio, edtStock, edtImagenUrl;
    private Button btnGuardar, btnEliminar;
    private CheckBox checkBoxEsPopular; // Nueva variable para la CheckBox
    private ApiService apiService;
    private Long libroId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);
        getSupportActionBar().setTitle("Editar");

        // Vincular vistas con sus IDs
        edtTitulo = findViewById(R.id.edtTitulo);
        edtAutor = findViewById(R.id.edtAutor);
        edtPrecio = findViewById(R.id.edtPrecio);
        edtStock = findViewById(R.id.edtStock);
        edtImagenUrl = findViewById(R.id.edtImagenUrl);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnEliminar = findViewById(R.id.btnEliminar);
        checkBoxEsPopular = findViewById(R.id.checkBoxEsPopular); // Vincular la CheckBox

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Crear instancia de Retrofit y ApiService
        Retrofit retrofit = ConexionApi.getConexionApi();
        apiService = retrofit.create(ApiService.class);

        // Obtener datos del libro desde el Intent
        Intent intent = getIntent();
        if (intent != null) {
            libroId = intent.getLongExtra("libroId", -1);
            if (libroId == -1) {
                Toast.makeText(this, "ID del libro no válido", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            String titulo = intent.getStringExtra("titulo");
            String autor = intent.getStringExtra("autor");
            double precio = intent.getDoubleExtra("precio", 0.0);
            int stock = intent.getIntExtra("stock", 0);
            String imagenUrl = intent.getStringExtra("imagenUrl");
            boolean esPopular = intent.getBooleanExtra("esPopular", false); // Obtener el valor de es_popular

            // Mostrar los datos actuales en los campos
            edtTitulo.setText(titulo);
            edtAutor.setText(autor);
            edtPrecio.setText(String.valueOf(precio));
            edtStock.setText(String.valueOf(stock));
            edtImagenUrl.setText(imagenUrl);
            checkBoxEsPopular.setChecked(esPopular); // Establecer el valor de es_popular en la CheckBox
        }

        // Botón Guardar
        btnGuardar.setOnClickListener(view -> guardarCambios());

        // Botón Eliminar
        btnEliminar.setOnClickListener(view -> eliminarLibro());
    }

    private void guardarCambios() {
        String nuevoTitulo = edtTitulo.getText().toString().trim();
        String nuevoAutor = edtAutor.getText().toString().trim();
        String nuevoPrecioStr = edtPrecio.getText().toString().trim();
        String nuevoStockStr = edtStock.getText().toString().trim();
        String nuevaImagenUrl = edtImagenUrl.getText().toString().trim();
        boolean esPopular = checkBoxEsPopular.isChecked(); // Obtener el estado de la CheckBox

        if (nuevoTitulo.isEmpty() || nuevoAutor.isEmpty() || nuevoPrecioStr.isEmpty() || nuevoStockStr.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double nuevoPrecio = 0;
        int nuevoStock = 0;

        try {
            nuevoPrecio = Double.parseDouble(nuevoPrecioStr);
            nuevoStock = Integer.parseInt(nuevoStockStr);
        } catch (NumberFormatException e) {
            Toast.makeText(EditarActivity.this, "Precio o stock inválidos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (libroId == null || libroId <= 0) {
            Toast.makeText(this, "ID del libro no configurado correctamente", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el objeto libro actualizado
        Libro libroActualizado = new Libro();
        libroActualizado.setTitulo(nuevoTitulo);
        libroActualizado.setAutor(nuevoAutor);
        libroActualizado.setPrecio(nuevoPrecio);
        libroActualizado.setStock(nuevoStock);
        libroActualizado.setImagenUrl(nuevaImagenUrl);
        libroActualizado.setEsPopular(esPopular); // Establecer es_popular

        // Llamada a la API para actualizar el libro
        Call<Libro> call = apiService.actualizarLibro(libroId, libroActualizado);
        call.enqueue(new Callback<Libro>() {
            @Override
            public void onResponse(Call<Libro> call, Response<Libro> response) {
                if (response.isSuccessful()) {
                    Libro libroActualizado = response.body();

                    if (libroActualizado != null) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("ACTUALIZADO", true);
                        resultIntent.putExtra("libroId", libroActualizado.getId());
                        resultIntent.putExtra("titulo", libroActualizado.getTitulo());
                        resultIntent.putExtra("autor", libroActualizado.getAutor());
                        resultIntent.putExtra("precio", libroActualizado.getPrecio());
                        resultIntent.putExtra("stock", libroActualizado.getStock());
                        resultIntent.putExtra("imagenUrl", libroActualizado.getImagenUrl());
                        resultIntent.putExtra("esPopular", libroActualizado.isEsPopular()); // Incluir es_popular
                        setResult(RESULT_OK, resultIntent);
                        finish();

                        Toast.makeText(EditarActivity.this, "Libro actualizado correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditarActivity.this, "Error: El libro actualizado es nulo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditarActivity.this, "Error al guardar los cambios: " + response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Libro> call, Throwable t) {
                Toast.makeText(EditarActivity.this, "Error de red o servidor: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void eliminarLibro() {
        new android.app.AlertDialog.Builder(this)
                .setMessage("¿Estás seguro de que quieres eliminar este libro?")
                .setCancelable(false)
                .setPositiveButton("Sí", (dialog, id) -> {
                    Log.d("EliminarLibro", "ID del libro: " + libroId);

                    Call<Void> call = apiService.eliminarLibro(libroId);

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(EditarActivity.this, "Libro eliminado", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                String errorMessage = "Error al eliminar el libro. Código: " + response.code() + " Mensaje: " + response.message();
                                Toast.makeText(EditarActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            String errorMessage = "Fallo de conexión o servidor. Error: " + t.getMessage();
                            Toast.makeText(EditarActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
