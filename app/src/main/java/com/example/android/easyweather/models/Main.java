package com.example.android.easyweather.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {

    @SerializedName("temp_min")
    @Expose
    private double temp_min;

    @SerializedName("temp_max")
    @Expose
    private double temp_max;

    public double getTemp_min() {
        return temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }

    @SerializedName("humidity")
    @Expose
    private double humidity;

    public double getHumidity() {
        return humidity;
    }
}
