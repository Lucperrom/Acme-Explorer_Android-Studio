package com.example.entregable1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.entregable1.entity.Enlace;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.entregable1.entity.Enlace;

import java.util.List;

public class DisponibleSeleccionado extends AppCompatActivity {
    GridView gridView;
    EnlaceAdapter enlaceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlace_adapter);

        gridView=findViewById(R.id.gridView);
        gridView.setNumColumns(1);
        EnlaceAdapter enlaceAdapter=new EnlaceAdapter(Enlace.generaEnlaces(),this);
        gridView.setAdapter(enlaceAdapter);
    }
}
class EnlaceAdapter extends BaseAdapter {

    List<Enlace> enlaces;
    Context context;

    public EnlaceAdapter(List<Enlace> enlaces, Context context) {
        this.enlaces = enlaces;
        this.context = context;
    }

    @Override
    public int getCount() {
        return enlaces.size();
    }

    @Override
    public Object getItem(int i) {
        return enlaces.get(i);
    }

    @Override
    public long getItemId(int i) {
        return enlaces.get(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Enlace enlace = enlaces.get(i);
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.enlace_item, viewGroup, false);
        }
        CardView cardView = view.findViewById(R.id.cardView);
        TextView textView = view.findViewById(R.id.textView);
        ImageView imageView = view.findViewById(R.id.imageView);

        textView.setText(enlace.getDescripcion());
        imageView.setImageResource(enlace.getRecursoImageView());

        cardView.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, enlace.getClase());
            if (enlace.getClase().equals(ListadoTripActivity.class) && enlace.getDescripcion().equals("Viajes Seleccionados")) {
                intent.putExtra("FILTRO_SELECCIONADOS", true); // Enviamos una bandera
            }
            context.startActivity(intent);
        });
        return view;
    }
}
