package com.example.proyectofinal;

import static com.example.proyectofinal.Clases.ApiClient.getRetrofitInstance;

import com.example.proyectofinal.Clases.ApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConexionApi {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.65.1:8080";

    public static Retrofit getConexionApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getConexionApi().create(ApiService.class);
    }

}
