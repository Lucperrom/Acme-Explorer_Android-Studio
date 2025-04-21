package com.example.entregable1.entity;

import com.example.entregable1.AuthActivity;
import com.example.entregable1.ListadoTripActivity;
import com.example.entregable1.ProfileActivity;
import com.example.entregable1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            // Usuario está autenticado, mostrar Perfil
            list.add(new Enlace("Perfil", R.drawable.perfil, ProfileActivity.class));
        } else {
            // Usuario no está autenticado, mostrar Autenticación
            list.add(new Enlace("Autenticación", R.drawable.autenticacion, AuthActivity.class));
        }
        return list;
    }
}
