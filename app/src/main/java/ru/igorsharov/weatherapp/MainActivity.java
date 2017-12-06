package ru.igorsharov.weatherapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


public class MainActivity extends AppCompatActivity implements ForecastFragment.SecondFragmentInterface {

    TodayFragment todayFragment = new TodayFragment();
    ForecastFragment forecastFragment = new ForecastFragment();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // подключаем Toolbar к Активити вместо ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Floating button - часть материального дизайна и будет рассмотрена позже в уроке 6.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Находим drawer (выезжающую панель) в ресурсах.
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // Вешаем слушателя на drawer.
        drawer.addDrawerListener(toggle);

        // Cинхронизирует hamburger menu с drawer.
        toggle.syncState();


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

}

