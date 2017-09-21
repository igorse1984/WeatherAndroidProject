package ru.igorsharov.weatherapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SecondFragment extends Fragment {

    public final static String RECEIVE_WEATHER_INFO = "receive_weather_info";

    private TextView cityTv, temperatureTodayTv, pressureTodayTv, descWeatherForecastTv, temperatureForecastTv, pressureForecastTv;
    String city;
    int weather;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_second, container, false);

//            textView = view.findViewById(R.id.textView);
        temperatureTodayTv = view.findViewById(R.id.temperatureToday);
        pressureTodayTv = view.findViewById(R.id.pressureToday);
        descWeatherForecastTv = view.findViewById(R.id.descWeatherForecast);
        temperatureForecastTv = view.findViewById(R.id.temperatureForecast);
        pressureForecastTv = view.findViewById(R.id.pressureForecast);
        cityTv = view.findViewById(R.id.city);
//            weather = WeatherSelector.getTemperature(city);
//            textView.setText(weather);


        // передача неявного интента
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent1 = new Intent(Intent.ACTION_SEND);
//                intent1.setType("text/plain");
//                intent1.putExtra(Intent.EXTRA_TEXT, "Погода в городе " + city + ": " + weather);
//                if (intent1.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent1);
//                }
//            }
//        });
        return view;
    }

    void setWeather(){
                cityTv.setText(String.valueOf(((TextView) view).getText()));
        checkBoxHandler(pos);
    }

    private void checkBoxHandler(int pos) {
        // обработка чекбоксов
        if (checkBoxTemperature.isChecked()) {
            temperatureTodayTv.setVisibility(View.VISIBLE);
            // определяем цвет TextView для отображаемой температуры
            setColorOfTemperature(temperatureTodayTv, getTemperature(pos));
            Log.d(TAG, "pos " + pos);
            temperatureTodayTv.setText(plus(getTemperature(pos)).concat(String.valueOf(getTemperature(pos)).concat("°")));
        } else {
            temperatureTodayTv.setVisibility(View.GONE);
        }

        if (checkBoxPressure.isChecked()) {
            pressureTodayTv.setVisibility(View.VISIBLE);
//            pressureTodayTv.setText(String.valueOf(Weather.getInstance().getPressure((int) spinner.getSelectedItemId())).concat(getResources().getString(R.string.pressure1)));
            pressureTodayTv.setText(String.valueOf(Weather.getInstance().getPressure(pos)).concat(getResources().getString(R.string.pressure1)));
        } else {
            pressureTodayTv.setVisibility(View.GONE);
        }

        if (checkBoxWeatherForecast.isChecked()) {
            descWeatherForecastTv.setVisibility(View.VISIBLE);
            temperatureForecastTv.setVisibility(View.VISIBLE);
            pressureForecastTv.setVisibility(View.VISIBLE);
            // определяем цвет TextView для отображаемой температуры
            setColorOfTemperature(temperatureForecastTv, getTemperatureForecast(pos));
            temperatureForecastTv.setText(plus(getTemperatureForecast(pos)).concat(String.valueOf(getTemperatureForecast(pos)).concat("°")));
//            pressureForecastTv.setText(String.valueOf(Weather.getInstance().getPressureForecast((int) spinner.getSelectedItemId())).concat(getResources().getString(R.string.pressure1)));
            pressureForecastTv.setText(String.valueOf(Weather.getInstance().getPressureForecast(pos)).concat(getResources().getString(R.string.pressure1)));
        } else {
            descWeatherForecastTv.setVisibility(View.GONE);
            temperatureForecastTv.setVisibility(View.GONE);
            pressureForecastTv.setVisibility(View.GONE);
        }
    }
}
