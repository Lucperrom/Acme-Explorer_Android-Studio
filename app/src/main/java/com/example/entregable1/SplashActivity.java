package com.example.entregable1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.entregable1.entity.Trip;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
        //todosLosViajes = Trip.generaViajes(1);
        //guardarViajesEnRealTimeDatabase(todosLosViajes);
        // Solo generar y guardar si no está guardado aún
       /* if (!sharedPreferences.contains(VIAJES_KEY)) {
            guardarViajesEnSharedPreferences(todosLosViajes);
        } else {
            todosLosViajes = obtenerViajesDeSharedPreferences();
        }*/

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, DisponibleSeleccionado.class));
            finish();
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

    private void guardarViajesEnRealTimeDatabase(List<Trip> viajes) {
        FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getServiceInstance();
        for (Trip viaje : viajes) {
            firebaseDatabaseService.saveTrip(viaje, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        Log.i("Acme-Explorer", "Trip insertado");
                    } else {
                        Log.i("Acme-Explorer", "Error al insertar Trip." + databaseError.getMessage());
                    }
                }
            });
        }
    }
}
