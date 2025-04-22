package com.example.entregable1.resttypes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherRetrofitInterface {

    @GET("data/2.5/weather")
    Call<WeatherResponse> getCurrentWeather(
            @Query("lat") Float lat,
            @Query("lon") Float lon,
            @Query("appid") String appid,
            @Query("lang") String lang,
            @Query("units") String units
    );

}
