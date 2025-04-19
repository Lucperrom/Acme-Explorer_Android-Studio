package com.example.entregable1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.entregable1.entity.Trip;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private final int DURACION = 1000;

    SharedPreferences sharedPreferences;
    List<Trip> todosLosViajes;
    public static final String PREF_NAME = "viajes_pref";
    public static final String VIAJES_KEY = "lista_viajes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Solo generar y guardar si no está guardado aún
        if (!sharedPreferences.contains(VIAJES_KEY)) {
            todosLosViajes = Trip.generaViajes(100);
            guardarViajesEnSharedPreferences(todosLosViajes);
        } else {
            todosLosViajes = obtenerViajesDeSharedPreferences();
        }

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, DisponibleSeleccionado.class));
        }, DURACION);
    }

    private void guardarViajesEnSharedPreferences(List<Trip> viajes) {
        Gson gson = new Gson();
        String json = gson.toJson(viajes);
        sharedPreferences.edit().putString(VIAJES_KEY, json).apply();
    }

    public List<Trip> obtenerViajesDeSharedPreferences() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(VIAJES_KEY, null);
        Type tipoLista = new TypeToken<List<Trip>>() {}.getType();
        return gson.fromJson(json, tipoLista);
    }
}
