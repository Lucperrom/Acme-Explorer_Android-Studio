package com.example.entregable1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.entregable1.entity.Trip;
import com.example.entregable1.paypalrest.CreatePaymentRequest;
import com.example.entregable1.paypalrest.PayPalApiClient;
import com.example.entregable1.paypalrest.PaymentResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TripDetailActivity extends AppCompatActivity {

    Trip trip;
    TextView tvLugarSalida, tvLugarDestino, tvPrecio, tvFechaSalida, tvFechaLlegada;
    ImageView ivTripImage;

    ImageButton btSelected;
    Button btBuy, btMap;

    private PayPalApiClient paypalClient;
    private Button payWithPayPalButton;

    private SharedPreferences sharedPreferences;
    private static FirebaseUser firebaseUser;

    private static FirebaseFirestore mDatabase;

    public static final String PREF_NAME = "precio_pref";
    public static final String PRECIO_KEY = "precio";

    public static final String PAGADO_PREF = "pagado_pref";
    public static final String PAGADO_KEY = "pagado";

    public static final String TRIP_PREF = "trip_pref";
    public static final String TRIP_KEY = "trip";

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
        btMap = findViewById(R.id.buttonMap);
        paypalClient = PayPalApiClient.getInstance(true);
        payWithPayPalButton = findViewById(R.id.btnPayWithPaypal);
        mDatabase = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        trip = getIntent().getParcelableExtra("TRIP_OBJECT");

        if (trip != null) {
            setearCampos();
            Gson gson = new Gson();
            String json = gson.toJson(trip);
            sharedPreferences = getSharedPreferences(TRIP_PREF, MODE_PRIVATE);
            sharedPreferences.edit().putString(TRIP_KEY, json).apply();

        }else{
            SharedPreferences sharedPreferences = getSharedPreferences(TripDetailActivity.TRIP_PREF, MODE_PRIVATE);
            String json = sharedPreferences.getString(TripDetailActivity.TRIP_KEY, null);
            if (json != null) {
                Gson gson = new Gson();
                Type aTrip = new TypeToken<Trip>(){
                }.getType();
                trip = gson.fromJson(json, aTrip);
                setearCampos();
            }
        }

        payWithPayPalButton.setOnClickListener(v -> {
            Gson gson = new Gson();
            String json = gson.toJson(trip.getPrecio());
            sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            sharedPreferences.edit().putString(PRECIO_KEY, json).apply();
            Intent intent = new Intent(TripDetailActivity.this, PaymentActivity.class);
            //Log.i("TripDetailActivity", "Precio del viaje antes de pasar a PaymentActivity: " + trip.getPrecio());
            //intent.putExtra("PRECIO_TRIP", String.valueOf(trip.getPrecio()));
            startActivity(intent);
        });

        btMap.setOnClickListener(v -> {
            //Intent intent = new Intent(this, MapsActivity.class);
            Intent intent = new Intent(this, LocationActivity.class);
            intent.putExtra("SALIDA", trip.getLugarSalida());
            startActivity(intent);
        });


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

        if(getIntent().getStringExtra("PAGADO") != null) {
            Log.i("TripDetailActivity", "Pago realizado");
            payWithPayPalButton.setVisibility(View.GONE);
            if (firebaseUser != null) {
                mDatabase.collection("users").document(firebaseUser.getUid()).collection("trips").add(trip);
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences(TripDetailActivity.PAGADO_PREF, MODE_PRIVATE);
                String json = sharedPreferences.getString(TripDetailActivity.PAGADO_KEY, null);
                List<Trip> trips = new ArrayList<>();
                if (json != null) {
                    Gson gson = new Gson();
                    Type listaTrip = new TypeToken<List<Trip>>() {
                    }.getType();
                    trips = gson.fromJson(json, listaTrip);

                }
                trips.add(trip);
                Gson gson = new Gson();
                String json2 = gson.toJson(trips);
                sharedPreferences = getSharedPreferences(PAGADO_PREF, MODE_PRIVATE);
                sharedPreferences.edit().putString(PAGADO_KEY, json2).apply();
            }
        }

            if (firebaseUser!=null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .document(firebaseUser.getUid())
                        .collection("trips")
                        .whereEqualTo("codigo", trip.getCodigo())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                payWithPayPalButton.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Trip", "Error verificando el viaje", e);
                        });
            } else{
                SharedPreferences sharedPreferences = getSharedPreferences(TripDetailActivity.PAGADO_PREF, MODE_PRIVATE);
                String json = sharedPreferences.getString(TripDetailActivity.PAGADO_KEY, null);
                if (json != null) {
                    Gson gson = new Gson();
                    Type listaTrips = new TypeToken<List<Trip>>() {}.getType();
                    List<Trip> trips = gson.fromJson(json, listaTrips);
                    if(trips.contains(trip)) payWithPayPalButton.setVisibility(View.GONE);
                }
            }
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
        tvPrecio.setText(String.valueOf(trip.getPrecio() + "€"));
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