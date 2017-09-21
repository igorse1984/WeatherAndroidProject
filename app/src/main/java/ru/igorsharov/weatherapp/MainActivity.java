package ru.igorsharov.weatherapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements OneFragment.OneFragmentInterface, SecondFragment.SecondFragmentInterface {

    OneFragment oneFragment = new OneFragment();
    SecondFragment secondFragment = new SecondFragment();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction().add(R.id.fragment_container, oneFragment).commit();
    }

    @Override
    public void onListViewSelected(Bundle bundle) {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, secondFragment).commit();
        secondFragment.setWeather(bundle);
    }

    @Override
    public void clickButtonBackOnSecondFragment() {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, oneFragment).commit();
    }
}

