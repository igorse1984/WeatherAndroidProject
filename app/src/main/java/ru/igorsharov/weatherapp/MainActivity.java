package ru.igorsharov.weatherapp;

import android.os.Bundle;
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
        getFragmentManager().beginTransaction().add(R.id.fragment_container, new SecondFragment()).commit();

    }
}

