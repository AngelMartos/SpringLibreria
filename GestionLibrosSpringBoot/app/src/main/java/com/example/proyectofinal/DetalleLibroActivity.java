package com.example.proyectofinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.proyectofinal.Clases.ApiService;
import com.example.proyectofinal.Clases.Libro;
import com.example.proyectofinal.Clases.Reserva;
import com.example.proyectofinal.Clases.Usuario;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetalleLibroActivity extends AppCompatActivity {

    private TextView tituloTextView, autorTextView, precioTextView, stockTextView;
    private EditText cantidadEditText;
    private Button reservarButton;
    private ImageView imagenLibroImageView;
    private long libroId;
    private int stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_libro);

        getSupportActionBar().setTitle("Reserva");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Inicializar vistas
        tituloTextView = findViewById(R.id.tituloTextView);
        autorTextView = findViewById(R.id.autorTextView);
        precioTextView = findViewById(R.id.precioTextView);
        stockTextView = findViewById(R.id.stockTextView);
        cantidadEditText = findViewById(R.id.cantidadEditText);
        reservarButton = findViewById(R.id.reservarButton);
        imagenLibroImageView = findViewById(R.id.imagenLibroImageView);

        // Obtener el libroId y detalles del Intent
        libroId = getIntent().getLongExtra("libroId", -1);
        String titulo = getIntent().getStringExtra("titulo");
        String autor = getIntent().getStringExtra("autor");
        double precio = getIntent().getDoubleExtra("precio", 0.0);
        stock = getIntent().getIntExtra("stock", 0);
        String imagenUrl = getIntent().getStringExtra("imagenUrl");

        // Mostrar los detalles del libro
        tituloTextView.setText(titulo);
        autorTextView.setText(autor);
        precioTextView.setText("Precio: " + precio);
        stockTextView.setText("Stock: " + stock);

        // Cargar la imagen del libro en un hilo de fondo
        new DescargarImagenTask(imagenLibroImageView).execute(imagenUrl);

        // Acción del botón para realizar la reserva
        reservarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarReserva();
            }
        });
    }

    private void realizarReserva() {
        // Verificar que la cantidad no esté vacía
        String cantidadStr = cantidadEditText.getText().toString().trim();
        if (cantidadStr.isEmpty()) {
            Toast.makeText(DetalleLibroActivity.this, "Por favor, ingrese la cantidad a reservar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir la cantidad a un entero y verificar que sea un valor positivo
        int cantidad = Integer.parseInt(cantidadStr);
        if (cantidad <= 0) {
            Toast.makeText(DetalleLibroActivity.this, "La cantidad debe ser un número positivo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cantidad > stock) {
            Toast.makeText(DetalleLibroActivity.this, "No puedes reservar más unidades que las disponibles en el stock", Toast.LENGTH_SHORT).show();
            return;
        }


        // Verificar que el libroId sea válido
        if (libroId <= 0) {
            Toast.makeText(DetalleLibroActivity.this, "El libro no es válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el usuarioId desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MiApp", MODE_PRIVATE);
        long usuarioId = sharedPreferences.getLong("usuarioId", -1);
        if (usuarioId == -1) {
            Toast.makeText(DetalleLibroActivity.this, "Usuario no encontrado. Inicie sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio = getIntent().getDoubleExtra("precio", 0.0);
        double precioTotal = precio * cantidad;

        // Obtener los objetos Usuario y Libro completos
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        Libro libro = new Libro();
        libro.setId(libroId);

        // Crear el objeto Reserva con los objetos completos
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setLibro(libro);
        reserva.setCantidad(cantidad);

        // Verificar que la reserva no sea null antes de enviarla
        if (reserva == null) {
            Toast.makeText(DetalleLibroActivity.this, "No se pudo crear la reserva. Inténtelo de nuevo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Realizar la solicitud de reserva
        Retrofit retrofit = ConexionApi.getConexionApi();
        ApiService apiService = retrofit.create(ApiService.class);
        apiService.crearReserva(reserva).enqueue(new Callback<Reserva>() {
            @Override
            public void onResponse(Call<Reserva> call, Response<Reserva> response) {
                if (response.isSuccessful()) {
                    actualizarStockEnBaseDeDatos(cantidad);

                    Toast.makeText(DetalleLibroActivity.this, "Reserva realizada con éxito", Toast.LENGTH_SHORT).show();
                    // Redirigir a la actividad ClienteActivity en vez de ReservasActivity
                    Intent intent = new Intent(DetalleLibroActivity.this, ClienteActivity.class);
                    startActivity(intent);
                    finish(); // Esto es opcional, pero te asegura que no puedas regresar a esta actividad con el botón atrás.
                } else {
                    // Mostrar mensaje de error detallado
                    String errorMessage = "Error al guardar la reserva: " + response.code() + " " + response.message();
                    Toast.makeText(DetalleLibroActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Reserva> call, Throwable t) {
                Toast.makeText(DetalleLibroActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarStockEnBaseDeDatos(int nuevoStock) {
        Retrofit retrofit = ConexionApi.getConexionApi();
        ApiService apiService = retrofit.create(ApiService.class);

        apiService.actualizarStock(libroId, nuevoStock).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Actualizamos el stock localmente después de recibir confirmación
                    stock = nuevoStock;
                    stockTextView.setText("Stock: " + stock);
                    Toast.makeText(DetalleLibroActivity.this, "Stock actualizado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetalleLibroActivity.this, "Error al actualizar el stock. Código: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DetalleLibroActivity.this, "Error al conectar para actualizar el stock", Toast.LENGTH_SHORT).show();
            }
        });
    }




    // AsyncTask para descargar la imagen en segundo plano
    private static class DescargarImagenTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView;

        public DescargarImagenTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String imagenUrl = strings[0];
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(imagenUrl).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            // Handle the back button in the action bar
            getOnBackPressedDispatcher().onBackPressed(); // Updated for newer Android versions
            return true;
        } else if (itemId == R.id.action_logout) {
            Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if (itemId == R.id.action_carrito) {
            // Abre la pantalla de ReservasActivity cuando se hace clic en el carrito
            Intent intent = new Intent(this, ReservasActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_popular) {
            // Abre la pantalla de PopularActivity cuando se hace clic en el popular
            Intent intent = new Intent(this, popularesActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}