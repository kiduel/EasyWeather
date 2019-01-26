package com.example.android.easyweather.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherApiResult {

    @SerializedName("city")
    @Expose
    private City city;

    @SerializedName("list")
    @Expose
    private List<WeatherData> weatherDataList;

    public City getCity() {
        return city;
    }

    public List<WeatherData> getWeatherDataList() {
        return weatherDataList;
    }
}
