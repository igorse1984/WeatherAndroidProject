package ru.igorsharov.weatherapp;


import android.util.Log;

import org.json.JSONObject;

import ru.igorsharov.weatherapp.DBdata.DBWeather;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract.WeatherEntry;
import ru.igorsharov.weatherapp.JSON.JSONLoader;
import ru.igorsharov.weatherapp.JSON.JSONParser;

/**
 * Делает запросы парсеру на получение необходимых данных и
 * кладет их в базу данных, работает с запросами погодных параметров
 */

// TODO что будет если добавить одновременно много городов?

final class DataWeatherHandler {
    private static final DataWeatherHandler ourInstance = new DataWeatherHandler();
    private static final String LOG_TAG = DataWeatherHandler.class.getSimpleName();
    static final String TEXT_LOAD = "Идет загрузка...";


    static DataWeatherHandler getInstance() {
        return ourInstance;
    }

    private DataWeatherHandler() {
    }

    private DBWeather db = AppDB.getDb();
    // id последней загруженной строки
    private long id;

    void addColumnCity() {
        // добавление информационной строки о загрузке в базу для отображения в списке
        id = db.put(WeatherEntry.C_CITY, TEXT_LOAD);
    }

    // обращение к методу из OneFragment
    void requestOfGoogleGeo(String city) {
        JSONParser.OfGoogleGeo.setJSONObject(JSONLoader.loadLocationOfGoogleGeo(city));
        String cityName = JSONParser.OfGoogleGeo.getCityName();
        String[] coordinates = JSONParser.OfGoogleGeo.getCityCoord();
        Log.d("COORD", "lon" + coordinates[0]);
        Log.d("COORD", "lat" + coordinates[1]);
        if (cityName != null) {
            // запись в базу
            updateBD(id,
                    new String[]{WeatherEntry.C_CITY, WeatherEntry.C_LONGITUDE, WeatherEntry.C_LATITUDE},
                    new String[]{cityName, coordinates[0], coordinates[1]});
        }
    }

    private void updateBD(long id, String[] keys, String[] values) {
        db.update(id, keys, values);
    }

    // загрузка погоды в БД выбранного города
    void loadWeather(long id, String lng, String lat, boolean isForecast) {
        //запрашивается погода с сервера и передается парсеру
        String[] keys;
        String[] values;

        setJSONParser(JSONLoader.loadWeather(lng, lat, isForecast));

        if (!isForecast) {
            keys = new String[]{WeatherEntry.C_LOCATION, WeatherEntry.C_TEMPERATURE, WeatherEntry.C_PRESSURE};
            values = new String[]{JSONParser.OfOpenWeather.getLocation(),
                    JSONParser.OfOpenWeather.getTemperature(), JSONParser.OfOpenWeather.getPressure()};
        } else {
            // TODO доработать прогноз
            keys = new String[]{WeatherEntry.C_TEMPERATURE_FORECAST, WeatherEntry.C_PRESSURE_FORECAST};
            values = new String[]{JSONParser.OfOpenWeather.getTempForecast(), JSONParser.OfOpenWeather.getPressureForecast()};
        }

        // запись всей погодной информации о городе в базу
        updateBD(id, keys, values);
    }

    private void setJSONParser(JSONObject jo) {
        JSONParser.OfOpenWeather.setJSONObject(jo);
    }


    // TODO необходимо реализовать кэш, на случай отсутствия интернет соединения
    // возврат погодных параметров
    private String getCommonParam(String city, String key) {
        return db.getWeatherOfParams(city, key);
    }

    String getLocation(String city) {
        return getCommonParam(city, WeatherEntry.C_LOCATION);
    }

    String getTemperature(String city) {
        return getCommonParam(city, WeatherEntry.C_TEMPERATURE);
    }

    String getPressure(String city) {
        return getCommonParam(city, WeatherEntry.C_PRESSURE);
    }

    String getTemperatureForecast(String city) {
        return getCommonParam(city, WeatherEntry.C_TEMPERATURE_FORECAST);
    }

    String getPressureForecast(String city) {
        return getCommonParam(city, WeatherEntry.C_PRESSURE_FORECAST);
    }

    String getLongitude(String city) {
        return getCommonParam(city, WeatherEntry.C_LONGITUDE);
    }

    String getLatitude(String city) {
        return getCommonParam(city, WeatherEntry.C_LATITUDE);
    }
}