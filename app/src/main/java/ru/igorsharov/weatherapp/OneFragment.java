package ru.igorsharov.weatherapp;

import android.app.Fragment;
import android.os.Bundle;
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
import android.widget.TextView;

import java.util.HashMap;

public class OneFragment extends Fragment {

    private final static String TAG = OneFragment.class.getSimpleName();
    private LinearLayout primaryLayout, weatherLayout;
    private Button buttonCheck, buttonBack;
    private CheckBox chBoxTemperature, chBoxPressure, chBoxForecast;
    private HashMap<String, Boolean> hm = new HashMap<>();
    ListView listView;
    boolean changeLayout;


    interface OneFragmentInterface {
        void onListViewSelected(Bundle bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        initView(view);
        initListView();
        Weather.getInstance().initWeather(listView.getCount());
        return view;
    }

    private void initView(View v) {
        chBoxTemperature = v.findViewById(R.id.checkBoxTemperature);
        chBoxPressure = v.findViewById(R.id.checkBoxPressure);
        chBoxForecast = v.findViewById(R.id.checkBoxWeatherForecast);
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

    private void showWeatherInfo(View view, int pos) {
        Bundle b = new Bundle();

        hm.put(SecondFragment.CH_TEMPERATURE, chBoxTemperature.isChecked());
        hm.put(SecondFragment.CH_PRESSURE, chBoxPressure.isChecked());
        hm.put(SecondFragment.CH_FORECAST, chBoxForecast.isChecked());

        b.putSerializable(SecondFragment.CHECKBOX_STATUSES, hm);
        b.putString(SecondFragment.CITY, String.valueOf(((TextView) view).getText()));
        Log.d(TAG, "getString = " + b.getString(SecondFragment.CITY));
        b.putInt(SecondFragment.LV_POSITION, pos);

        OneFragmentInterface oneFragmentInterface = (OneFragmentInterface) getActivity();
        oneFragmentInterface.onListViewSelected(b);
    }
}
