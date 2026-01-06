package com.example.proyectofinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectofinal.Clases.ApiService;
import com.example.proyectofinal.Clases.Usuario;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton, registerButton, exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Libreria");

        // Inicializar las vistas
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        exitButton = findViewById(R.id.exitButton);

        // Configurar el botón de login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Configurar el botón de registro
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });

        // Configurar el botón de salir
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validación básica
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el objeto Usuario para enviar a la API
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(password);

        // Realizar la solicitud de login a la API
        Retrofit retrofit = ConexionApi.getConexionApi();
        ApiService apiService = retrofit.create(ApiService.class);
        apiService.login(usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario loggedInUser = response.body();

                    // Guardar el objeto Usuario completo en SharedPreferences
                    Gson gson = new Gson();
                    String usuarioJson = gson.toJson(loggedInUser);  // Convierte el objeto Usuario a JSON
                    SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("usuario", usuarioJson);  // Guarda el objeto como cadena JSON
                    editor.apply();

                    // Verificar el rol del usuario y navegar según el rol
                    if (loggedInUser.getRole().equals("TRABAJADOR")) {
                        // Navegar a la actividad de trabajador
                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                        startActivity(intent);
                    } else {
                        // Navegar a la actividad de cliente
                        Intent intent = new Intent(LoginActivity.this, ClienteActivity.class);
                        startActivity(intent);
                    }
                } else {
                    // En caso de respuesta no exitosa o cuerpo nulo
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                // Mostrar mensaje de error con más detalles
                Toast.makeText(LoginActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
