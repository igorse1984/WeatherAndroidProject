package ru.igorsharov.weatherapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SecondFragment extends Fragment {

    private final static String TAG = SecondFragment.class.getSimpleName();
    public final static String WEATHER_INFO_OF_CITY = "checkBoxesStatus";
    public final static String CITY = "city";
    public final static String SHOW_TEMPERATURE = "temperature";
    public final static String SHOW_PRESSURE = "pressure";
    public final static String SHOW_FORECAST = "forecast";
    private String city;
    private TextView tvCity;
    private TextView tvTemperatureToday;
    private TextView tvPressureToday;
    private TextView tvDescWeatherForecast;
    private TextView tvTemperatureForecast;
    private TextView tvPressureForecast;
    Bundle b;

    interface SecondFragmentInterface {
        void clickButtonBackOnSecondFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_second, container, false);

        Button buttonBack = view.findViewById(R.id.buttonBack);
        tvTemperatureToday = view.findViewById(R.id.temperatureToday);
        tvPressureToday = view.findViewById(R.id.pressureToday);
        tvDescWeatherForecast = view.findViewById(R.id.descWeatherForecast);
        tvTemperatureForecast = view.findViewById(R.id.temperatureForecast);
        tvPressureForecast = view.findViewById(R.id.pressureForecast);
        tvCity = view.findViewById(R.id.city);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SecondFragmentInterface secondFragmentInterface = (SecondFragmentInterface) getActivity();
                secondFragmentInterface.clickButtonBackOnSecondFragment();
            }
        });

        return view;
    }

    // реализовать метод расшаривания друзьям
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

    @Override
    public void onStart() {
        super.onStart();
        city = b.getString(CITY);
        tvCity.setText(city);
        setTextViewOfOption();
    }

    void setWeather(Bundle b) {
        this.b = b;
    }

    private void setTextViewOfOption() {
        // обработка чекбоксов

        // отображение температуры
        if (b.getBoolean(SHOW_TEMPERATURE)) {
            tvTemperatureToday.setVisibility(View.VISIBLE);

            // определяем цвет TextView для отображаемой температуры
            setTextColorOfTemperature(tvTemperatureToday, getOutTemperature(city));
            tvTemperatureToday.setText(plus(getOutTemperature(city)).concat(String.valueOf(getOutTemperature(city)).concat("°")));
        } else {
            tvTemperatureToday.setVisibility(View.GONE);
        }

        // давление воздуха
        if (b.getBoolean(SHOW_PRESSURE)) {
            tvPressureToday.setVisibility(View.VISIBLE);
            tvPressureToday.setText(getOutPressure(city).concat(getResources().getString(R.string.pressure1)));
        } else {
            tvPressureToday.setVisibility(View.GONE);
        }

        // прогноз погоды
        if (b.getBoolean(SHOW_FORECAST)) {
            tvDescWeatherForecast.setVisibility(View.VISIBLE);
            tvTemperatureForecast.setVisibility(View.VISIBLE);
            tvPressureForecast.setVisibility(View.VISIBLE);
            // определяем цвет TextView для отображаемой температуры
            setTextColorOfTemperature(tvTemperatureForecast, getOutTemperatureForecast(city));
            tvTemperatureForecast.setText(plus(getOutTemperatureForecast(city)).concat(String.valueOf(getOutTemperatureForecast(city)).concat("°")));
            tvPressureForecast.setText(getOutPressureForecast(city).concat(getResources().getString(R.string.pressure1)));
        } else {
            tvDescWeatherForecast.setVisibility(View.GONE);
            tvTemperatureForecast.setVisibility(View.GONE);
            tvPressureForecast.setVisibility(View.GONE);
        }
    }

    int getOutTemperature(String city) {
        return WeatherBox.getInstance().getTemperature(city);
    }

    int getOutTemperatureForecast(String city) {
        return WeatherBox.getInstance().getTemperatureForecast(city);
    }

    String getOutPressure(String city) {
        return String.valueOf(WeatherBox.getInstance().getPressure(city));
    }

    String getOutPressureForecast(String city) {
       return String.valueOf(WeatherBox.getInstance().getPressureForecast(city));
    }

    String plus(int temperature) {
        if (temperature > 0) {
            return "+";
        }
        return "";
    }

    void setTextColorOfTemperature(TextView tv, int temperature) {
        tv.setTextColor(ContextCompat.getColor(getActivity(), getColorForTemperature(temperature)));
    }

    private int getColorForTemperature(int weather) {
        if (weather >= 0 && weather < 10) {
            return R.color.color0;
        } else {
            if (weather >= 10 && weather < 20) {
                return R.color.color10;
            } else {
                if (weather >= 20 && weather < 30) {
                    return R.color.color20;
                } else {
                    if (weather >= 30) {
                        return R.color.color30;
                    } else {
                        return R.color.colorCold;
                    }
                }
            }
        }
    }
}
