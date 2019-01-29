package com.example.android.easyweather.utils;

public class Coordinates {
    public final double latitude;
    public final double longitude;

    public Coordinates(double lat, double lon) {
        latitude = lat;
        longitude = lon;
    }

    @Override
    public String toString() {
        return "Latitude :" + latitude + " Longitude: " + longitude;
    }

    public boolean equals(Coordinates obj) {
        if (obj == null) {
            return false;
        }

        if (this.getClass() != obj.getClass())
        {
            return false;
        }
        return this.longitude == obj.longitude && this.latitude == obj.latitude;
    }
}
