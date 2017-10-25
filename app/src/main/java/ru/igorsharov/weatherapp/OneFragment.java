package ru.igorsharov.weatherapp;

import android.app.Fragment;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
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

import ru.igorsharov.weatherapp.DBdata.DBWeather;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract.WeatherEntry;

public class OneFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, TextWatcher {

    private final static String TAG = OneFragment.class.getSimpleName();
    private final static long MINTIME = 3000L;
    private final static float MINDIST = 1.0F;
    private CheckBox chBoxTemperature, chBoxPressure, chBoxForecast;
    private ListView listView;
    private EditText editTextCityAdd;
    private Button buttonAdd;
    private Button btnFindLoc;
    private SimpleCursorAdapter lvAdapter;
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
        String[] from = {WeatherEntry.C_CITY};
        int[] to = {R.id.tvListItem};

        //Create simple Cursor adapter
        lvAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.list_item, cursorForList, from, to, 1);

        listView.setAdapter(lvAdapter);
    }

    void initLoc() {
        lochlp = new LocationHelper(getActivity());
        loc = lochlp.getLastLoc();
    }

    // TODO перенести чекбоксы во второй фрагмент?

    // переход по второй фрагмент и передача настроек
    private void showWeatherInfo(long id, String city) {
        Bundle b = new Bundle();

        b.putBoolean(SecondFragment.TEMPERATURE_SHOW_KEY, chBoxTemperature.isChecked());
        b.putBoolean(SecondFragment.PRESSURE_SHOW_KEY, chBoxPressure.isChecked());
        b.putBoolean(SecondFragment.FORECAST_SHOW_KEY, chBoxForecast.isChecked());
        b.putString(SecondFragment.CITY_KEY, city);
        b.putLong(SecondFragment.ID_DB_KEY, id);


        // обратиться к активити можно либо через создание интерфейса фрагмента,
        // либо через каст с получением ссылки на активити
        ((MainActivity) getActivity()).onListViewSelected(b);
    }

    /**
     * ОБРАБОТКА КЛИКОВ
     */

    // TODO скрывать клавиатуру после нажатия кновки добавления
    // TODO запретить дублировать города в списке

    //Клики по кнопкам
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAdd:
                String city = String.valueOf(editTextCityAdd.getText());
                editTextCityAdd.setText("");
                // экземпляр AsyncTask, получает название города
                DownloadTask downloadTask = new DownloadTask();
                //Запускаем задачу
                downloadTask.execute(city);

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
        String city = String.valueOf(((TextView) view).getText());
        // блокирование отправки серверу сообщения о загрузке вместо названия города
        if (!city.equals(WeatherDataHandler.TEXT_LOAD)) {
            // переход во второй фрагмент
            showWeatherInfo(id, city);
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

    // реализация фонового запроса названия города от GoogleGeo по введенным данным пользователя
    private class DownloadTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            WeatherDataHandler.getInstance().addColumnCity();
            cursorReNew();
        }

        @Override
        protected Void doInBackground(String... params) {
            WeatherDataHandler.getInstance().getCityName(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cursorReNew();
        }
    }
}
