package ru.igorsharov.weatherapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SecondFragment extends Fragment {

    private final static String LOG_TAG = SecondFragment.class.getSimpleName();
    public final static String CITY = "city";
    public final static String SHOW_TEMPERATURE = "temperature";
    public final static String SHOW_PRESSURE = "pressure";
    public final static String SHOW_FORECAST = "forecast";
    private TextView tvCity;
    private TextView tvTemperatureToday;
    private TextView tvPressureToday;
    private TextView tvDescWeatherForecast;
    private TextView tvTemperatureForecast;
    private TextView tvPressureForecast;
    Bundle b;
    String temperature;
    String temperatureForecast;
    String pressure;
    String pressureForecast;
    WeatherBox wBox = WeatherBox.getInstance();

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
        String city = b.getString(CITY);
        tvCity.setText(city);

        temperature = wBox.getTemperature(city);
        temperatureForecast = wBox.getTemperatureForecast(city);
        pressure = wBox.getPressure(city);
        pressureForecast = wBox.getPressureForecast(city);

        setTextViewOfOption();
    }

    void setWeather(Bundle b) {
        this.b = b;
    }

    private void setTextViewOfOption() {
        // обработка чекбоксов
        // отображение основной температуры
        setWeatherView(tvTemperatureToday, SHOW_TEMPERATURE, temperature, true);

        // давление воздуха
        setWeatherView(tvPressureToday, SHOW_PRESSURE, pressure, false);

        // прогноз погоды
        setWeatherView(tvTemperatureForecast, b.getBoolean(SHOW_FORECAST) ? SHOW_TEMPERATURE : SHOW_FORECAST, temperatureForecast, true);
        setWeatherView(tvPressureForecast, b.getBoolean(SHOW_FORECAST) ? SHOW_PRESSURE : SHOW_FORECAST, pressureForecast, false);
        setWeatherView(tvDescWeatherForecast, SHOW_FORECAST, null, false);
    }

    private void setWeatherView(TextView v, String showParamKey, String value, boolean isTemp) {
        if (b.getBoolean(showParamKey)) {
            v.setVisibility(View.VISIBLE);

            /* определяем цвет TextView если это температура */
            if (isTemp) {
                setTextColorOfTemperature(v, value);
                v.setText(plus(value).concat(value.concat(" ℃")));
            } else {
                if (value != null) {
                    v.setText(value.concat(" ").concat(getResources().getString(R.string.pressure1)));
                }
            }
        } else {
            v.setVisibility(View.GONE);
        }
    }

    String plus(String temperature) {
        if (Integer.valueOf(temperature) > 0) {
            return "+";
        }
        return "";
    }

    void setTextColorOfTemperature(TextView tv, String temperature) {
        tv.setTextColor(ContextCompat.getColor(getActivity(), getColorForTemperature(temperature)));
    }

    private int getColorForTemperature(String w) {
        Log.d(LOG_TAG, w);
        int weather = Integer.valueOf(w);
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
