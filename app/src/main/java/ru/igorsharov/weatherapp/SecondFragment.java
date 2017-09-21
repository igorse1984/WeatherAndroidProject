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

import java.util.HashMap;

public class SecondFragment extends Fragment {

    public final static String CHECKBOX_STATUSES = "checkBoxesStatus";
    public final static String CITY = "city";
    public final static String LV_POSITION = "lvPosition";
    public final static String CH_TEMPERATURE = "chTemperature";
    public final static String CH_PRESSURE = "chPressure";
    public final static String CH_FORECAST = "chForecast";


    private final static String TAG = SecondFragment.class.getSimpleName();
    private TextView tvCity, tvTemperatureToday, tvPressureToday, tvDescWeatherForecast, tvTemperatureForecast, tvPressureForecast;
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

    @Override
    public void onStart() {
        super.onStart();
        tvCity.setText(b.getString(CITY));
        setTextViewOfOption((HashMap) b.getSerializable(CHECKBOX_STATUSES), b.getInt(LV_POSITION));
    }

    void setWeather(Bundle b) {
        this.b = b;
    }

    void buttonBack() {

    }

    private void setTextViewOfOption(HashMap hm, int pos) {
        // обработка чекбоксов

        // отображение температуры
        if ((boolean) hm.get(CH_TEMPERATURE)) {
            tvTemperatureToday.setVisibility(View.VISIBLE);

            // определяем цвет TextView для отображаемой температуры
            setColorOfTemperature(tvTemperatureToday, getTemperature(pos));
            Log.d(TAG, "pos " + pos);
            tvTemperatureToday.setText(plus(getTemperature(pos)).concat(String.valueOf(getTemperature(pos)).concat("°")));
        } else {
            tvTemperatureToday.setVisibility(View.GONE);
        }

        // давление воздуха
        if ((boolean) hm.get(CH_PRESSURE)) {
            tvPressureToday.setVisibility(View.VISIBLE);
            tvPressureToday.setText(String.valueOf(Weather.getInstance().getPressure(pos)).concat(getResources().getString(R.string.pressure1)));
        } else {
            tvPressureToday.setVisibility(View.GONE);
        }

        // прогноз погоды
        if ((boolean) hm.get(CH_FORECAST)) {
            tvDescWeatherForecast.setVisibility(View.VISIBLE);
            tvTemperatureForecast.setVisibility(View.VISIBLE);
            tvPressureForecast.setVisibility(View.VISIBLE);
            // определяем цвет TextView для отображаемой температуры
            setColorOfTemperature(tvTemperatureForecast, getTemperatureForecast(pos));
            tvTemperatureForecast.setText(plus(getTemperatureForecast(pos)).concat(String.valueOf(getTemperatureForecast(pos)).concat("°")));
            tvPressureForecast.setText(String.valueOf(Weather.getInstance().getPressureForecast(pos)).concat(getResources().getString(R.string.pressure1)));
        } else {
            tvDescWeatherForecast.setVisibility(View.GONE);
            tvTemperatureForecast.setVisibility(View.GONE);
            tvPressureForecast.setVisibility(View.GONE);
        }
    }

    int getTemperature(int pos) {
        return Weather.getInstance().getTemperature(pos);
    }

    int getTemperatureForecast(int pos) {
        return Weather.getInstance().getTemperatureForecast(pos);
    }

    String plus(int temperature) {
        if (temperature > 0) {
            return "+";
        }
        return "";
    }

    void setColorOfTemperature(TextView tv, int temperature) {
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
