package com.example.proyectofinal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectofinal.Clases.ApiService;
import com.example.proyectofinal.Clases.Libro;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AgregarActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 100;

    private EditText etTitulo, etAutor, etPrecio, etStock, etTema, etImagenUrl;
    private Button btnGuardar, btnCancelar;
    private ApiService apiService;

    private Button btnSeleccionarImagen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);

        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);

        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());

        // Configurar el ActionBar con el título
        getSupportActionBar().setTitle("Agregar Libro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Inicializar los elementos de la interfaz
        etTitulo = findViewById(R.id.etTitulo);
        etAutor = findViewById(R.id.etAutor);
        etPrecio = findViewById(R.id.etPrecio);
        etStock = findViewById(R.id.etStock);
        etTema = findViewById(R.id.etTema);
        etImagenUrl = findViewById(R.id.etImagenUrl);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        Retrofit retrofit = ConexionApi.getConexionApi();
        apiService = retrofit.create(ApiService.class);

        // Manejar el clic en el botón "Guardar"
        btnGuardar.setOnClickListener(v -> agregarLibro());

        // Manejar el clic en el botón "Cancelar"
        btnCancelar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void agregarLibro() {
        // Obtener los valores del formulario
        String titulo = etTitulo.getText().toString();
        String autor = etAutor.getText().toString();
        String tema = etTema.getText().toString();
        String imagenUrl = etImagenUrl.getText().toString();
        double precio;
        int stock;

        // Validar los campos
        if (titulo.isEmpty() || autor.isEmpty() || etPrecio.getText().toString().isEmpty() ||
                etStock.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            precio = Double.parseDouble(etPrecio.getText().toString());
            stock = Integer.parseInt(etStock.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precio y stock deben ser valores numéricos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un objeto Libro
        Libro nuevoLibro = new Libro(titulo, autor, precio, stock, imagenUrl);
        nuevoLibro.setTema(tema);

        // Llamar al API para guardar el libro
        Call<Libro> call = apiService.agregarLibro(nuevoLibro);
        call.enqueue(new Callback<Libro>() {
            @Override
            public void onResponse(Call<Libro> call, Response<Libro> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AgregarActivity.this, "Libro agregado correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("ACTUALIZADO", true);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(AgregarActivity.this, "Error al agregar el libro", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Libro> call, Throwable t) {
                Toast.makeText(AgregarActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Muestra la URI en el campo de texto de la URL de la imagen
                etImagenUrl.setText(selectedImageUri.toString());
            } else {
                Toast.makeText(this, "No se pudo obtener la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
