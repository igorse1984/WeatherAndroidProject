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

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import ru.igorsharov.weatherapp.DBdata.DBWeather;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract;
import ru.igorsharov.weatherapp.DBdata.ForecastSimpleAdapter;

public class ForecastFragment extends Fragment {

    private final static String LOG_TAG = ForecastFragment.class.getSimpleName();
    public final static String TEMPERATURE_SHOW_KEY = "temperature";
    public final static String PRESSURE_SHOW_KEY = "pressure";
    public final static String FORECAST_SHOW_KEY = "forecast";
    public final static String ID_DB_CITY_KEY = "ID";
    private String TABLE_NAME;
    private TextView tvCity;
    private TextView tvTemperatureToday;
    private TextView tvPressureToday;
    private TextView tvLocation;
    private String id;
    private ForecastSimpleAdapter lvForecastAdapter;
    private Cursor cursorForList;
    private DBWeather db = AppDB.getDb();
    private ListView listViewForecast;
    private Bundle bundle;

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
                DataWeatherHandler.DbUtils.delTable(id, TABLE_NAME);
            }
        });

        setDbListAdapter();
        return view;
    }

    // обновление listView
    private void cursorReNew() {
        cursorForList = db.getReadableCursor(TABLE_NAME);
        lvForecastAdapter.swapCursor(cursorForList);
    }

    private void setDbListAdapter() {
        //Create arrays of columns and UI elements
        String[] from = DBWeatherContract.DBKeys.fromForForecastAdapter;

        //Create simple Cursor adapter
        lvForecastAdapter = new ForecastSimpleAdapter(getActivity(),
                R.layout.forecast_list_item, cursorForList, from, null, 1);

        listViewForecast.setAdapter(lvForecastAdapter);
    }

    // TODO реализовать метод расшаривания друзьям
    // передача неявного интента
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent1 = new Intent(Intent.ACTION_SEND);
//                intent1.setType("text/plain");
//                intent1.putExtra(Intent.EXTRA_TEXT, "Погода в городе " + id + ": " + weather);
//                if (intent1.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent1);
//                }
//            }
//        });

    @Override
    public void onStart() {
        super.onStart();

        // id строки из предыдущего фрагмента
        id = bundle.getString(ID_DB_CITY_KEY);

        // в случае успешного создания доп таблицы
        if (createAdditionallyTable()) {
            // запись названия доп таблицы в основную
            DataWeatherHandler.DbUtils.putForecastTableName(id, TABLE_NAME);
        }
        // отображение во фрагменте названия текущего города
        tvCity.setText(DataWeatherHandler.DbUtils.getCityFromDb(id));
        // достаем координаты для последующего запроса прогноза
        String lng = DataWeatherHandler.DbUtils.getLongitude(id);
        String lat = DataWeatherHandler.DbUtils.getLatitude(id);
        // запуск задачи загрузки погоды
        new LoadWeatherTask(this).execute(TABLE_NAME, lng, lat);
//        cursorReNew();
    }

    private boolean createAdditionallyTable() {
        // генерирование имени для таблицы прогноза
        TABLE_NAME = "tableForecastOfCityId".concat(id);
        // создание доп таблицы для прогноза
        // с получение результата создания
        return db.putTable(db.getWritableDatabase(), TABLE_NAME);

    }

    //метод для полученя Bundle из другой активити до начала работы системных методов данного фрагмента
    void setWeather(Bundle b) {
        this.bundle = b;
    }

    // метод запускается по завершению AsyncTask
    private void setViewToday() {
        // TODO переделать
        tvLocation.setText(DataWeatherHandler.DbUtils.getLocation(id));
        //инициализация View в соответствии с настройками чекбоксов
        //основная температура
        setView(tvTemperatureToday, TEMPERATURE_SHOW_KEY, DataWeatherHandler.DbUtils.getTemperature(id));

        //давление воздуха
        setView(tvPressureToday, PRESSURE_SHOW_KEY, DataWeatherHandler.DbUtils.getPressure(id));
    }


    private void setView(TextView v, String paramShowKey, String value) {
        if (bundle.getBoolean(paramShowKey)) {
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

    private static class LoadWeatherTask extends AsyncTask<String, Void, Void> {

        /**
         * для борьбы с утечками памяти делаем AsyncTask статическим и
         * создаем экземпляр WeakReference (слабая ссылка)
         * для возможности доступа AsyncTask к нестатическим методам фрагента
         */
        private WeakReference<ForecastFragment> activityReference;

        // конструктор AsyncTask
        LoadWeatherTask(ForecastFragment forecastFragment) {
            activityReference = new WeakReference<>(forecastFragment);
        }

        @Override
        protected Void doInBackground(String... params) {
            String TABLE_NAME = params[0];
            String lng = params[1];
            String lat = params[2];
            JSONObject jo = DataWeatherHandler.NetUtils.loadWeather(lng, lat, true);
            DataWeatherHandler.ParsingUtils.setWeatherJSONParser(jo);
            // TODO оптимизировать дублирование
            DataWeatherHandler.DbUtils.putWeatherValuesArrInDb(
                    TABLE_NAME,
                    DBWeatherContract.DBKeys.C_DATE,
                    DataWeatherHandler.ParsingUtils.parseDateForecast());

            DataWeatherHandler.DbUtils.updWeatherValuesArrInDb(
                    TABLE_NAME,
                    DBWeatherContract.DBKeys.C_TEMPERATURE,
                    DataWeatherHandler.ParsingUtils.parseTempForecast());

            DataWeatherHandler.DbUtils.updWeatherValuesArrInDb(
                    TABLE_NAME,
                    DBWeatherContract.DBKeys.C_PRESSURE,
                    DataWeatherHandler.ParsingUtils.parsePressureForecast());

            DataWeatherHandler.DbUtils.updWeatherValuesArrInDb(
                    TABLE_NAME,
                    DBWeatherContract.DBKeys.C_ICON_WEATHER,
                    DataWeatherHandler.ParsingUtils.parseIconIdArr());


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            // получаем слабую ссылку на фрагмент для доступа к его методам
            ForecastFragment forecastFragment = activityReference.get();
            if (forecastFragment == null) return;

            forecastFragment.setViewToday();
            forecastFragment.cursorReNew();
        }
    }

}
