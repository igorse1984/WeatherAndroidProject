package ru.igorsharov.weatherapp;


import android.database.Cursor;

import ru.igorsharov.weatherapp.data.DBWeatherContract.WeatherEntry;

/**
 * ввод/вывод информации о погоде
 * входные параметры: название города
 * генерирует погоду,
 * записывает и читает данные из БД
 */
class WeatherBox {
    private static final WeatherBox ourInstance = new WeatherBox();

    static WeatherBox getInstance() {
        return ourInstance;
    }

    private WeatherBox() {
    }

    private String temperature;
    private String pressure;
    private String temperatureForecast;
    private String pressureForecast;

    void addCity(String city) {
        generateWeather4City();
        AppDB.getDb().addDataWeather(city, temperature, pressure, temperatureForecast, pressureForecast);
    }

    private void generateWeather4City() {
        temperature = generateTemperature();
        pressure = generatePressure();
        temperatureForecast = generateTemperature();
        pressureForecast = generatePressure();
    }

    private String generateTemperature() {
        return String.valueOf((int) ((((Math.random() / 2)) - 0.2) * 100));
    }

    private String generatePressure() {
        return String.valueOf((int) (((Math.random() / 5) + 0.6) * 1000));
    }

    // необходимо реализовать кэш

    String getTemperature(String city) {
        return getWeatherParam(city, WeatherEntry.C_TEMPERATURE);
    }

    String getPressure(String city) {
        return getWeatherParam(city, WeatherEntry.C_PRESSURE);
    }

    String getTemperatureForecast(String city) {
        return getWeatherParam(city, WeatherEntry.C_TEMPERATURE_FORECAST);
    }

    String getPressureForecast(String city) {
        return getWeatherParam(city, WeatherEntry.C_PRESSURE_FORECAST);
    }

    private String getWeatherParam(String city, String wParam) {
        String tName = WeatherEntry.T_NAME;
        String cCity = WeatherEntry.C_CITY;
        String sqlRequest = "SELECT " + wParam
                + " FROM " + tName
                + " WHERE " + cCity
                + "=" + "'" + city + "'";
        Cursor cursor = AppDB.getDb().getReadableCursor(sqlRequest);
        if (cursor.moveToFirst()) {
            /* Calculate indexes of columns and get*/
            String str = cursor.getString(
                    cursor.getColumnIndex(wParam));
            cursor.close();
            return str;
        }
        return null;
    }
}