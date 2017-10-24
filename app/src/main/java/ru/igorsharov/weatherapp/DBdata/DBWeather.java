package ru.igorsharov.weatherapp.DBdata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
     * Добавляет информацию о погоде в базу данных
     */
    public long put(String... weatherValues) {

        // заполняем базу через объект вспомогательного класса
        ContentValues v = new ContentValues();

        for (int i = 0; i < weatherValues.length - 1; i += 2) {
            // Заполение значений
            v.put(weatherValues[i], weatherValues[i + 1]);

            Log.d("DB.put", weatherValues[i] + " = " + weatherValues[i + 1]);
        }

        // записываем значения в базу и возвращаем ID вставленной строки
        return getWritableDatabase().insert(WeatherEntry.T_NAME, null, v);
    }

    /**
     * Метод для добавления данных в базу через контент-провайдера
     *
     * @return id of new element
     */
    long put(ContentValues values) {
        return this.getWritableDatabase().insert(WeatherEntry.T_NAME, null, values);
    }

    /**
     * Получение погодных данных из базы данных
     *
     * @param city   название города
     * @param wParam погодный параметр
     */
    public String getWeather(String city, String wParam) {
        String tName = WeatherEntry.T_NAME;
        String cCity = WeatherEntry.C_CITY;
        String sqlRequest = "SELECT " + wParam
                + " FROM " + tName
                + " WHERE " + cCity
                + "=" + "'" + city + "'";
        Cursor cursor = AppDB.getDb().getRawReadableCursor(sqlRequest);
        if (cursor.moveToFirst()) {
            /* Calculate indexes of columns and getWeather*/
            String str = cursor.getString(
                    cursor.getColumnIndex(wParam));
            cursor.close();
            return str;
        }
        return null;
    }

    /**
     * Обновление информации о погоде в базе
     */
    public void update(long id, String... weatherValues) {

		/* Create a new map of values, where column names are the keys */
        ContentValues v = new ContentValues();

		/* Fill values */
        for (int i = 0; i < weatherValues.length - 1; i += 2) {
            // Заполение значений
            v.put(weatherValues[i], weatherValues[i + 1]);

            Log.d("DB.update", weatherValues[i] + " = " + weatherValues[i + 1]);
        }

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
