package ru.igorsharov.weatherapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OneFragment extends Fragment {

    private final static String TAG = OneFragment.class.getSimpleName();
    private CheckBox chBoxTemperature, chBoxPressure, chBoxForecast;
    private ListView listView;
    private EditText editTextCityAdd;
    private Button buttonAdd;
    private List<String> cityArr = new ArrayList<>();
    private ArrayAdapter<String> listViewAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        initView(view);
        setOnClickListeners();

        //Get a Cursor
        Cursor cursorForList = MyApp.getDb().getReadableCursor(DBEmpl.TableDep.T_NAME);

        //Create arrays of columns and UI elements
        String[] from = {DBEmpl.TableDep.C_NAME, DBEmpl.TableDep.C_LOCA};
        int[] to = {R.id.tvName, R.id.tvLocation};

        //Create simple Cursor adapter
        SimpleCursorAdapter lvAdapter = new SimpleCursorAdapter(this,
                R.layout.list_item, cursorForList, from, to, 1);

        //setting up adapter to list view
        lvList.setAdapter(lvAdapter);
        MyApp.getDb().addDep("Foo", "Bar");
        MyApp.getDb().deleteDep(1);

        return view;
    }

    private void initView(View v) {
        chBoxTemperature = v.findViewById(R.id.checkBoxTemperature);
        chBoxPressure = v.findViewById(R.id.checkBoxPressure);
        chBoxForecast = v.findViewById(R.id.checkBoxWeatherForecast);
        listView = v.findViewById(R.id.listView);
        editTextCityAdd = v.findViewById(R.id.editTextCityAdd);
        buttonAdd = v.findViewById(R.id.buttonAdd);

        listViewAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, cityArr);

        listView.setAdapter(listViewAdapter);
    }

    private void setOnClickListeners() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showWeatherInfo(String.valueOf(((TextView) view).getText()));
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = String.valueOf(editTextCityAdd.getText());
                cityArr.add(city);
                editTextCityAdd.setText("");
                listViewAdapter.notifyDataSetChanged();
                WeatherBox.getInstance().putCityAndGenerateWeather(city);
            }
        });
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
}
