package ru.igorsharov.weatherapp;

import android.app.Fragment;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ru.igorsharov.weatherapp.DBdata.CustomSimpleAdapter;
import ru.igorsharov.weatherapp.DBdata.DBWeather;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract.WeatherEntry;

public class OneFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, TextWatcher {

    private final static String TAG = OneFragment.class.getSimpleName();
    private final static String UPD_ERROR_MSG = "уже присутствует в списке";
    private final static String UPD_OK_MSG = "Обьект добавлен";
    private final static long MINTIME = 3000L;
    private final static float MINDIST = 1.0F;
    private CheckBox chBoxTemperature, chBoxPressure, chBoxForecast;
    private ListView listView;
    private EditText editTextCityAdd;
    private Button buttonAdd;
    private Button btnFindLoc;
    private CustomSimpleAdapter lvAdapter;
    private DBWeather db = AppDB.getDb();
    private Cursor cursorForList;
    private LocationHelper lochlp;
    private Location loc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        initViews(view);
        setListeners();
        setDbListAdapter();
        cursorReNew();
        initLoc();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        lochlp.setLocListener(MINTIME, MINDIST);
    }

    @Override
    public void onPause() {
        lochlp.removeLocUpd();
        super.onPause();
    }

    private void initViews(View v) {
        chBoxTemperature = v.findViewById(R.id.checkBoxTemperature);
        chBoxPressure = v.findViewById(R.id.checkBoxPressure);
        chBoxForecast = v.findViewById(R.id.checkBoxWeatherForecast);
        listView = v.findViewById(R.id.listView);
        editTextCityAdd = v.findViewById(R.id.editTextCityAdd);
        buttonAdd = v.findViewById(R.id.buttonAdd);
        btnFindLoc = v.findViewById(R.id.btnFindLoc);
    }

    void setListeners() {
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);
        buttonAdd.setOnClickListener(this);
        btnFindLoc.setOnClickListener(this);
        editTextCityAdd.addTextChangedListener(this);
    }

    // обновление listView по новой технологии взамен устаревшего метода requery
    private void cursorReNew() {
        cursorForList = db.getReadableCursor(WeatherEntry.T_NAME);
        lvAdapter.swapCursor(cursorForList);
    }

    private void setDbListAdapter() {
        //Create arrays of columns and UI elements
        String[] from = {
                WeatherEntry.C_CITY,
                WeatherEntry.C_TEMPERATURE_TODAY,
                WeatherEntry.C_ICON_WEATHER};

        //Create simple Cursor adapter
        // null потому что адаптер знает про view элементы
        lvAdapter = new CustomSimpleAdapter(getActivity(),
                R.layout.list_item, cursorForList, from, null, 1);

        listView.setAdapter(lvAdapter);
    }

    void initLoc() {
        lochlp = new LocationHelper(getActivity());
        loc = lochlp.getLastLoc();
    }

    // TODO загружать погоду при добавлении города
    // TODO перенести чекбоксы во второй фрагмент
    // TODO уйти от передачи названия города во второй фрагмент, оставить только id


    // переход по второй фрагмент и передача настроек
    private void showWeatherInfo(long id, String lvCity) {
        Bundle b = new Bundle();

        b.putBoolean(SecondFragment.TEMPERATURE_SHOW_KEY, chBoxTemperature.isChecked());
        b.putBoolean(SecondFragment.PRESSURE_SHOW_KEY, chBoxPressure.isChecked());
        b.putBoolean(SecondFragment.FORECAST_SHOW_KEY, chBoxForecast.isChecked());
        b.putLong(SecondFragment.ID_DB_CITY_KEY, id);
        b.putString(SecondFragment.CITY_KEY, lvCity);


        // обратиться к активити можно либо через создание интерфейса фрагмента,
        // либо через каст с получением ссылки на активити
        ((MainActivity) getActivity()).onListViewSelected(b);
    }

    /**
     * ОБРАБОТКА КЛИКОВ
     */

    // TODO скрывать клавиатуру после нажатия кновки добавления
    // TODO добавить виджет
    // TODO доработать геолокацию

    //Клики по кнопкам
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAdd:
                String city = String.valueOf(editTextCityAdd.getText());
                editTextCityAdd.setText("");
                // получение названия города с GoogleGeo, запускаем в новом потоке
                new DownloadTask(this).execute(city);
                break;

            case R.id.btnFindLoc:
                if (loc != null) {
                    String strLoc = lochlp.getAddressStr(loc);
                     /* Show address */
                    if (strLoc != null) {
                        editTextCityAdd.setText("");
                        editTextCityAdd.setText(strLoc);
                    }
                }
                break;
        }
    }


    //Клики по ListView
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO надо брать город из базы по id
        String lvCity = String.valueOf(((TextView) view.findViewById(R.id.tvCityName)).getText());
        // блокирование отправки серверу сообщения о загрузке вместо названия города
        if (!lvCity.equals(DataWeatherHandler.TEXT_LOAD)) {
            // переход во второй фрагмент с передачей id города в базе
            showWeatherInfo(id, lvCity);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        db.delete(id);
        cursorReNew();
        return true;
    }

    // задаем реакцию EditText
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        boolean isEmpty = (String.valueOf(editTextCityAdd.getText()).equals(""));
        btnFindLoc.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        buttonAdd.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    public void printMessage(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
    }

    // реализация фонового запроса названия города от GoogleGeo по введенным данным пользователя
    // описание к происходящему есть во втором фрагменте
    private static class DownloadTask extends AsyncTask<String, Void, String[]> {

        private WeakReference<OneFragment> activityReference;
        private long id;

        DownloadTask(OneFragment oneFragment) {
            activityReference = new WeakReference<>(oneFragment);

        }

        @Override
        protected void onPreExecute() {
            // добавление пустой строки с информацией о загрузке
            // и получение id этой строки
            id = DataWeatherHandler.addColumnCity();

            OneFragment oneFragment = activityReference.get();
            if (oneFragment == null) {
                return;
            }
            oneFragment.cursorReNew();
        }

        /**
         * @param params подается "черновой" город введеный пользователем
         * @return город от Гугла и статус попытки записи его в базу
         * для последующего выбора сообщения пользователю
         */
        @Override
        protected String[] doInBackground(String... params) {
            String draftCity = params[0];
            // возвращает массив с названием и статусом запроса
            String[] cityAndStat = DataWeatherHandler.loadCityOfGoogleAndPutInDB(draftCity);

            String lng = DataWeatherHandler.DBData.getLongitude(cityAndStat[0]);
            String lat = DataWeatherHandler.DBData.getLatitude(cityAndStat[0]);
            DataWeatherHandler.loadOfOpenWeatherAndUpdDB(id, lng, lat, false);

            return cityAndStat;
        }

        @Override
        protected void onPostExecute(String[] params) {
            OneFragment oneFragment = activityReference.get();
            if (oneFragment == null) {
                return;
            }
            oneFragment.cursorReNew();

            // вывод сообщений пользователю о результате добавления города
            if (Integer.parseInt(params[1]) == 0) {
                oneFragment.printMessage(params[0] + " " + UPD_ERROR_MSG);
            } else if (Integer.parseInt(params[1]) == 1) {
                oneFragment.printMessage(UPD_OK_MSG);
            }
        }
    }

}
