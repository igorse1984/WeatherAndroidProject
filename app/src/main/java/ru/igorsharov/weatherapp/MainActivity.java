package ru.igorsharov.weatherapp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements OneFragment.OneFragmentInterface {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction().add(R.id.fragment_container, new OneFragment()).commit();

    }

    @Override
    public void onListViewSelected(int pos) {
// подключаем FragmentManager

        // Получаем ссылку на второй фрагмент по ID
        SecondFragment secondFragment = new SecondFragment();
        secondFragment.setWeather(pos);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, secondFragment).commit();
    }
}

