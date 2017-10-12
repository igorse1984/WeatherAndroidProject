package ru.igorsharov.weatherapp;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ru.igorsharov.weatherapp.DBdata.DBWeather;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract.WeatherEntry;
import ru.igorsharov.weatherapp.JSON.WeatherJSONLoader;
import ru.igorsharov.weatherapp.JSON.WeatherJSONParser;

/**
 * генерирует погоду
 * выводит информацию о погоде из БД по запросу названия города
 */
final class WeatherBox {
    private static final WeatherBox ourInstance = new WeatherBox();
    private static final String LOG_TAG = WeatherBox.class.getSimpleName();


    static WeatherBox getInstance() {
        return ourInstance;
    }

    private WeatherBox() {
    }

    private DBWeather db = AppDB.getDb();

    void addCity(String reqCity) {
        WeatherJSONParser.setJSONObject(requestWeatherJSONObject(reqCity));

        db.put(WeatherJSONParser.getCityName(),
                WeatherJSONParser.getTemperature(),generatePressure(),
                "0", generatePressure());
    }

    // запрашивает у сервера JSON с информацией о погоде для запрашиваемого города
    private JSONObject requestWeatherJSONObject(final String reqCity) {
        final JSONObject[] jsonObject = new JSONObject[1];
        Thread thread = new Thread() {
            public void run() {
                try {
                    jsonObject[0] = WeatherJSONLoader.getJSONObj(reqCity);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //        return String.valueOf((int) ((((Math.random() / 2)) - 0.2) * 100));
        return jsonObject[0];
    }

    private String generatePressure() {
        return String.valueOf((int) (((Math.random() / 5) + 0.6) * 1000));
    }

    // необходимо реализовать кэш

    String getTemperature(String city) {
        return db.get(city, WeatherEntry.C_TEMPERATURE);
    }

    String getPressure(String city) {
        return db.get(city, WeatherEntry.C_PRESSURE);
    }

    String getTemperatureForecast(String city) {
        return db.get(city, WeatherEntry.C_TEMPERATURE_FORECAST);
    }

    String getPressureForecast(String city) {
        return db.get(city, WeatherEntry.C_PRESSURE_FORECAST);
    }
}