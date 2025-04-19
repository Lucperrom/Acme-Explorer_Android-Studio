package com.example.entregable1.entity;

import com.example.entregable1.FilterActivity;
import com.example.entregable1.ListadoTripActivity;
import com.example.entregable1.MainActivity;
import com.example.entregable1.R;
import com.example.entregable1.TripItem;

import java.util.ArrayList;
import java.util.List;

public class Enlace {
    private String descripcion;
    private int recursoImageView;
    private Class clase;

    public Enlace(String descripcion, int recursoImageView, Class clase) {
        this.descripcion = descripcion;
        this.recursoImageView = recursoImageView;
        this.clase = clase;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getRecursoImageView() {
        return recursoImageView;
    }

    public void setRecursoImageView(int recursoImageView) {
        this.recursoImageView = recursoImageView;
    }

    public Class getClase() {
        return clase;
    }

    public void setClase(Class clase) {
        this.clase = clase;
    }

    public static List<Enlace> generaEnlaces(){
        List<Enlace> list=new ArrayList<>();
        list.add(new Enlace("Viajes Disponibles",R.drawable.viajar, ListadoTripActivity.class));
        list.add(new Enlace("Viajes Seleccionados", R.drawable.objetivo, ListadoTripActivity.class));
        return list;
    }
}
