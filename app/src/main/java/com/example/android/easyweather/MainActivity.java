package com.example.android.easyweather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.easyweather.interfaces.WeatherInterface;
import com.example.android.easyweather.models.CurrentDayApiResult;
import com.example.android.easyweather.models.WeatherApiResult;
import com.example.android.easyweather.models.WeatherData;
import com.example.android.easyweather.rest.ApiClient;
import com.example.android.easyweather.utils.Coordinates;
import com.example.android.easyweather.utils.LocationUtils;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double Lat, Lon;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private static final int REQUEST_PERMISSIONS_LOCATION_SETTINGS_REQUEST_CODE = 33;
    private String city="";
    private TextView cityTextView;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION };
    private WeatherAdapter weatherAdapter;
    private List<WeatherData>  weatherDataList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private TextView temp,minTemp,maxTemp,humidity,pressure;
    private Dialog dialog;
    private ConstraintLayout constraintLayout;
    Coordinates coordinates;
    Coordinates coordinates_two;

    String x;
    private final String API_KEY = "5fb870fb64c3c947b40bbadde5601b4f";

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cityTextView=findViewById(R.id.city);

        recyclerView=findViewById(R.id.recylerView);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        temp=findViewById(R.id.temp);
        minTemp=findViewById(R.id.minTemp);
        maxTemp=findViewById(R.id.maxTemp);
        humidity=findViewById(R.id.humidity);
        pressure=findViewById(R.id.pressure);

        constraintLayout=findViewById(R.id.container);


        dialog=new Dialog(MainActivity.this);

        dialog.setContentView(R.layout.dialog_layout);

        dialog.show();

        checkPermissions();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if ( locationResult != null) {


                    /*
                    The lat and log are saved in coordinates object,
                    then network is called only when the coordinate change.
                     */
                    Lat = locationResult.getLastLocation().getLatitude();
                    Lon = locationResult.getLastLocation().getLongitude();

                    if (coordinates == null){
                        coordinates = new Coordinates(Lat, Lon);
                        city = LocationUtils.hereLocation(Lat,Lon,MainActivity.this);

                        setDefaultCityWeather(city);
                        getCurrentCityWeather(city);
                    } else {
                        coordinates_two = new Coordinates(Lat, Lon);
                        if (!coordinates.equals(coordinates_two)) {
                            city = LocationUtils.hereLocation(Lat,Lon,MainActivity.this);
                            setDefaultCityWeather(city);
                            getCurrentCityWeather(city);
                        }
                    }

                    cityTextView.setText(String.valueOf(city));
                    constraintLayout.setVisibility(View.VISIBLE);
                    dialog.hide();

                }
            }
        };

        checkForLocationRequest();
        checkForLocationSettings();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        finish();
                        return;
                    }
                }

                checkForLocationRequest();
                checkForLocationSettings();


                break;
        }
    }

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }


    private void checkForLocationSettings() {

        try {

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.addLocationRequest(locationRequest);
            SettingsClient settingsClient = LocationServices.getSettingsClient(MainActivity.this);
            Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
            task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {


                    requestLocationUpate();
                }
            });

            task.addOnFailureListener(MainActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode){

                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(MainActivity.this, REQUEST_PERMISSIONS_LOCATION_SETTINGS_REQUEST_CODE);
                            } catch (IntentSender.SendIntentException sie) {
                                sie.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Toast.makeText(MainActivity.this, "Please try another the device", Toast.LENGTH_LONG).show();
                    }
                }
            });


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if(fusedLocationProviderClient!=null){
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }

    }

    private void checkForLocationRequest() {

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        // locationRequest.setNumUpdates(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }


    private void requestLocationUpate(){

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_PERMISSIONS_LOCATION_SETTINGS_REQUEST_CODE){
            if(resultCode== Activity.RESULT_OK){

                requestLocationUpate();

            } else if(resultCode== Activity.RESULT_CANCELED){

                finish();

            }


        }


    }

    private void getCurrentCityWeather(String city){

            WeatherInterface weatherInterface = ApiClient.getRetrofitInstance().create(WeatherInterface.class);

            final Call<WeatherApiResult> weatherApiResultCall = weatherInterface.getWeather(city, API_KEY);
            weatherApiResultCall.enqueue(new Callback<WeatherApiResult>() {
                @Override
                public void onResponse(Call<WeatherApiResult> call, Response<WeatherApiResult> response) {
                    if ( response.isSuccessful() ) {

                        weatherDataList = response.body().getWeatherDataList();

                        List<WeatherData> weatherData = new ArrayList<>();

                        for (int i = 0; i < weatherDataList.size() / 8; i++) {

                            weatherData.add(weatherDataList.get(i * 8));
                        }


                        weatherAdapter = new WeatherAdapter(MainActivity.this, weatherData);

                        recyclerView.setAdapter(weatherAdapter);


                    } else {

                        Toast.makeText(MainActivity.this, "No", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<WeatherApiResult> call, Throwable t) {

                    Toast.makeText(MainActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


    }


    private void setDefaultCityWeather(String city){


        WeatherInterface  weatherInterface=ApiClient.getRetrofitInstance().create(WeatherInterface.class);

        Call<CurrentDayApiResult> currentDayApiResultCall=weatherInterface.getCurrenDay(city,API_KEY);
        currentDayApiResultCall.enqueue(new Callback<CurrentDayApiResult>() {
            @Override
            public void onResponse(Call<CurrentDayApiResult> call, Response<CurrentDayApiResult> response) {
                if(response.isSuccessful()){

                    if(response.body()!=null){

                        temp.setText(String.valueOf(response.body().getCurrentDayApi().getTemp()));
                        minTemp.setText(String.valueOf(response.body().getCurrentDayApi().getTemp_min()));
                        maxTemp.setText(String.valueOf(response.body().getCurrentDayApi().getTemp_max()));
                        humidity.setText(String.valueOf(response.body().getCurrentDayApi().getHumidity()));
                        pressure.setText(String.valueOf(response.body().getCurrentDayApi().getPressure()));
                    }

                }
            }

            @Override
            public void onFailure(Call<CurrentDayApiResult> call, Throwable t) {

                Toast.makeText(MainActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

}






