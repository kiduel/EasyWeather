package com.example.android.easyweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.easyweather.models.WeatherData;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {


    private Context context;
    private List<WeatherData> weatherDataList;

    public WeatherAdapter(Context context, List<WeatherData> weatherDataList) {
        this.context = context;
        this.weatherDataList = weatherDataList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.bindData(weatherDataList.get(i));

    }

    @Override
    public int getItemCount() {
        return weatherDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView weatherImage;
        private TextView dayWeather;
        private TextView climateWeather;
        private TextView minTemp;
        private TextView maxTemp;
        private TextView humidity;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            weatherImage = itemView.findViewById(R.id.imageView);
            dayWeather = itemView.findViewById(R.id.day);
            climateWeather = itemView.findViewById(R.id.climate);
            maxTemp = itemView.findViewById(R.id.highTemp);
            minTemp = itemView.findViewById(R.id.lowTemp);
            humidity = itemView.findViewById(R.id.humidity);


        }


        private void bindData(WeatherData weatherData) {


            Picasso.get().load("http://openweathermap.org/img/w/" + weatherData.getWeatherList().get(0).getIcon() + ".png").fit().into(weatherImage);


            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(0);

            dayWeather.setText(convertTimeStampToDay(weatherData.getDt()));
            climateWeather.setText(weatherData.getWeatherList().get(0).getDescription());

            maxTemp.setText("High: " + String.valueOf(numberFormat.format(weatherData.getMain().getTemp_max()) + "\u00B0F"));
            minTemp.setText("Low: " + String.valueOf(numberFormat.format(weatherData.getMain().getTemp_min()) + "\u00B0F"));

            humidity.setText("Humidity: " + String.valueOf(NumberFormat.getPercentInstance().format(weatherData.getMain().getHumidity() / 100.0)));


        }


        private String convertTimeStampToDay(long timeStamp) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeStamp * 1000);
            TimeZone tz = TimeZone.getDefault();
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE");
            return dateFormatter.format(calendar.getTime());

        }


    }
}