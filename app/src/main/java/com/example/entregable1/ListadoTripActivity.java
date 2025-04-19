package com.example.entregable1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.entregable1.entity.Filtro;
import com.example.entregable1.entity.Trip;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListadoTripActivity extends AppCompatActivity implements TripAdapter.OnTripClickListener {
    Filtro filtro = new Filtro(); // Inicialización por defecto
    List<Trip> todosLosViajes;
    List<Trip> viajesFiltrados;

    RecyclerView rvTrips;
    TripAdapter adapter;
    Switch switchColumnas;

    CardView cardFiltrar;

    ActivityResultLauncher<Intent> filtroLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        rvTrips = findViewById(R.id.rvTrips);
        switchColumnas = findViewById(R.id.switch1);
        cardFiltrar = findViewById(R.id.CardFilter);

        // Cargar viajes desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SplashActivity.PREF_NAME, MODE_PRIVATE);
        String json = sharedPreferences.getString(SplashActivity.VIAJES_KEY, null);

        if (json != null) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Trip>>() {}.getType();
            todosLosViajes = gson.fromJson(json, tipoLista);

            // Recuperar el estado de selección de cada viaje
            recargarSelecciones();
        } else {
            todosLosViajes = new ArrayList<>();
        }

        viajesFiltrados = new ArrayList<>(todosLosViajes);

        // Verificar si se solicitó el filtro de seleccionados
        if (getIntent().getBooleanExtra("FILTRO_SELECCIONADOS", false)) {
            filtro.setSelected(true);
            aplicarFiltros(); // Usamos el método general de aplicar filtros
        }

        adapter = new TripAdapter(viajesFiltrados, this);
        rvTrips.setAdapter(adapter);
        rvTrips.setLayoutManager(new LinearLayoutManager(this));

        // Cambio de columnas
        switchColumnas.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                rvTrips.setLayoutManager(new GridLayoutManager(this, 2));
            } else {
                rvTrips.setLayoutManager(new LinearLayoutManager(this));
            }
        });

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

        // No es necesario actualizar viajesFiltrados aquí ya que aplicarFiltros() lo rehará
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
                long salida = trip.getFechaSalida().getTimeInMillis();
                long llegada = trip.getFechaLlegada().getTimeInMillis();
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