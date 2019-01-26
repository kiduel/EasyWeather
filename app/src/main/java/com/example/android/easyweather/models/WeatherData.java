package com.example.android.easyweather.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherData {

    @SerializedName("dt")
    @Expose
    private long dt;

    @SerializedName("main")
    @Expose
    private Main main;

    @SerializedName("weather")
    @Expose
    private List<Weather>  weatherList;


    public List<Weather> getWeatherList() {
        return weatherList;
    }

    public long getDt() {
        return dt;
    }

    public Main getMain() {
        return main;
    }
}
