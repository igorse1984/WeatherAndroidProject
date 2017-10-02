package ru.igorsharov.weatherapp.DBdata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ru.igorsharov.weatherapp.AppDB;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract.WeatherEntry;

public class DBWeather extends DBHelper {

    /**
     * Constructor with one parameter that describe a link
     * to the Context object.
     *
     * @param context The context object.
     */
    public DBWeather(Context context) {
        super(context, WeatherEntry.DB_NAME, null, WeatherEntry.DB_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is
     * where the creation of tables and the initial population of the
     * tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

		/* Create tables */
        execSQL(db, WeatherEntry.SQL_CREATE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything
     * else it needs to upgrade to the new schema version.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		/* Drop tables */
        dropTable(db, WeatherEntry.T_NAME);

		/* Invoke onCreate method */
        this.onCreate(db);

    }

    /**
     * Add information about department to a data base
     *
     * @param city                city name
     * @param temperature         temperature of city
     * @param pressure            pressure of city
     * @param temperatureForecast temperature forecast of city
     * @param pressureForecast    pressure forecast of city
     */
    public void put(String city, String temperature, String pressure, String temperatureForecast, String pressureForecast) {

		/* Create a new map of values, where column names are the keys */
        ContentValues v = new ContentValues();

		/* Fill values */
        v.put(WeatherEntry.C_CITY, city);
        v.put(WeatherEntry.C_TEMPERATURE, temperature);
        v.put(WeatherEntry.C_PRESSURE, pressure);
        v.put(WeatherEntry.C_TEMPERATURE_FORECAST, temperatureForecast);
        v.put(WeatherEntry.C_PRESSURE_FORECAST, pressureForecast);

		/* Add item to a data base */
        getWritableDatabase().insert(WeatherEntry.T_NAME, null, v);
    }

    public String get(String city, String wParam) {
        String tName = WeatherEntry.T_NAME;
        String cCity = WeatherEntry.C_CITY;
        String sqlRequest = "SELECT " + wParam
                + " FROM " + tName
                + " WHERE " + cCity
                + "=" + "'" + city + "'";
        Cursor cursor = AppDB.getDb().getRawReadableCursor(sqlRequest);
        if (cursor.moveToFirst()) {
            /* Calculate indexes of columns and get*/
            String str = cursor.getString(
                    cursor.getColumnIndex(wParam));
            cursor.close();
            return str;
        }
        return null;
    }

    /**
     * Update information about weather into a data base.
     *
     * @param city                city name
     * @param temperature         temperature of city
     * @param pressure            pressure of city
     * @param temperatureForecast temperature forecast of city
     * @param pressureForecast    pressure forecast of city
     * @param id                  of element that will be updated
     */
    public void update(String city, String temperature, String pressure, String temperatureForecast, String pressureForecast, long id) {

		/* Create a new map of values, where column names are the keys */
        ContentValues v = new ContentValues();

		/* Fill values */
        v.put(WeatherEntry.C_CITY, city);
        v.put(WeatherEntry.C_TEMPERATURE, temperature);
        v.put(WeatherEntry.C_PRESSURE, pressure);
        v.put(WeatherEntry.C_TEMPERATURE_FORECAST, temperatureForecast);
        v.put(WeatherEntry.C_PRESSURE_FORECAST, pressureForecast);

		/* Update information */
        getWritableDatabase().update(DBWeatherContract.WeatherEntry.T_NAME, v,
                WeatherEntry.SQL_WHERE_BY_ID, new String[]{String.valueOf(id)});
    }

    /**
     * Delete weather info from a data base.
     *
     * @param id of element that will be deleted
     */
    public void delete(long id) {
        getWritableDatabase().delete(
                WeatherEntry.T_NAME, WeatherEntry.SQL_WHERE_BY_ID,
                new String[]{String.valueOf(id)});
    }

    public Cursor getRawReadableCursor(String sqlRequest) {
        return getReadableDatabase().rawQuery(sqlRequest, null);
    }

}
