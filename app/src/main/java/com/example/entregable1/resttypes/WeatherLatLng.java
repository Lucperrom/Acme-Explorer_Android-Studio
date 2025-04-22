package com.example.entregable1.resttypes;

public class WeatherLatLng {
    private float lan, lon;

    public WeatherLatLng(float lan, float lon) {
        this.lan = lan;
        this.lon = lon;
    }

    public WeatherLatLng() {
    }

    public float getLan() {
        return lan;
    }
    public void setLan(float lan) {
        this.lan = lan;
    }
    public float getLon() {
        return lon;
    }
    public void setLon(float lon) {

    }
}
