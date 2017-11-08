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

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import ru.igorsharov.weatherapp.DBdata.DBWeather;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract;
import ru.igorsharov.weatherapp.DBdata.TodaySimpleAdapter;

public class TodayFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, TextWatcher {

    private final static String TAG = TodayFragment.class.getSimpleName();
    public final static String T_NAME = "weatherToday";
    private final static String UPD_ERROR_MSG = "невозможно добавить обьект";
    private final static String UPD_OK_MSG = "Обьект добавлен";
    private final static long MINTIME = 3000L;
    private final static float MINDIST = 1.0F;
    static final String TEXT_LOAD = "Идет загрузка...";
    private CheckBox chBoxTemperature, chBoxPressure, chBoxForecast;
    private ListView listView;
    private EditText editTextCityAdd;
    private Button buttonAdd;
    private Button btnFindLoc;
    private TodaySimpleAdapter lvTodayAdapter;
    // TODO здесь только для обновления состояние listView может можно убрать в DataWeatherHandler?
    private DBWeather db = AppDB.getDb();
    private Cursor cursorForList;
    private LocationHelper lochlp;
    private Location loc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
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
        cursorForList = db.getReadableCursor(T_NAME);
        lvTodayAdapter.swapCursor(cursorForList);
    }

    private void setDbListAdapter() {
        //Create arrays of columns and UI elements
        String[] from = DBWeatherContract.DBKeys.fromForTodayAdapter;

        //Create simple Cursor adapter
        // null потому что адаптер знает про view элементы
        lvTodayAdapter = new TodaySimpleAdapter(getActivity(),
                R.layout.today_list_item, cursorForList, from, null, 1);

        listView.setAdapter(lvTodayAdapter);
    }

    void initLoc() {
        lochlp = new LocationHelper(getActivity());
        loc = lochlp.getLastLoc();
    }

    // TODO перенести чекбоксы во второй фрагмент
    // переход по второй фрагмент и передача настроек
    private void toNextFragment(String id) {
        Bundle b = new Bundle();

        b.putBoolean(ForecastFragment.TEMPERATURE_SHOW_KEY, chBoxTemperature.isChecked());
        b.putBoolean(ForecastFragment.PRESSURE_SHOW_KEY, chBoxPressure.isChecked());
        b.putBoolean(ForecastFragment.FORECAST_SHOW_KEY, chBoxForecast.isChecked());
        b.putString(ForecastFragment.ID_DB_CITY_KEY, id);


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
                String draftCity = String.valueOf(editTextCityAdd.getText());
                editTextCityAdd.setText("");
                // получение названия города с GoogleGeo, запускаем в новом потоке
                new DownloadTask(this).execute(draftCity);
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

        String lvCity = String.valueOf(((TextView) view.findViewById(R.id.tvCityName)).getText());
        // блокирование отправки серверу сообщения о загрузке вместо названия города
        if (!lvCity.equals(TEXT_LOAD)) {
            // переход во второй фрагмент с передачей id города в базе
            toNextFragment(String.valueOf(id));
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String sId = String.valueOf(id);
        // удаление записи о городе
        db.delete(T_NAME, sId);
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

    private void printMessage(String str) {
        DataWeatherHandler.printMessage(getActivity(), str);
    }


    /**
     * реализация фонового запроса названия города от GoogleGeo по введенным данным пользователя
     * описание к происходящему есть во втором фрагменте
     */
    private static class DownloadTask extends AsyncTask<String, Void, String[]> {

        private WeakReference<TodayFragment> activityReference;
        private String id;

        DownloadTask(TodayFragment todayFragment) {
            activityReference = new WeakReference<>(todayFragment);
        }

        @Override
        protected void onPreExecute() {
            TodayFragment todayFragment = activityReference.get();
            if (todayFragment == null) {
                return;
            }
            // добавление пустой строки с информацией о загрузке
            // и получение id этой строки
            id = String.valueOf(
                    DataWeatherHandler.DbUtils.putWeatherDbLine(
                            T_NAME,
                            new String[]{DBWeatherContract.DBKeys.C_CITY},
                            new String[]{TEXT_LOAD}));
            todayFragment.cursorReNew();
        }

        /**
         * @param params подается "черновой" город введеный пользователем
         * @return город от Гугла и статус попытки записи его в базу
         * для последующего выбора сообщения пользователю
         */
        @Override
        protected String[] doInBackground(String... params) {
            String draftCity = params[0];
            // возвращает массив с названием города от Гугл и статусом запроса
            String[] cityAndStat = DataWeatherHandler.NetUtils.loadCityOfGoogleAndPutInDB(T_NAME, draftCity, id);
            String lng = DataWeatherHandler.DbUtils.getLongitude(id);
            String lat = DataWeatherHandler.DbUtils.getLatitude(id);
            JSONObject jo = DataWeatherHandler.NetUtils.loadWeather(lng, lat, false);
            DataWeatherHandler.ParsingUtils.setWeatherJSONParser(jo);
            DataWeatherHandler.DbUtils.updWeatherDbLine(
                    T_NAME,
                    id,
                    DBWeatherContract.DBKeys.keysTodayArr,
                    DataWeatherHandler.ParsingUtils.parseWeatherToday());

            return cityAndStat;
        }

        @Override
        protected void onPostExecute(String[] params) {
            TodayFragment todayFragment = activityReference.get();
            if (todayFragment == null) {
                return;
            }
            todayFragment.cursorReNew();

            // вывод сообщений пользователю о результате добавления города
            // 0 - название города
            // 1 - результат добавления
            if (Integer.parseInt(params[1]) == 0) {
                todayFragment.printMessage(params[0] + " " + UPD_ERROR_MSG);
            } else if (Integer.parseInt(params[1]) == 1) {
                todayFragment.printMessage(UPD_OK_MSG);
            }
        }
    }

}
