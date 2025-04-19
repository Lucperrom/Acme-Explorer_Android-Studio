package com.example.entregable1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.entregable1.entity.Trip;

public class TripItem extends AppCompatActivity {

    CardView cardView;
    ImageView imageTrip;
    TextView ciudad, descripcion;

    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_item);
        cardView = findViewById(R.id.CardTrip);
        imageTrip = findViewById(R.id.imageTrip);
        ciudad = findViewById(R.id.textViewCiudad);
        descripcion = findViewById(R.id.textViewDescripcion);
        trip = new Trip();

        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(TripItem.this, TripDetailActivity.class);
            intent.putExtra("TRIP", trip);
            startActivity(intent);
        });
    }
}