package com.example.entregable1;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.entregable1.entity.Filtro;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private TextView tvFechaFin, tvFechaInicio;
    private Calendar calendar = Calendar.getInstance();
    int yy = calendar.get(Calendar.YEAR);
    int mm = calendar.get(Calendar.MONTH);
    int dd = calendar.get(Calendar.DAY_OF_MONTH);
    int hh = calendar.get(Calendar.HOUR_OF_DAY);
    int mi = calendar.get(Calendar.MINUTE);

    private ImageView calendarIcon, calendarIcon2;

    private EditText maxPrice, minPrice;

    private Button btGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvFechaFin = findViewById(R.id.textViewFin);
        tvFechaInicio = findViewById(R.id.textViewInicio);
        calendarIcon = findViewById(R.id.imageViewFechaInicio);
        calendarIcon2 = findViewById(R.id.imageViewFechaFin);
        maxPrice = findViewById(R.id.editTextPriceMax);
        minPrice = findViewById(R.id.editTextPriceMin);
        btGo = findViewById(R.id.buttonFiltrar);

        calendarIcon.setOnClickListener(this::setDiaInicio);
        calendarIcon2.setOnClickListener(this::setDiaFin);
        btGo.setOnClickListener(this::lanzarAdapters);


    }

    public void setDiaInicio(View view) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                tvFechaInicio.setText(day + "/" + (month + 1) + "/" + year);
                dd = day;
                mm = month;
                yy = year;
            }
        }, yy, mm, dd);
        datePickerDialog.show();
    }

    public void setDiaFin(View view) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                tvFechaFin.setText(day + "/" + (month + 1) + "/" + year);
                dd = day;
                mm = month;
                yy = year;
            }
        }, yy, mm, dd);
        datePickerDialog.show();
    }

    public void lanzarAdapters(View view) {
        Filtro filtro = new Filtro();
        Calendar calInicio = Calendar.getInstance();
        Calendar calFin = Calendar.getInstance();

        String[] fechaInicioParts = tvFechaInicio.getText().toString().split("/");
        String[] fechaFinParts = tvFechaFin.getText().toString().split("/");

        if (fechaInicioParts.length == 3 && fechaFinParts.length == 3) {
            calInicio.set(Integer.parseInt(fechaInicioParts[2]),
                    Integer.parseInt(fechaInicioParts[1]) - 1,
                    Integer.parseInt(fechaInicioParts[0]));
            calFin.set(Integer.parseInt(fechaFinParts[2]),
                    Integer.parseInt(fechaFinParts[1]) - 1,
                    Integer.parseInt(fechaFinParts[0]));

            filtro.setFechaIni(calInicio);
            filtro.setFechaFin(calFin);
        }

        if (!maxPrice.getText().toString().isEmpty()) {
            filtro.setPrecioMax(Integer.parseInt(maxPrice.getText().toString()));
        }
        if (!minPrice.getText().toString().isEmpty()) {
            filtro.setPrecioMin(Integer.parseInt(minPrice.getText().toString()));
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("FILTRO", filtro);
        setResult(RESULT_OK, resultIntent); // <-- Esto hace el "return"
        finish();

    }
}