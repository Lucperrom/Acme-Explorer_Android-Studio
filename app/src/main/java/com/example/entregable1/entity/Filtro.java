package com.example.entregable1.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

public class Filtro implements Parcelable {
    private int precioMax, precioMin;
    private Calendar fechaIni, fechaFin;

    private Boolean selected;

    public Filtro(int precioMin, int precioMax, Calendar fechaIni, Calendar fechaFin, Boolean selected) {

        this.precioMax = precioMax;
        this.precioMin = precioMin;
        this.fechaIni = fechaIni;
        this.fechaFin = fechaFin;
        this.selected = (selected != null) ? selected : false;;

    }
    public Filtro(){

        precioMax=Integer.MAX_VALUE;
        precioMin=Integer.MIN_VALUE;
        selected=false;
        fechaIni = Calendar.getInstance();
        fechaIni.set(0, 0, 1); // Año 0, enero 1

        fechaFin = Calendar.getInstance();
        fechaFin.set(9999, 11, 31);

    }

    protected Filtro(Parcel in) {

        precioMax = in.readInt();
        precioMin = in.readInt();
        selected=in.readBoolean();
        long fechaIniMillis = in.readLong();
        if (fechaIniMillis != -1) {
            fechaIni = Calendar.getInstance();
            fechaIni.setTimeInMillis(fechaIniMillis);
        }

        long fechaFinMillis = in.readLong();
        if (fechaFinMillis != -1) {
            fechaFin = Calendar.getInstance();
            fechaFin.setTimeInMillis(fechaFinMillis);
        }


    }

    public static final Creator<Filtro> CREATOR = new Creator<Filtro>() {
        @Override
        public Filtro createFromParcel(Parcel in) {
            return new Filtro(in);
        }

        @Override
        public Filtro[] newArray(int size) {
            return new Filtro[size];
        }
    };


    public int getPrecioMax() {
        return precioMax;
    }

    public void setPrecioMax(int precioMax) {
        this.precioMax = precioMax;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }


    public int getPrecioMin() {
        return precioMin;
    }

    public void setPrecioMin(int precioMin) {
        this.precioMin = precioMin;
    }

    public Calendar getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(Calendar fechaIni) {
        this.fechaIni = fechaIni;
    }

    public Calendar getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Calendar fechaFin) {
        this.fechaFin = fechaFin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(precioMax);
        parcel.writeInt(precioMin);
        parcel.writeBoolean(selected);
        parcel.writeLong(fechaIni != null ? fechaIni.getTimeInMillis() : -1);
        parcel.writeLong(fechaFin != null ? fechaFin.getTimeInMillis() : -1);
    }
}
