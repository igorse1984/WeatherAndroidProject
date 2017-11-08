package ru.igorsharov.weatherapp;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements ForecastFragment.SecondFragmentInterface {

    TodayFragment todayFragment = new TodayFragment();
    ForecastFragment forecastFragment = new ForecastFragment();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager().beginTransaction().add(R.id.fragment_container, todayFragment).commit();
    }

    public void onListViewSelected(Bundle bundle) {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, forecastFragment).commit();
        forecastFragment.setWeather(bundle);
    }

    @Override
    public void clickButtonBackOnSecondFragment() {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, todayFragment).commit();
    }

    public Fragment getTodayFragment(){
        return todayFragment;
    }
}

