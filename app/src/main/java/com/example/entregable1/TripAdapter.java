package com.example.entregable1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.entregable1.entity.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> implements EventListener<QuerySnapshot> {

    private List<Trip> mTrips;

    private OnTripClickListener mListener;

    public TripAdapter(List<Trip> trips, OnTripClickListener listener){
        mTrips = trips;
        mListener = listener;
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

    }

    public interface OnTripClickListener {
        void onTripClick(Trip trip);
    }

    @NonNull
    @Override
    public TripAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tripView = inflater.inflate(R.layout.activity_trip_item, parent, false);
        return new ViewHolder(tripView);
    }

    @Override
    public void onBindViewHolder(@NonNull TripAdapter.ViewHolder holder, int position) {
        Trip trip = mTrips.get(position);
        Context context = holder.itemView.getContext();

        Glide.with(holder.imageTrip.getContext())
                .load(trip.getUrl())
                .into(holder.imageTrip);

        holder.ciudad.setText(trip.getLugarDestino());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaSalida = sdf.format(trip.getFechaSalida().getTime());
        String fechaLlegada = sdf.format(trip.getFechaLlegada().getTime());

        holder.descripcion.setText(trip.getPrecio() + "$. Salida: " + fechaSalida + " Llegada: " + fechaLlegada);

        // Estado visual inicial del botón según el valor ya establecido en el objeto trip
        // No cargamos de nuevo aquí porque ya se hizo en la actividad
        if (trip.getSelected() != null && trip.getSelected()) {
            holder.btSelected.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.btSelected.setImageResource(android.R.drawable.btn_star_big_off);
        }

        // OnClick de la tarjeta
        holder.cardView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onTripClick(trip);
            }
        });

        // OnClick del botón de selección
        holder.btSelected.setOnClickListener(v -> {
            boolean isSelected = trip.getSelected() != null && trip.getSelected();

            if (isSelected) {
                trip.setSelected(false);
                holder.btSelected.setImageResource(android.R.drawable.btn_star_big_off);
            } else {
                trip.setSelected(true);
                holder.btSelected.setImageResource(android.R.drawable.btn_star_big_on);
            }

            // Guardar en SharedPreferences usando el código como clave
            saveTripSelection(context, trip.getCodigo(), trip.getSelected());
        });
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    private void saveTripSelection(Context context, String tripId, boolean isSelected) {
        if (tripId != null && !tripId.isEmpty()) {
            context.getSharedPreferences("TripPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(tripId, isSelected)
                    .apply();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageTrip;
        public TextView ciudad, descripcion;
        public CardView cardView;
        ImageButton btSelected;

        public ViewHolder(View itemView) {
            super(itemView);

            imageTrip = (ImageView) itemView.findViewById(R.id.imageTrip);
            ciudad = (TextView) itemView.findViewById(R.id.textViewCiudad);
            descripcion = (TextView) itemView.findViewById(R.id.textViewDescripcion);
            cardView = (CardView) itemView.findViewById(R.id.CardTrip);
            btSelected = (ImageButton) itemView.findViewById(R.id.buttonSelected);
        }
    }
}