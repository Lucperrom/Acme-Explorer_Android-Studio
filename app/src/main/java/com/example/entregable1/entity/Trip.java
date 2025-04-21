package com.example.entregable1.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.entregable1.Constantes;
import com.google.firebase.database.Exclude;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Trip implements Parcelable {
    private String LugarSalida, LugarDestino, Descripcion, Url, Codigo;
    private double Precio; // Imprescindible para coincidir con el JSON
    private Date FechaSalida, FechaLlegada; // Imprescindible: Calendar no es instanciable
    private Boolean selected;

    public Trip() {
    }

    public Trip(String lugarSalida, String lugarDestino, String descripcion, String url, double precio, Date fechaSalida, Date fechaLlegada, String codigo) {
        LugarSalida = lugarSalida;
        LugarDestino = lugarDestino;
        Descripcion = descripcion;
        Url = url;
        Precio = precio;
        FechaSalida = fechaSalida;
        FechaLlegada = fechaLlegada;
        Codigo = codigo;
    }

    public Trip(String lugarSalida, String lugarDestino, String descripcion, double precio, String url) {
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
        Precio = in.readDouble();
        selected = in.readBoolean();
        long fechaSalidaMillis = in.readLong();
        FechaSalida = fechaSalidaMillis == 0 ? null : new Date(fechaSalidaMillis);
        long fechaLlegadaMillis = in.readLong();
        FechaLlegada = fechaLlegadaMillis == 0 ? null : new Date(fechaLlegadaMillis);
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

            Trip trip = new Trip(salida, ciudad, "Viaje desde " + salida + " hasta " + ciudad, precio, image);

            trip.setFechaSalida(generarFechaFuturaAleatoriaDate());
            trip.setFechaLlegada(generarFechaFuturaAleatoriaDate());

            trip.setSelected(false);

            int randomCode = ThreadLocalRandom.current().nextInt(1000, 10000); // Generate random 4-digit number
            trip.setCodigo("TRP-" + randomCode);

            viajes.add(trip);

        }
        return viajes;

    }

    public static Date generarFechaFuturaAleatoriaDate() {
        LocalDate fechaActualLocal = LocalDate.now();
        Random random = new Random();

        // Genera un número aleatorio de días en el futuro (por ejemplo, hasta 365 días)
        int diasEnElFuturo = random.nextInt(365) + 1;

        LocalDate fechaFuturaLocal = fechaActualLocal.plusDays(diasEnElFuturo);

        // Convierte LocalDate a Date
        return Date.from(fechaFuturaLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static void main(String[] args) {
        Date fechaFuturaDate = generarFechaFuturaAleatoriaDate();
        System.out.println("Fecha futura aleatoria generada (Date): " + fechaFuturaDate);
    }

    public double getPrecio() {
        return Precio;
    }

    public void setPrecio(double precio) {
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

    public Date getFechaSalida() {
        return FechaSalida;
    }

    public void setFechaSalida(Date fechaSalida) {
        FechaSalida = fechaSalida;
    }

    public Date getFechaLlegada() {
        return FechaLlegada;
    }

    public void setFechaLlegada(Date fechaLlegada) {
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

    @Exclude
    public int getStability() {
        return 0;
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
        parcel.writeDouble(Precio);
        parcel.writeBoolean(selected);
        parcel.writeLong(FechaSalida == null ? 0 : FechaSalida.getTime());
        parcel.writeLong(FechaLlegada == null ? 0 : FechaLlegada.getTime());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return Double.compare(trip.Precio, Precio) == 0 && Objects.equals(LugarSalida, trip.LugarSalida) && Objects.equals(LugarDestino, trip.LugarDestino) && Objects.equals(Descripcion, trip.Descripcion) && Objects.equals(Url, trip.Url) && Objects.equals(Codigo, trip.Codigo) && Objects.equals(FechaSalida, trip.FechaSalida) && Objects.equals(FechaLlegada, trip.FechaLlegada) && Objects.equals(selected, trip.selected);
    }

    @Override
    public int hashCode() {
        return Objects.hash(LugarSalida, LugarDestino, Descripcion, Url, Codigo, Precio, FechaSalida, FechaLlegada, selected);
    }
}