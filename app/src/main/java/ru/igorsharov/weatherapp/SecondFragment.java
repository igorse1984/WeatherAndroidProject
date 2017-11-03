package ru.igorsharov.weatherapp;

import android.app.Fragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import ru.igorsharov.weatherapp.DBdata.CustomSimpleAdapter;
import ru.igorsharov.weatherapp.DBdata.DBWeather;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract;

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
    private TextView tvLocation;
    private String city;
    private CustomSimpleAdapter lvAdapter;
    private Cursor cursorForList;
    private DBWeather db = AppDB.getDb();
    private ListView listViewForecast;
    private boolean isForecast;
    Bundle b;
//    DataWeatherHandler dataWeatherHandler = new DataWeatherHandler();

    interface SecondFragmentInterface {
        void clickButtonBackOnSecondFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_second, container, false);

        Button buttonBack = view.findViewById(R.id.buttonBack);
        tvTemperatureToday = view.findViewById(R.id.temperatureToday);
        tvPressureToday = view.findViewById(R.id.pressureToday);
        tvCity = view.findViewById(R.id.cityName);
        tvLocation = view.findViewById(R.id.locationWeatherPoint);
        listViewForecast = view.findViewById(R.id.lvForecast);

        TextView tvDescWeatherForecast = view.findViewById(R.id.descWeatherForecast);
        setView(tvDescWeatherForecast, FORECAST_SHOW_KEY, null);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SecondFragmentInterface anInterface = (SecondFragmentInterface) getActivity();
                anInterface.clickButtonBackOnSecondFragment();
            }
        });

        setDbListAdapter();
        cursorReNew();

        return view;
    }

    // обновление listView
    private void cursorReNew() {
        cursorForList = db.getReadableCursor(DBWeatherContract.WeatherEntry.T_NAME);
        lvAdapter.swapCursor(cursorForList);
    }

    private void setDbListAdapter() {
        //Create arrays of columns and UI elements
        String[] from = {
                DBWeatherContract.WeatherEntry.C_CITY,
                DBWeatherContract.WeatherEntry.C_TEMPERATURE_FORECAST,
                DBWeatherContract.WeatherEntry.C_ICON_WEATHER};

        //Create simple Cursor adapter
        lvAdapter = new CustomSimpleAdapter(getActivity(),
                R.layout.list_item, cursorForList, from, null, 1);

        listViewForecast.setAdapter(lvAdapter);
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

        long id = b.getLong(ID_DB_CITY_KEY);
        String lng = DataWeatherHandler.DBData.getLongitude(city);
        String lat = DataWeatherHandler.DBData.getLatitude(city);
        isForecast = b.getBoolean(FORECAST_SHOW_KEY);

        loadWeather(id, lng, lat);
    }

    // загрузка данных о погоде
    private void loadWeather(long id, String lng, String lat) {
        //загрузка погоды, запуск задачи
        new LoadWeatherTask(this, id, lng, lat).execute(isForecast);
    }

    //метод для полученя Bundle из другой активити до начала работы системных методов данного фрагмента
    void setWeather(Bundle b) {
        this.b = b;
    }

    private void setViewToday() {
        tvLocation.setText(DataWeatherHandler.DBData.getLocation(city));
        //инициализация View в соответствии с настройками чекбоксов
        //основная температура
        setView(tvTemperatureToday, TEMPERATURE_SHOW_KEY, DataWeatherHandler.DBData.getTemperature(city));

        //давление воздуха
        setView(tvPressureToday, PRESSURE_SHOW_KEY, DataWeatherHandler.DBData.getPressure(city));
    }

//    private void setViewForecast() {
//        //прогноз погоды
//        setView(
//                tvTemperatureForecast, isForecast ? TEMPERATURE_SHOW_KEY : FORECAST_SHOW_KEY,
//                DataWeatherHandler.DBData.getTemperatureForecast(city));
//        setView(tvPressureForecast, isForecast ? PRESSURE_SHOW_KEY : FORECAST_SHOW_KEY,
//                DataWeatherHandler.DBData.getPressureForecast(city));
//    }


    private void setView(TextView v, String paramShowKey, String value) {
        if (b.getBoolean(paramShowKey)) {
            v.setVisibility(View.VISIBLE);

            // определяем цвет TextView если это температура
            if (paramShowKey.equals(TEMPERATURE_SHOW_KEY)) {
                v.setTextColor(DataWeatherHandler.colorOfTemp(getActivity(), value));
                v.setText(DataWeatherHandler.weatherString(value));
            } else {
                if (value != null) {
                    v.setText(value.concat(" ").concat(getResources().getString(R.string.pressure1)));
                }
            }
        } else {
            v.setVisibility(View.GONE);
        }
    }

    private static class LoadWeatherTask extends AsyncTask<Boolean, Void, Boolean> {

        // для борьбы с утечками памяти делаем AsyncTask статическим и
        // создаем экземпляр WeakReference (слабая ссылка)
        // для возможности доступа AsyncTask к нестатическим методам фрагента
        private WeakReference<SecondFragment> activityReference;

        private long id;
        private String lng;
        private String lat;

        // конструктор AsyncTask
        LoadWeatherTask(SecondFragment secondFragment, long id, String lng, String lat) {
            activityReference = new WeakReference<>(secondFragment);
            this.id = id;
            this.lng = lng;
            this.lat = lat;
        }

        // чтобы не дублировать выражение в doInBackground
        private void loadWeather(boolean isForecast) {
            DataWeatherHandler.loadOfOpenWeatherAndUpdDB(id, lng, lat, isForecast);
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            boolean isForecast = params[0];

            loadWeather(false);
            if (isForecast) {
                loadWeather(true);
            }
            return isForecast;
        }

        @Override
        protected void onPostExecute(Boolean isForecast) {

            // получаем слабую ссылку на фрагмент для доступа к его методам
            SecondFragment secondFragment = activityReference.get();
            if (secondFragment == null) return;

            secondFragment.setViewToday();
//            if (isForecast) {
//                secondFragment.setViewForecast();
//            }
        }
    }

}
