package com.example.entregable1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.entregable1.entity.Trip;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TripDetailActivity extends AppCompatActivity {

    Trip trip;
    TextView tvLugarSalida, tvLugarDestino, tvPrecio, tvFechaSalida, tvFechaLlegada;
    ImageView ivTripImage;

    ImageButton btSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        tvLugarSalida = findViewById(R.id.textLugarSalida);
        tvLugarDestino = findViewById(R.id.textLugarLlegada);
        tvPrecio = findViewById(R.id.textPrice);
        tvFechaSalida = findViewById(R.id.textSalida);
        tvFechaLlegada = findViewById(R.id.textLlegada);
        ivTripImage = findViewById(R.id.imageTripDetail);
        btSelected = findViewById(R.id.btSelected);

        trip = getIntent().getParcelableExtra("TRIP_OBJECT");

        if (trip != null) {
            setearCampos();
        }
        // Estado visual inicial del botón según selección
        if (trip.getSelected() != null && trip.getSelected()) {
            btSelected.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            btSelected.setImageResource(android.R.drawable.btn_star_big_off);
        }

        btSelected.setOnClickListener(v -> {
            boolean isSelected = trip.getSelected() != null && trip.getSelected();

            if (isSelected) {
                trip.setSelected(false);
                btSelected.setImageResource(android.R.drawable.btn_star_big_off);
            } else {
                trip.setSelected(true);
                btSelected.setImageResource(android.R.drawable.btn_star_big_on);
            }

            // Guardar en SharedPreferences
            saveTripSelection(this, trip.getCodigo(), trip.getSelected());
        });

    }
    private void saveTripSelection(Context context, String tripId, boolean isSelected) {
        context.getSharedPreferences("TripPrefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean(tripId, isSelected)
                .apply();
    }

    private void setearCampos() {
        tvLugarSalida.setText(trip.getLugarSalida());
        tvLugarDestino.setText(trip.getLugarDestino());
        tvPrecio.setText(String.valueOf(trip.getPrecio() + "$"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
        String fechaSalidaFormateada = sdf.format(trip.getFechaSalida().getTime()); // Formatear directamente el Date
        String fechaLlegadaFormateada = sdf.format(trip.getFechaLlegada().getTime()); // Formatear directamente el Date

        tvFechaSalida.setText(fechaSalidaFormateada);
        tvFechaLlegada.setText(fechaLlegadaFormateada);

        Glide.with(ivTripImage.getContext())
                .load(trip.getUrl())
                .into(ivTripImage);
    }
}