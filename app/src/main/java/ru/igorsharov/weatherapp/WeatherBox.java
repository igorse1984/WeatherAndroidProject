package ru.igorsharov.weatherapp;


import java.util.HashMap;

import static ru.igorsharov.weatherapp.R.string.pressure;
import static ru.igorsharov.weatherapp.R.string.temperature;

class WeatherBox {
    private static final WeatherBox ourInstance = new WeatherBox();

    private final String TEMPERATURE = "temperature";
    private final String PRESSURE = "pressure";
    private final String TEMPERATURE_FORECAST = "temperatureForecast";
    private final String PRESSURE_FORECAST = "pressureForecast";
    private HashMap<String, HashMap> weatherBoxOfCity = new HashMap<>();

    static WeatherBox getInstance() {
        return ourInstance;
    }

    private WeatherBox() {
    }


    void putCityAndGenerateWeather(String city) {
        HashMap<String, Integer> weatherBox = new HashMap<>(4);
        weatherBox.put(TEMPERATURE, (int) ((((Math.random() / 2)) - 0.2) * 100));
        weatherBox.put(PRESSURE, (int) ((((Math.random() / 5) + 0.6) * 1000)));
        weatherBox.put(TEMPERATURE_FORECAST, (int) ((((Math.random() / 2)) - 0.2) * 100));
        weatherBox.put(PRESSURE_FORECAST, (int) (((Math.random() / 5) + 0.6) * 1000));
        weatherBoxOfCity.put(city, weatherBox);
    }

    int getTemperature(String city) {
            return (int) weatherBoxOfCity.get(city).get(TEMPERATURE);
    }

    int getPressure(String city) {
            return (int) weatherBoxOfCity.get(city).get(PRESSURE);
    }

    int getTemperatureForecast(String city) {
            return (int) weatherBoxOfCity.get(city).get(TEMPERATURE_FORECAST);
    }

    int getPressureForecast(String city) {
            return (int) weatherBoxOfCity.get(city).get(PRESSURE_FORECAST);
    }
}