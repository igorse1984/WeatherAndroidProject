package ru.igorsharov.weatherapp;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
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

public class OneFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private final static String TAG = OneFragment.class.getSimpleName();
    private CheckBox chBoxTemperature, chBoxPressure, chBoxForecast;
    private ListView listView;
    private EditText editTextCityAdd;
    private Button buttonAdd;
    private SimpleCursorAdapter lvAdapter;
    private DBWeather db = AppDB.getDb();
    Cursor cursorForList = db.getReadableCursor(WeatherEntry.T_NAME);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        initView(view);
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);
        buttonAdd.setOnClickListener(this);
        return view;
    }

    /* обновление listView по новой технологии взамен устаревшего метода requery */
    private void cursorReNew() {
        cursorForList = db.getReadableCursor(WeatherEntry.T_NAME);
        lvAdapter.swapCursor(cursorForList);
    }

    private void initView(View v) {
        chBoxTemperature = v.findViewById(R.id.checkBoxTemperature);
        chBoxPressure = v.findViewById(R.id.checkBoxPressure);
        chBoxForecast = v.findViewById(R.id.checkBoxWeatherForecast);
        listView = v.findViewById(R.id.listView);
        editTextCityAdd = v.findViewById(R.id.editTextCityAdd);
        buttonAdd = v.findViewById(R.id.buttonAdd);


        //Create arrays of columns and UI elements
        String[] from = {WeatherEntry.C_CITY};
        int[] to = {R.id.tvName};

        //Create simple Cursor adapter
        lvAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.list_item, cursorForList, from, to, 1);

        listView.setAdapter(lvAdapter);
    }

    private void showWeatherInfo(String city) {
        Bundle b = new Bundle();

        b.putBoolean(SecondFragment.SHOW_TEMPERATURE, chBoxTemperature.isChecked());
        b.putBoolean(SecondFragment.SHOW_PRESSURE, chBoxPressure.isChecked());
        b.putBoolean(SecondFragment.SHOW_FORECAST, chBoxForecast.isChecked());

        b.putString(SecondFragment.CITY, city);


        // обратиться к активити можно либо через создание интерфейса фрагмента,
        // либо через каст с получением ссылки на активити
        ((MainActivity) getActivity()).onListViewSelected(b);
    }

    /* обработка кликов */
    @Override
    public void onClick(View v) {
        String city = String.valueOf(editTextCityAdd.getText());
        editTextCityAdd.setText("");
        WeatherBox.getInstance().addCity(city);
        cursorReNew();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        db.delete(id);
        cursorReNew();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showWeatherInfo(String.valueOf(((TextView) view).getText()));
    }
}
