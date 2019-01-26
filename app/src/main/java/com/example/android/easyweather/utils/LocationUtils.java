package com.example.android.easyweather.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtils {

    //Returns the current city
    public static String current_city(Context context) {
        String city = "";

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return TODO;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        city = hereLocation(location.getLatitude(), location.getLongitude(), context);


        return city;
    }


    private static String hereLocation(double lat, double lon, Context context) {
        String cityName = "";


        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 10);
            if (addresses.size() > 0) {
                for (Address adr : addresses) {
                    if (adr.getLocality() != null && adr.getLocality().length() > 0) {
                        cityName = adr.getLocality();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cityName;
    }




}
