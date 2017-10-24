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
    public static final String TEXT_LOAD = "Идет загрузка...";


    static WeatherDataHandler getInstance() {
        return ourInstance;
    }

    private WeatherDataHandler() {
    }

    private DBWeather db = AppDB.getDb();
    private long id;

    void addColumnCity() {
        // добавление информационной строки о загрузке в базу для отображения в списке
        id = db.put(WeatherEntry.C_CITY, TEXT_LOAD);
    }

    void updateCityName(String city) {
        // запись названия города в базу
        String cityName = JSONLoader.getCityNameOfGoogleGeo(city);
        if (cityName != null) {
            db.update(id, WeatherEntry.C_CITY, cityName);
        }
    }

    // обновление погоды в БД выбранного города
    void updateCityWeather(long id, String city) {

        //запрашивается погода с сервера и передается парсеру
        JSONParser.OfOpenWeather.setJSONObject(JSONLoader.getJSONWeather(city));

        // запись всей погодной информации о городе в базу
        String cityName = JSONParser.OfOpenWeather.getCityName();

        db.update(id, WeatherEntry.C_CITY, cityName,
                WeatherEntry.C_LOCATION, JSONParser.OfOpenWeather.getLocation(),
                WeatherEntry.C_TEMPERATURE, JSONParser.OfOpenWeather.getTemperature(),
                WeatherEntry.C_PRESSURE, JSONParser.OfOpenWeather.getPressure(),
                WeatherEntry.C_TEMPERATURE_FORECAST, "0",
                WeatherEntry.C_PRESSURE_FORECAST, generatePressure());
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