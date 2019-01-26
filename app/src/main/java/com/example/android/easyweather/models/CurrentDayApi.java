package com.example.android.easyweather.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentDayApi {

    @SerializedName("temp_min")
    @Expose
    private double temp_min;

    @SerializedName("temp_max")
    @Expose
    private double temp_max;

    @SerializedName("temp")
    @Expose
    private double temp;

    @SerializedName("pressure")
    @Expose
    private double pressure;

    public double getPressure() {
        return pressure;
    }

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

    public double getTemp() {
        return temp;
    }
}

