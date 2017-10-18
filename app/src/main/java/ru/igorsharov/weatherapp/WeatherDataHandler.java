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

    private void commonAddOrUpdate(String reqCity) {
        //устанавливает парсеру JSON обьект
        JSONParser.OfOpenWeather.setJSONObject(JSONLoader.getJSONWeather(reqCity));
    }

    // TODO оптимизировать оба метода

    void addCity(String reqCity) {
        commonAddOrUpdate(reqCity);

        // запись всей погодной информации о городе в базу
        db.put(WeatherEntry.C_CITY, JSONParser.OfOpenWeather.getCityName(),
                WeatherEntry.C_LOCATION,"",
                WeatherEntry.C_TEMPERATURE,"",
                WeatherEntry.C_PRESSURE,"",
                WeatherEntry.C_TEMPERATURE_FORECAST, "",
                WeatherEntry.C_PRESSURE_FORECAST,"");
    }

    void updateCity(long id, String reqCity) {
        commonAddOrUpdate(reqCity);

        // запись всей погодной информации о городе в базу
        db.update(id, WeatherEntry.C_CITY, JSONParser.OfOpenWeather.getCityName(),
                WeatherEntry.C_LOCATION, JSONParser.OfOpenWeather.getLocation(),
                WeatherEntry.C_TEMPERATURE, JSONParser.OfOpenWeather.getTemperature(),
                WeatherEntry.C_PRESSURE, JSONParser.OfOpenWeather.getPressure(),
                WeatherEntry.C_TEMPERATURE_FORECAST, "",
                WeatherEntry.C_PRESSURE_FORECAST, generatePressure());
    }


    private String generatePressure() {
        return String.valueOf((int) (((Math.random() / 5) + 0.6) * 1000));
    }

    // необходимо реализовать кэш

    // возврат погодных параметров
    private String getCommonParam(String city, String key) {
        return db.get(city, key);
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