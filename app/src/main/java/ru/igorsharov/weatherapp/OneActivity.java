package ru.igorsharov.weatherapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class OneActivity extends Fragment {

    private final static String TAG = OneActivity.class.getSimpleName();
    //    private final static String WEATHER_INFO = "weather_info";
    private final static String SPINNER_POS = "spinner_pos";
    private final static String CHANGE_LAYOUT = "change_layout";
    private final static String CHECKBOX_TEMPERATURE = "checkbox_temperature";
    private final static String CHECKBOX_PRESSURE = "checkbox_pressure";
    private final static String CHECKBOX_WEATHER_FORECAST = "checkbox_weather_forecast";
    public static final String APP_PREFERENCES = "mysettings";
    private LinearLayout primaryLayout, weatherLayout;
    private Spinner spinner;
    private Button buttonCheck, buttonBack;
    private TextView cityTv, temperatureTodayTv, pressureTodayTv, descWeatherForecastTv, temperatureForecastTv, pressureForecastTv;
    private CheckBox checkBoxTemperature, checkBoxPressure, checkBoxWeatherForecast;
    ListView listView;
    boolean changeLayout;
    SharedPreferences mySharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        initView(view);
//        initListView();
//        Weather.getInstance().initWeather(listView.getCount());
        //        initSpinner();

//        buttonCheck.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                primaryLayout.setVisibility(View.GONE);
//                weatherLayout.setVisibility(View.VISIBLE);
//                changeLayout = true;
//                showWeatherInfo();
//            }
//        });

//        buttonBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                primaryLayout.setVisibility(View.VISIBLE);
//                weatherLayout.setVisibility(View.GONE);
//                changeLayout = false;
//            }
//        });

        // восстанавливаем имеющиеся данные при пересоздании активити
//        restoreInstance(savedInstanceState);
        return view;
    }

    private void initView(View v) {
        primaryLayout = v.findViewById(R.id.primaryLayout);
        weatherLayout = v.findViewById(R.id.weatherLayout);
        spinner = v.findViewById(R.id.spinner);
        buttonCheck = v.findViewById(R.id.buttonCheck);
        buttonBack = v.findViewById(R.id.buttonBack);
        temperatureTodayTv = v.findViewById(R.id.temperatureToday);
        pressureTodayTv = v.findViewById(R.id.pressureToday);
        descWeatherForecastTv = v.findViewById(R.id.descWeatherForecast);
        temperatureForecastTv = v.findViewById(R.id.temperatureForecast);
        pressureForecastTv = v.findViewById(R.id.pressureForecast);
        cityTv = v.findViewById(R.id.city);
        checkBoxTemperature = v.findViewById(R.id.checkBoxTemperature);
        checkBoxPressure = v.findViewById(R.id.checkBoxPressure);
        checkBoxWeatherForecast = v.findViewById(R.id.checkBoxWeatherForecast);
        listView = v.findViewById(R.id.listView);
    }

    private void initListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.city_selection));

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showWeatherInfo(view, i);
            }
        });
    }

    private void initSpinner() {
        // необходимо настроить адаптер, увеличить размер текста
        ArrayAdapter adapter =
                ArrayAdapter.createFromResource(getContext(), R.array.city_selection, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

       private void restoreInstance(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            spinner.setSelection(savedInstanceState.getInt(SPINNER_POS));
            checkBoxTemperature.setChecked(savedInstanceState.getBoolean(CHECKBOX_TEMPERATURE));
            checkBoxPressure.setChecked(savedInstanceState.getBoolean(CHECKBOX_PRESSURE));
            checkBoxWeatherForecast.setChecked(savedInstanceState.getBoolean(CHECKBOX_WEATHER_FORECAST));

            if (savedInstanceState.getBoolean(CHANGE_LAYOUT)) {
                changeLayout = true;
                primaryLayout.setVisibility(View.GONE);
                weatherLayout.setVisibility(View.VISIBLE);
//                showWeatherInfo();
            }
        }
    }


    // срабатывает между onPause() и onStop()
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putInt(SPINNER_POS, (int) spinner.getSelectedItemId());
        outState.putBoolean(CHANGE_LAYOUT, changeLayout);
        outState.putBoolean(CHECKBOX_TEMPERATURE, checkBoxTemperature.isChecked());
        outState.putBoolean(CHECKBOX_PRESSURE, checkBoxPressure.isChecked());
        outState.putBoolean(CHECKBOX_WEATHER_FORECAST, checkBoxWeatherForecast.isChecked());
    }


    private void showWeatherInfo(View view, int pos) {
        // пока без перехода на новое активити
//        Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
//        intent.putExtra(SecondActivity.RECEIVE_WEATHER_INFO, String.valueOf(spinner.getSelectedItem()));
//        startActivity(intent);
//        Log.d(TAG, "spinner position " + spinner.getSelectedItemId());

        // вывод погоды на экран
//        cityTv.setText(String.valueOf(spinner.getSelectedItem()));
//        Log.d(TAG, String.valueOf(checkBoxTemperature.isChecked()));
        cityTv.setText(String.valueOf(((TextView) view).getText()));
        primaryLayout.setVisibility(View.GONE);
        weatherLayout.setVisibility(View.VISIBLE);
        changeLayout = true;
        checkBoxCheck(pos);
    }

    String plus(int temperature) {
        if (temperature > 0) {
            return "+";
        }
        return "";
    }

    int getTemperature(int pos) {
//        return Weather.getInstance().getTemperature((int) spinner.getSelectedItemId());
        return Weather.getInstance().getTemperature(pos);
    }

    int getTemperatureForecast(int pos) {
//        return Weather.getInstance().getTemperatureForecast((int) spinner.getSelectedItemId());
        return Weather.getInstance().getTemperatureForecast(pos);
    }


    private void checkBoxCheck(int pos) {
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


    void setColorOfTemperature(TextView tv, int temperature) {
        tv.setTextColor(ContextCompat.getColor(getContext(), getColorForTemperature(temperature)));
    }
}
