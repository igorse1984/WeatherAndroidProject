package ru.igorsharov.weatherapp;

import android.app.Application;
import android.util.Log;

import ru.igorsharov.weatherapp.data.DBWeather;

public class AppDB extends Application {

    /* Static file for store a link to the data base object */
    private static DBWeather mDBWeather;

    /* Private field for store a LOG tag */
    public static final String LOG_TAG = "AppDB";

    @Override
    public void onCreate() {

		/* Invoke a parent method */
        super.onCreate();

		/* Create a data base object */
        mDBWeather = new DBWeather(getApplicationContext());

        /* Write a log */
        Log.d(LOG_TAG, "AppDB onCreate");
    }

    /**
     * Get a link to the data base object
     */
    public static DBWeather getDb() {
        return mDBWeather;
    }
}
