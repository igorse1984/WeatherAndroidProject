package ru.igorsharov.weatherapp;

import android.app.Fragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import ru.igorsharov.weatherapp.DBdata.Adapters.ForecastSimpleAdapter;
import ru.igorsharov.weatherapp.DBdata.DBWeather;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract;
import ru.igorsharov.weatherapp.DataHandler.DataWeatherHandler;
import ru.igorsharov.weatherapp.DataHandler.DbUtils;

public class ForecastFragment extends Fragment {

    private final static String LOG_TAG = ForecastFragment.class.getSimpleName();
    public final static String TEMPERATURE_SHOW_KEY = "temperature";
    public final static String PRESSURE_SHOW_KEY = "pressure";
    public final static String ID_DB_CITY_KEY = "ID";
    private String TABLE_NAME;
    private TextView tvCity;
    private TextView tvTemperatureToday;
    private TextView tvPressureToday;
    private TextView tvLocation;
    private ImageView ivIconToday;
    private NavigationView navigationView;
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

        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        Button buttonBack = view.findViewById(R.id.buttonBack);
        tvTemperatureToday = view.findViewById(R.id.temperatureToday);
        tvPressureToday = view.findViewById(R.id.pressureToday);
        tvCity = view.findViewById(R.id.cityName);
        tvLocation = view.findViewById(R.id.locationWeatherPoint);
        listViewForecast = view.findViewById(R.id.lvForecast);
        ivIconToday = view.findViewById(R.id.imageViewToday);
        navigationView = getActivity().findViewById(R.id.nav_view);

        // слушатель на кнопку Назад
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SecondFragmentInterface anInterface = (SecondFragmentInterface) getActivity();
                anInterface.clickButtonBackOnSecondFragment();
                DbUtils.delTable(id, TABLE_NAME);
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
        String[] from = DBWeatherContract.DBKeys.keysOfForecastAdapter;

        //Create simple Cursor adapter
        lvForecastAdapter = new ForecastSimpleAdapter(
                getActivity(),
                R.layout.forecast_list_item,
                cursorForList,
                from,
                null,
                1,
                new boolean[]{bundle.getBoolean(TEMPERATURE_SHOW_KEY), bundle.getBoolean(PRESSURE_SHOW_KEY)});

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
            DbUtils.putForecastTableName(id, TABLE_NAME);
        }

        // достаем координаты для последующего запроса прогноза
        String lng = DbUtils.getLongitude(id);
        String lat = DbUtils.getLatitude(id);

        setViewToday();

        // запуск задачи загрузки погоды
        new LoadWeatherTask(this).execute(TABLE_NAME, lng, lat);
//        spillCursorReNew();
    }

    private boolean createAdditionallyTable() {
        // генерирование имени для таблицы прогноза
        TABLE_NAME = "tableForecastOfCityId".concat(id);
        // создание доп таблицы для прогноза
        // с получение результата создания
        return db.putTable(db.getWritableDatabase(), TABLE_NAME);

    }

    //метод для полученя Bundle из другой активити до начала работы callback методов данного фрагмента
    void setWeather(Bundle b) {
        this.bundle = b;
    }

    // установки текущих значений города в заголовке
    private void setViewToday() {
        // отображение названия текущего города
        String city = DbUtils.getCityFromDb(id);
        tvCity.setText(city);

        // отображение локации поголной точки
        tvLocation.setText(DbUtils.getLocation(id));

        // устанавливаем название города в Navigation View
        TextView headerTextView = navigationView.findViewById(R.id.tvNavHandlerCity);
        headerTextView.setVisibility(View.VISIBLE);
        headerTextView.setText(city);

        //инициализация View в соответствии с настройками чекбоксов
        //основная температура
        if (bundle.getBoolean(TEMPERATURE_SHOW_KEY)) {
            setTemperatureView(tvTemperatureToday, DbUtils.getTemperature(id));
        }

        if (bundle.getBoolean(PRESSURE_SHOW_KEY)) {
            //давление воздуха
            setPressureView(tvPressureToday, DbUtils.getPressure(id));
        }

        setIconView(ivIconToday, DbUtils.getIconToday(id));
    }

    private void setTemperatureView(TextView v, String value) {
        if (value != null) {
            v.setVisibility(View.VISIBLE);
            // определяем цвет TextView
            v.setTextColor(DataWeatherHandler.colorOfTemp(getActivity(), value));
            // получаем значение температуры и добавляем символ градуса
            String temperature = DataWeatherHandler.addDegree(value);
            // устанавливаем полученное значение
            v.setText(temperature);

            // устанавливаем температуру в Navigation View
            TextView headerTextView = navigationView.findViewById(R.id.tvNavHandlerTemp);
            headerTextView.setVisibility(View.VISIBLE);
            headerTextView.setText(temperature);
        }
    }

    private void setPressureView(TextView v, String value) {
        if (value != null) {
            v.setVisibility(View.VISIBLE);
            v.setText(value.concat(" ").concat(getResources().getString(R.string.pressure_amount)));
        }
    }

    private void setIconView(ImageView iv, String icon) {
        if (icon != null) {
            iv.setVisibility(View.VISIBLE);
            iv.setImageResource(DataWeatherHandler.getIconId(icon));

            // устанавливаем картинку погоды в Navigation View
            ImageView headerImageView = navigationView.findViewById(R.id.ivNavHeader);
            headerImageView.setImageResource(DataWeatherHandler.getIconId(icon));
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

        private void spillCursorReNew() {
            // получаем слабую ссылку на фрагмент для доступа к его методам
            ForecastFragment forecastFragment = activityReference.get();
            if (forecastFragment == null) return;
            forecastFragment.cursorReNew();
        }

        @Override
        protected void onPreExecute() {
            spillCursorReNew();
        }

        @Override
        protected Void doInBackground(String... params) {
            // загрузка, парсинг и запись прогноза в БД
            DataWeatherHandler.loadForecast(params[0], params[1], params[2]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            spillCursorReNew();
        }
    }

}
