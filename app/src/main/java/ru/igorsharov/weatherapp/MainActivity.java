package ru.igorsharov.weatherapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class MainActivity extends AppCompatActivity implements
        ForecastFragment.SecondFragmentInterface,
        NavigationView.OnNavigationItemSelectedListener {

    TodayFragment todayFragment = new TodayFragment();
    ForecastFragment forecastFragment = new ForecastFragment();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // подключаем Toolbar к Активити вместо ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Floating button - часть материального дизайна и будет рассмотрена позже в уроке 6
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // находим drawer (выезжающую панель) в ресурсах
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // создание кнопки гамбургера для меню
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // Вешаем слушателя на drawer
        drawer.addDrawerListener(toggle);

        // Cинхронизирует hamburger menu с drawer
        // без данной строки кнопка гамбургер не появится
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Обработка нажатий на пункты меню
        int id = item.getItemId();
        int optionId = R.layout.feedback;
        ViewGroup parent = findViewById(R.id.content);

        if (id == R.id.to_main) {
//            optionId = R.layout.activity_main2;
        } else if (id == R.id.about_developer) {
            optionId = R.layout.about_developer;
        } else if (id == R.id.feedback) {
            optionId = R.layout.feedback;
        }

        parent.removeAllViews();
        View newContent = getLayoutInflater().inflate(optionId, parent, false);
        parent.addView(newContent);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // закрываем NavigationView
        // параметр определяет анимацию закрытия
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

