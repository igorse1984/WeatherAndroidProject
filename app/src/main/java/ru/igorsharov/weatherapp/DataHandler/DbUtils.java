package ru.igorsharov.weatherapp.DataHandler;


import java.util.ArrayList;

import ru.igorsharov.weatherapp.DBdata.DBWeatherContract;
import ru.igorsharov.weatherapp.TodayFragment;

public class DbUtils extends DataWeatherHandler {


    // TODO необходимо реализовать кэш, на случай отсутствия интернет соединения

    public static long putWeatherDbLine(String tableName, String[] keys, String[] valuesOfKeys) {
        // возвращает id последней загруженной строки
        return dbWeather.put(tableName, keys, valuesOfKeys);
    }

    static int updateLineDb(String tableName, String id, String[] keys, String[] values) {
        return dbWeather.update(tableName, id, keys, values);
    }

    static void addWeatherArrInDb(String tableName, String[] keys, ArrayList valuesOfKey) {
        dbWeather.insertArrays(tableName, keys, valuesOfKey);
    }

    public static void updWeatherDbLine(String tableName, String id, String[] keys, String[] valuesOfKeys) {
        // TODO доработать прогноз, добавить данные
        // запись всей погодной информации о городе в базу
        updateLineDb(tableName, id, keys, valuesOfKeys);
    }

    public static void putForecastTableName(String id, String tableName) {
        dbWeather.update(
                TodayFragment.T_NAME,
                id,
                new String[]{DBWeatherContract.DBKeys.C_FORECAST_TABLE_NAME},
                new String[]{tableName});
    }

    public static void delTable(String id, String tableName) {
        if (tableName != null) {
            dbWeather.deleteTable(
                    dbWeather.getWritableDatabase(),
                    tableName);
        }
    }

    // возврат погодных параметров
    private static String getTodayValueFromDb(String id, String key) {
        return dbWeather.getFromDb(TodayFragment.T_NAME, id, DBWeatherContract.DBKeys._ID, key);
    }

    private static String getForecastValueFromDb(String nameForecastTable, String id, String key) {
        return dbWeather.getFromDb(nameForecastTable, id, DBWeatherContract.DBKeys._ID, key);
    }

    public static String getCityFromDb(String id) {
        return dbWeather.getFromDb(TodayFragment.T_NAME, id, DBWeatherContract.DBKeys._ID, DBWeatherContract.DBKeys.C_CITY);
    }

    public static String getLocation(String request) {
        return getTodayValueFromDb(request, DBWeatherContract.DBKeys.C_METEOLOCATION);
    }

    public static String getTemperature(String request) {
        return getTodayValueFromDb(request, DBWeatherContract.DBKeys.C_TEMPERATURE);
    }

    public static String getPressure(String request) {
        return getTodayValueFromDb(request, DBWeatherContract.DBKeys.C_PRESSURE);
    }

    static String getTemperatureForecast(String request) {
        return getTodayValueFromDb(request, DBWeatherContract.DBKeys.C_TEMPERATURE);
    }

    static String getPressureForecast(String request) {
        return getTodayValueFromDb(request, DBWeatherContract.DBKeys.C_PRESSURE);
    }

    public static String getLongitude(String request) {
        return getTodayValueFromDb(request, DBWeatherContract.DBKeys.C_LONGITUDE);
    }

    public static String getLatitude(String request) {
        return getTodayValueFromDb(request, DBWeatherContract.DBKeys.C_LATITUDE);
    }

    static String getNameForecastTable(String request) {
        return getTodayValueFromDb(request, DBWeatherContract.DBKeys.C_FORECAST_TABLE_NAME);
    }
}
