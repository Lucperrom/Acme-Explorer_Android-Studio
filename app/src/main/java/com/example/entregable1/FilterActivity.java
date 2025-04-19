package com.example.entregable1;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.entregable1.entity.Filtro;

public class FilterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Filtro filtro = new Filtro();
        filtro.setSelected(true); // Indicamos que queremos filtrar por seleccionados
        Intent resultIntent = new Intent();
        resultIntent.putExtra("FILTRO", filtro);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}