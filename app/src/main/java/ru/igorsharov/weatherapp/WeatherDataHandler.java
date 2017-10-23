package ru.igorsharov.weatherapp;


import ru.igorsharov.weatherapp.DBdata.DBWeather;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract.WeatherEntry;
import ru.igorsharov.weatherapp.JSON.JSONLoader;
import ru.igorsharov.weatherapp.JSON.JSONParser;

/**
 * Делает запросы парсеру на получение необходимых данных и
 * кладет их в базу данных, выдает информацию параметов погоды
 */
final class WeatherDataHandler {
    private static final WeatherDataHandler ourInstance = new WeatherDataHandler();
    private static final String LOG_TAG = WeatherDataHandler.class.getSimpleName();


    static WeatherDataHandler getInstance() {
        return ourInstance;
    }

    private WeatherDataHandler() {
    }

    private DBWeather db = AppDB.getDb();


    // TODO оптимизировать addCity и updateCity

    void addCity(String city) {
        // запись всей погодной информации о городе в базу
        String cityName;
        if ((cityName = JSONLoader.getCityNameOfGoogleGeo(city)) != null) {
            db.put(WeatherEntry.C_CITY, cityName,
                    WeatherEntry.C_LOCATION, "",
                    WeatherEntry.C_TEMPERATURE, "",
                    WeatherEntry.C_PRESSURE, "",
                    WeatherEntry.C_TEMPERATURE_FORECAST, "",
                    WeatherEntry.C_PRESSURE_FORECAST, "");
        }
    }

    // обновление погоды в БД выбранного города
    void updateCity(long id, String city) {
        //запрашивается с сервера погода и устанавливается парсеру
        JSONParser.OfOpenWeather.setJSONObject(JSONLoader.getJSONWeather(city));

        // запись всей погодной информации о городе в базу
        String cityName;
        if ((cityName = JSONParser.OfOpenWeather.getCityName()) != null) {
            db.update(id, WeatherEntry.C_CITY, cityName,
                    WeatherEntry.C_LOCATION, JSONParser.OfOpenWeather.getLocation(),
                    WeatherEntry.C_TEMPERATURE, JSONParser.OfOpenWeather.getTemperature(),
                    WeatherEntry.C_PRESSURE, JSONParser.OfOpenWeather.getPressure(),
                    WeatherEntry.C_TEMPERATURE_FORECAST, "0",
                    WeatherEntry.C_PRESSURE_FORECAST, generatePressure());
        }
    }


    private String generatePressure() {
        return String.valueOf((int) (((Math.random() / 5) + 0.6) * 1000));
    }

    // TODO необходимо реализовать кэш

    // возврат погодных параметров
    private String getCommonParam(String city, String key) {
        return db.getWeather(city, key);
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
}