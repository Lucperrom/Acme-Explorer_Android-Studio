package com.example.entregable1;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.entregable1.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private TextView tvDistancia;
    private TextView tvTemperatura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        tvDistancia = findViewById(R.id.textDistancia);
        tvTemperatura = findViewById(R.id.textTemperatura);
        String temp = getIntent().getStringExtra("TEMP");
        if(temp != null && !temp.isEmpty())  tvTemperatura.setText(temp);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String nombreLugar = getIntent().getStringExtra("SALIDA");
        // Usar Geocoder en segundo plano
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> direcciones = geocoder.getFromLocationName(nombreLugar, 1);
            if (direcciones != null && !direcciones.isEmpty()) {
                Address direccion = direcciones.get(0);
                double latitud = direccion.getLatitude();
                double longitud = direccion.getLongitude();
                LatLng ubicacion = new LatLng(latitud, longitud);
                pinOrigen(ubicacion);
                mMap.addMarker(new MarkerOptions().position(ubicacion).title("Ubicación: " + nombreLugar));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15));
            } else {
                Toast.makeText(this, "No se encontró la ubicación", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al obtener la ubicación", Toast.LENGTH_SHORT).show();
        }
    }

    public void pinOrigen(LatLng ubicacion){
        String origen_lat = getIntent().getStringExtra("LAT");
        String origen_lon = getIntent().getStringExtra("LON");
        if(origen_lat != "" || origen_lon != ""){
            double lat = Double.parseDouble(origen_lat);
            double lon = Double.parseDouble(origen_lon);
            LatLng origen = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(origen).title("Tu ubicación"));
            calcularDistancia(origen, ubicacion);
        }
    }



    private void calcularDistancia(LatLng origen, LatLng ubicacion){
        Location locOrigen = new Location("");
        locOrigen.setLatitude(origen.latitude);
        locOrigen.setLongitude(origen.longitude);

        Location locDestino = new Location("");
        locDestino.setLatitude(ubicacion.latitude);
        locDestino.setLongitude(ubicacion.longitude);

        float distanciaMetros = locOrigen.distanceTo(locDestino);
        float distanciaKm = distanciaMetros / 1000;
        tvDistancia.setText("Distancia entre tu ubicación y la salida: " + distanciaKm + " km");

    }

}