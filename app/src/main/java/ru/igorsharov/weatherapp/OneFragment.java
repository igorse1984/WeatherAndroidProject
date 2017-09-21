package ru.igorsharov.weatherapp;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

public class OneFragment extends Fragment {

    private final static String TAG = OneFragment.class.getSimpleName();
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


    interface OneFragmentInterface {
        void onListViewSelected(int p);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_one, container, false);
        initView(view);
        initListView();
        Weather.getInstance().initWeather(listView.getCount());
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

        checkBoxTemperature = v.findViewById(R.id.checkBoxTemperature);
        checkBoxPressure = v.findViewById(R.id.checkBoxPressure);
        checkBoxWeatherForecast = v.findViewById(R.id.checkBoxWeatherForecast);
        listView = v.findViewById(R.id.listView);
    }

    private void initListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
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
                ArrayAdapter.createFromResource(getActivity(), R.array.city_selection, android.R.layout.simple_spinner_item);
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
        OneFragmentInterface oneFragmenInterface = (OneFragmentInterface) getActivity();
        oneFragmenInterface.onListViewSelected(pos);
//        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SecondFragment()).commit();
//        cityTv.setText(String.valueOf(((TextView) view).getText()));
//        primaryLayout.setVisibility(View.GONE);
//        weatherLayout.setVisibility(View.VISIBLE);
//        changeLayout = true;
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
        tv.setTextColor(ContextCompat.getColor(getActivity(), getColorForTemperature(temperature)));
    }
}
