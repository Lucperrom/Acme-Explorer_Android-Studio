package com.example.entregable1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.entregable1.entity.Filtro;
import com.example.entregable1.entity.Trip;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListadoTripActivity extends AppCompatActivity implements TripAdapter.OnTripClickListener {
    Filtro filtro = new Filtro(); // Inicialización por defecto
    List<Trip> todosLosViajes = new ArrayList<>();
    List<Trip> viajesFiltrados = new ArrayList<>();;


    RecyclerView rvTrips;
    TripAdapter adapter;
    Switch switchColumnas;

    CardView cardFiltrar;

    ActivityResultLauncher<Intent> filtroLauncher;

    private FirebaseDatabaseService firebaseDatabaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        rvTrips = findViewById(R.id.rvTrips);
        switchColumnas = findViewById(R.id.switch1);
        cardFiltrar = findViewById(R.id.CardFilter);

        // Cargar viajes desde SharedPreferences
        //SharedPreferences sharedPreferences = getSharedPreferences(SplashActivity.PREF_NAME, MODE_PRIVATE);
        firebaseDatabaseService = FirebaseDatabaseService.getServiceInstance();

        firebaseDatabaseService.getTrip().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    todosLosViajes.clear(); // Limpia la lista por si acaso
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Trip trip = snapshot.getValue(Trip.class);
                        if (trip != null) {
                            todosLosViajes.add(trip);
                        }
                    }
                    Log.i("Acme-Explorer", "Se han recuperado todos los viajes. Total: " + todosLosViajes.size());

                    // Inicializa y configura el adaptador AQUÍ, después de recibir los datos
                    viajesFiltrados = new ArrayList<>(todosLosViajes);
                    adapter = new TripAdapter(viajesFiltrados, ListadoTripActivity.this);
                    rvTrips.setAdapter(adapter);
                    rvTrips.setLayoutManager(new LinearLayoutManager(ListadoTripActivity.this));

                    recargarSelecciones();
                    aplicarFiltros();

                    // Verificar si se solicitó el filtro de seleccionados (ahora que tenemos los datos)
                    if (getIntent().getBooleanExtra("FILTRO_SELECCIONADOS", false)) {
                        filtro.setSelected(true);
                        aplicarFiltros();
                    } else {
                        aplicarFiltros(); // Aplicar filtros iniciales si no hay filtro de seleccionados
                    }

                    // Cambio de columnas (la lógica puede quedarse aquí)
                    switchColumnas.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            rvTrips.setLayoutManager(new GridLayoutManager(ListadoTripActivity.this, 2));
                        } else {
                            rvTrips.setLayoutManager(new LinearLayoutManager(ListadoTripActivity.this));
                        }
                    });

                } else {
                    Log.i("Acme-Explorer", "No se encontraron viajes en la base de datos.");
                    viajesFiltrados = new ArrayList<>(); // Inicializa una lista vacía
                    adapter = new TripAdapter(viajesFiltrados, ListadoTripActivity.this);
                    rvTrips.setAdapter(adapter);
                    rvTrips.setLayoutManager(new LinearLayoutManager(ListadoTripActivity.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Acme-Explorer", "Error al leer los viajes: " + databaseError.getMessage());
                viajesFiltrados = new ArrayList<>(); // Inicializa una lista vacía en caso de error
                adapter = new TripAdapter(viajesFiltrados, ListadoTripActivity.this);
                rvTrips.setAdapter(adapter);
                rvTrips.setLayoutManager(new LinearLayoutManager(ListadoTripActivity.this));
            }
        });

        //String json = sharedPreferences.getString(SplashActivity.VIAJES_KEY, null);

      /*  if (json != null) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Trip>>() {}.getType();
            todosLosViajes = gson.fromJson(json, tipoLista);

            // Recuperar el estado de selección de cada viaje
            recargarSelecciones();
        } else {
            todosLosViajes = new ArrayList<>();
        }*/

        // Lanzador de filtros con resultado
        filtroLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        filtro = result.getData().getParcelableExtra("FILTRO");
                        aplicarFiltros();
                    }
                }
        );

        // Botón de filtrar
        cardFiltrar.setOnClickListener(v -> {
            Intent intent = new Intent(ListadoTripActivity.this, MainActivity.class);
            // Pasar el filtro actual para que se muestre en la actividad de filtros
            intent.putExtra("FILTRO", filtro);
            filtroLauncher.launch(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar el estado de selección al volver a la actividad
        recargarSelecciones();
        // Volver a aplicar los filtros para reflejar cambios en selecciones
        aplicarFiltros();
    }

    private void recargarSelecciones() {
        SharedPreferences tripPrefs = getSharedPreferences("TripPrefs", MODE_PRIVATE);

        // Actualizar todosLosViajes
        for (Trip trip : todosLosViajes) {
            if (trip.getCodigo() != null && !trip.getCodigo().isEmpty()) {
                boolean isSelected = tripPrefs.getBoolean(trip.getCodigo(), false);
                trip.setSelected(isSelected);
            }
        }

        // Después de actualizar la lista, necesitas notificar al adapter que los datos han cambiado
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void aplicarFiltros() {
        viajesFiltrados.clear();
        List<Trip> listaBase;

        // Determinar la lista base para aplicar los filtros (todos los viajes o solo los seleccionados)
        if (filtro.getSelected() != null && filtro.getSelected()) {
            listaBase = new ArrayList<>();
            for (Trip trip : todosLosViajes) {
                if (trip.getSelected() != null && trip.getSelected()) {
                    listaBase.add(trip);
                }
            }
        } else {
            listaBase = new ArrayList<>(todosLosViajes);
        }

        List<Trip> resultadoFiltrado = new ArrayList<>(listaBase);

        // 2. Aplicar filtro de precio si está configurado
        if (filtro.getPrecioMin() > 0 || filtro.getPrecioMax() < Float.MAX_VALUE) {
            resultadoFiltrado.removeIf(trip -> trip.getPrecio() < filtro.getPrecioMin() || trip.getPrecio() > filtro.getPrecioMax());
        }

        // 3. Aplicar filtro de fecha si está configurado
        if (filtro.getFechaIni() != null && filtro.getFechaFin() != null) {
            long inicioFiltro = filtro.getFechaIni().getTimeInMillis();
            long finFiltro = filtro.getFechaFin().getTimeInMillis();

            resultadoFiltrado.removeIf(trip -> {
                long salida = trip.getFechaSalida().getTime();
                long llegada = trip.getFechaLlegada().getTime();
                return salida < inicioFiltro || llegada > finFiltro;
            });
        }

        viajesFiltrados.addAll(resultadoFiltrado);

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onTripClick(Trip trip) {
        Intent intent = new Intent(this, TripDetailActivity.class);
        intent.putExtra("TRIP_OBJECT", trip);
        startActivity(intent);
    }
}