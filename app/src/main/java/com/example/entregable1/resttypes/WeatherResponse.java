package com.example.entregable1.resttypes;

import java.util.List;

public class WeatherResponse {
    private List<Weather> weather;
    private String base,name;
    private WeatherConditions main;
    private WeatherWind wind;
    private long timezone, id;
    private int code;

    public WeatherResponse(WeatherLatLng weather, List<Weather> weatherList, String base, String name, WeatherConditions main, WeatherWind wind, long timezone, long id, int code) {
        this.weather = weatherList;
        this.base = base;
        this.name = name;
        this.main = main;
        this.wind = wind;
        this.timezone = timezone;
        this.id = id;
        this.code = code;
    }
    public WeatherResponse() {
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weatherList) {
        this.weather = weatherList;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WeatherConditions getMain() {
        return main;
    }

    public void setMain(WeatherConditions main) {
        this.main = main;
    }

    public WeatherWind getWind() {
        return wind;
    }

    public void setWind(WeatherWind wind) {
        this.wind = wind;
    }

    public long getTimezone() {
        return timezone;
    }

    public void setTimezone(long timezone) {
        this.timezone = timezone;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
