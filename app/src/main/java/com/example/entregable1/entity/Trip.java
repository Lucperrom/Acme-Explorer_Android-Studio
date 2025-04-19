package com.example.entregable1.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.entregable1.Constantes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Trip implements Parcelable {
    private String LugarSalida, LugarDestino, Descripcion, Url, Codigo;
    private int Precio;
    private Calendar FechaSalida, FechaLlegada;

    private Boolean selected;

    public Trip() {
    }

    public Trip(String lugarSalida, String lugarDestino, String descripcion, String url, int precio, Calendar fechaSalida, Calendar fechaLlegada, String codigo) {
        LugarSalida = lugarSalida;
        LugarDestino = lugarDestino;
        Descripcion = descripcion;
        Url = url;
        Precio = precio;
        FechaSalida = fechaSalida;
        FechaLlegada = fechaLlegada;
        Codigo = codigo;
    }

    public Trip(String lugarSalida, String lugarDestino, String descripcion, int precio, String url) {
        LugarSalida = lugarSalida;
        LugarDestino = lugarDestino;
        Descripcion = descripcion;
        Precio = precio;
        Url = url;
    }

    protected Trip(Parcel in) {
        LugarSalida = in.readString();
        LugarDestino = in.readString();
        Descripcion = in.readString();
        Url = in.readString();
        Precio = in.readInt();
        selected = in.readBoolean();

        FechaSalida = Calendar.getInstance();
        FechaSalida.setTimeInMillis(in.readLong());

        FechaLlegada = Calendar.getInstance();
        FechaLlegada.setTimeInMillis(in.readLong());
        Codigo = in.readString();
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public static List<Trip> generaViajes(int tam) {
        int numRandom, minRandom = 0, maxRandom = Constantes.ciudades.length;
        int maxSalidas = Constantes.lugarSalida.length;
        int numRandomImage, minRandomImage = 0, maxRandomImage = Constantes.urlImagenes.length;

        List<Trip> viajes = new ArrayList<>();
        for (int i = 0; i < tam; i++) {
            int precio = ThreadLocalRandom.current().nextInt(10, 1000);
            numRandom = ThreadLocalRandom.current().nextInt(minRandom, maxRandom);
            numRandomImage = ThreadLocalRandom.current().nextInt(minRandomImage, maxRandomImage);

            String ciudad = Constantes.ciudades[numRandom];
            String image = Constantes.urlImagenes[numRandomImage];
            String salida = Constantes.lugarSalida[i % maxSalidas];

            Calendar fechaSalida = Calendar.getInstance();
            Calendar fechaLlegada = Calendar.getInstance();

            int dayOfYear = ThreadLocalRandom.current().nextInt(1, fechaSalida.getActualMaximum(Calendar.DAY_OF_YEAR) + 1);
            fechaSalida.set(Calendar.YEAR, 2025);
            fechaSalida.set(Calendar.DAY_OF_YEAR, dayOfYear);
            fechaSalida.set(Calendar.HOUR_OF_DAY, 0);
            fechaSalida.set(Calendar.MINUTE, 0);
            fechaSalida.set(Calendar.SECOND, 0);

            int duracionDias = ThreadLocalRandom.current().nextInt(1, 15);
            fechaLlegada.setTimeInMillis(fechaSalida.getTimeInMillis());
            fechaLlegada.add(Calendar.DAY_OF_YEAR, duracionDias);

            Trip trip = new Trip(salida, ciudad, "Viaje desde " + salida + " hasta " + ciudad, precio, image);
            trip.setFechaSalida(fechaSalida);
            trip.setFechaLlegada(fechaLlegada);
            trip.setSelected(false);
            int randomCode = ThreadLocalRandom.current().nextInt(1000, 10000); // Generate random 4-digit number
            trip.setCodigo("TRP-" + randomCode);

            viajes.add(trip);
        }

        return viajes;
    }

    public int getPrecio() {
        return Precio;
    }

    public void setPrecio(int precio) {
        Precio = precio;
    }

    public String getLugarSalida() {
        return LugarSalida;
    }

    public void setLugarSalida(String lugarSalida) {
        LugarSalida = lugarSalida;
    }

    public String getLugarDestino() {
        return LugarDestino;
    }

    public void setLugarDestino(String lugarDestino) {
        LugarDestino = lugarDestino;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public Calendar getFechaSalida() {
        return FechaSalida;
    }

    public void setFechaSalida(Calendar fechaSalida) {
        FechaSalida = fechaSalida;
    }

    public Calendar getFechaLlegada() {
        return FechaLlegada;
    }

    public void setFechaLlegada(Calendar fechaLlegada) {
        FechaLlegada = fechaLlegada;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(LugarSalida);
        parcel.writeString(LugarDestino);
        parcel.writeString(Descripcion);
        parcel.writeString(Url);
        parcel.writeInt(Precio);
        parcel.writeBoolean(selected);
        parcel.writeLong(FechaSalida.getTimeInMillis());
        parcel.writeLong(FechaLlegada.getTimeInMillis());
        parcel.writeString(Codigo);

    }

    @Override
    public String toString() {
        return "Trip{" +
                "LugarSalida='" + LugarSalida + '\'' +
                ", LugarDestino='" + LugarDestino + '\'' +
                ", Descripcion='" + Descripcion + '\'' +
                ", Url='" + Url + '\'' +
                ", Precio=" + Precio +
                ", FechaSalida=" + FechaSalida +
                ", FechaLlegada=" + FechaLlegada +
                ", selected=" + selected +
                ", Codigo='" + Codigo + '\'' +
                '}';
    }
}