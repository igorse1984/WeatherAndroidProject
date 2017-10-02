package ru.igorsharov.weatherapp;


import ru.igorsharov.weatherapp.DBdata.DBWeather;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract.WeatherEntry;

/**
 * генерирует погоду
 * выводит информацию о погоде из БД по запросу названия города
 */
class WeatherBox {
    private static final WeatherBox ourInstance = new WeatherBox();

    static WeatherBox getInstance() {
        return ourInstance;
    }

    private WeatherBox() {
    }

    private DBWeather db = AppDB.getDb();

    void addCity(String city) {
        db.put(city, generateTemperature(), generatePressure(),
                generateTemperature(), generatePressure());
    }

    private String generateTemperature() {
        return String.valueOf((int) ((((Math.random() / 2)) - 0.2) * 100));
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