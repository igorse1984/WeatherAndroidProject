package ru.igorsharov.weatherapp;

import android.app.Fragment;
import android.os.AsyncTask;
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
    public final static String CITY_KEY = "city";
    public final static String TEMPERATURE_SHOW_KEY = "temperature";
    public final static String PRESSURE_SHOW_KEY = "pressure";
    public final static String FORECAST_SHOW_KEY = "forecast";
    public final static String ID_DB_CITY_KEY = "ID";
    private TextView tvCity;
    private TextView tvTemperatureToday;
    private TextView tvPressureToday;
    private TextView tvTemperatureForecast;
    private TextView tvPressureForecast;
    private TextView tvLocation;
    private String city;
    private String lng;
    private String lat;
    private long id;
    Bundle b;
    DataWeatherHandler dataWeatherHandler = DataWeatherHandler.getInstance();

    interface SecondFragmentInterface {
        void clickButtonBackOnSecondFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_second, container, false);

        Button buttonBack = view.findViewById(R.id.buttonBack);
        tvTemperatureToday = view.findViewById(R.id.temperatureToday);
        tvPressureToday = view.findViewById(R.id.pressureToday);
        tvTemperatureForecast = view.findViewById(R.id.temperatureForecast);
        tvPressureForecast = view.findViewById(R.id.pressureForecast);
        tvCity = view.findViewById(R.id.cityName);
        tvLocation = view.findViewById(R.id.locationWeatherPoint);

        TextView tvDescWeatherForecast = view.findViewById(R.id.descWeatherForecast);
        setView(tvDescWeatherForecast, FORECAST_SHOW_KEY, null);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SecondFragmentInterface secondFragmentInterface = (SecondFragmentInterface) getActivity();
                secondFragmentInterface.clickButtonBackOnSecondFragment();
            }
        });

        return view;
    }

    // TODO реализовать метод расшаривания друзьям
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
        //TODO по полученному id из первого фрагмента брать название города из базы
        city = b.getString(CITY_KEY);
        tvCity.setText(city);

        // TODO запихать все три, наверное в ArrayList или HashMap
        id = b.getLong(ID_DB_CITY_KEY);
        lng = dataWeatherHandler.getLongitude(city);
        lat = dataWeatherHandler.getLatitude(city);

        loadWeather();
    }

    // загрузка данных о погоде
    private void loadWeather() {
        //загрузка погоды на сегодня
        LoadWeatherTask todayWeatherTask = new LoadWeatherTask();
        //Запускаем задачу
        todayWeatherTask.execute(b.getBoolean(FORECAST_SHOW_KEY));
    }

    //метод для полученя Bundle из другой активити до начала работы системных методов данного фрагмента
    void setWeather(Bundle b) {
        this.b = b;
    }

    private void setViewToday() {
        tvLocation.setText(dataWeatherHandler.getLocation(city));
        //инициализация View в соответствии с настройками чекбоксов
        //основная температура
        setView(tvTemperatureToday, TEMPERATURE_SHOW_KEY, dataWeatherHandler.getTemperature(city));

        //давление воздуха
        setView(tvPressureToday, PRESSURE_SHOW_KEY, dataWeatherHandler.getPressure(city));
    }

    private void setViewForecast() {
        //прогноз погоды
        setView(
                tvTemperatureForecast, b.getBoolean(FORECAST_SHOW_KEY) ? TEMPERATURE_SHOW_KEY : FORECAST_SHOW_KEY,
                dataWeatherHandler.getTemperatureForecast(city));
        setView(tvPressureForecast, b.getBoolean(FORECAST_SHOW_KEY) ? PRESSURE_SHOW_KEY : FORECAST_SHOW_KEY,
                dataWeatherHandler.getPressureForecast(city));
    }


    private void setView(TextView v, String paramShowKey, String value) {
        if (b.getBoolean(paramShowKey)) {
            v.setVisibility(View.VISIBLE);

            // определяем цвет TextView если это температура
            if (paramShowKey.equals(TEMPERATURE_SHOW_KEY)) {
                defineColorDependOfTemp(v, value);
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
        if (Double.valueOf(temperature) > 0) {
            return "+";
        }
        return "";
    }

    void defineColorDependOfTemp(TextView tv, String temperature) {
        tv.setTextColor(ContextCompat.getColor(getActivity(), getColorForTemperature(temperature)));
    }

    private int getColorForTemperature(String w) {
        Log.d(LOG_TAG, w);
        double weather = Double.valueOf(w);
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

    private class LoadWeatherTask extends AsyncTask<Boolean, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... params) {
            boolean isForecast = params[0];
            DataWeatherHandler.getInstance().loadWeather(id, lng, lat, false);
            if (isForecast) {
                DataWeatherHandler.getInstance().loadWeather(id, lng, lat, true);
            }
            return isForecast;
        }

        @Override
        protected void onPostExecute(Boolean isForecast) {
            setViewToday();
            if (isForecast) {
                setViewForecast();
            }
        }
    }

}
