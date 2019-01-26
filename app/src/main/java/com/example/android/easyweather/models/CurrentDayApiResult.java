package com.example.android.easyweather.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentDayApiResult {

    @SerializedName("main")
    @Expose
    private CurrentDayApi currentDayApi;

    public CurrentDayApi getCurrentDayApi() {
        return currentDayApi;
    }
}
