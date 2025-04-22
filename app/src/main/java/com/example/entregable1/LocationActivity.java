package com.example.entregable1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.entregable1.resttypes.WeatherResponse;
import com.example.entregable1.resttypes.WeatherRetrofitInterface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.Priority;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE_LOCATION = 0x123;

    private FusedLocationProviderClient locationServices;
    TextView tvLocation;
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        tvLocation = findViewById(R.id.location);
        locationServices = LocationServices.getFusedLocationProviderClient(this);

        retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build();

        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        if(ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Snackbar.make(tvLocation, R.string.location_rationale, Snackbar.LENGTH_LONG).setAction(R.string.location_rationale_ok, view -> {
                    ActivityCompat.requestPermissions(LocationActivity.this, permissions, PERMISSION_REQUEST_CODE_LOCATION);
                }).show();
            } else {
                ActivityCompat.requestPermissions(LocationActivity.this, permissions, PERMISSION_REQUEST_CODE_LOCATION);
            }
        }else{
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_CODE_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }else{
                String salida = getIntent().getStringExtra("SALIDA");
                Intent intent = new Intent(LocationActivity.this, MapsActivity.class);
                intent.putExtra("SALIDA", salida);
                intent.putExtra("LAT", "");
                intent.putExtra("LON", "");
                intent.putExtra("TEMP", "");
                startActivity(intent);
            }
        }
    }
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30000)
                .setWaitForAccurateLocation(false) // Opcional, dependiendo de tus necesidades
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // Seguridad adicional
        }

        locationServices.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull com.google.android.gms.location.LocationResult locationResult){
            if (locationResult == null || locationResult.getLastLocation() == null ||  !locationResult.getLastLocation().hasAccuracy()) return;
            else{
                Location location = locationResult.getLastLocation();
                weather(location); // weather() ahora maneja todo el proceso
                stopLocationUpdates();
                Log.i("Acme-Explorer","Location: Lat:" + location.getLatitude() + " Long: " + location.getLongitude() + " Acc: " + location.getAccuracy());
            }

        }
    };

    private void weather(Location location){
        String salida = getIntent().getStringExtra("SALIDA");
        WeatherRetrofitInterface service = retrofit.create(WeatherRetrofitInterface.class);
        Call<WeatherResponse> response = service.getCurrentWeather((float)location.getLatitude(), (float)location.getLongitude(), getString(R.string.open_weather_map_api_key),  "es", "metric");

        // Preparar intent pero NO iniciar la actividad todavía
        Intent intent = new Intent(LocationActivity.this, MapsActivity.class);
        intent.putExtra("SALIDA", salida);
        intent.putExtra("LAT", String.valueOf(location.getLatitude()));
        intent.putExtra("LON", String.valueOf(location.getLongitude()));

        response.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    String temp = "La temperatura actual en "+ salida + " es: "  + response.body().getMain().getTemp() +
                            " y la condición es: " + (response.body().getWeather().size() > 0 ? response.body().getWeather().get(0).getDescription() : "desconocida");
                    intent.putExtra("TEMP", temp);
                } else {
                    intent.putExtra("TEMP", "No se pudo obtener la temperatura");
                }

                // Iniciar actividad DESPUÉS de recibir respuesta
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.i("Acme-Explorer", "REST: error en la llamada. " + t.getMessage());
                intent.putExtra("TEMP", "Error al obtener datos del clima");
                // Iniciar actividad aunque haya error
                startActivity(intent);
            }
        });

        // Ya no llames a startActivity aquí
        stopLocationUpdates();
    }

    public void stopLocationUpdates() {
        locationServices.removeLocationUpdates(locationCallback);
    }

}