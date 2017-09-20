package ru.igorsharov.weatherapp;


public class Weather {
    private static final Weather ourInstance = new Weather();

    private int[] temperature, pressure, temperatureForecast, pressureForecast;
    private int capacity;
    private boolean init = false;

    static Weather getInstance() {
        return ourInstance;
    }

    private Weather() {
    }

    void initWeather(int capacity) {
        if (!init) {
            this.capacity = capacity;
            temperature = new int[capacity];
            pressure = new int[capacity];
            temperatureForecast = new int[capacity];
            pressureForecast = new int[capacity];
            for (int i = 0; i < capacity; i++) {
                temperature[i] = (int) ((((Math.random() / 2)) - 0.2) * 100);
                pressure[i] = (int) ((((Math.random() / 5) + 0.6) * 1000));
                temperatureForecast[i] = (int) ((((Math.random() / 2)) - 0.2) * 100);
                pressureForecast[i] = (int) ((((Math.random() / 5) + 0.6) * 1000));
            }

            init = true;
        }
    }

    int getTemperature(int id) {
        if (id <= capacity) {
            return temperature[id];
        }
        return 0;
    }


    int getPressure(int id) {
        if (id <= capacity) {
            return pressure[id];
        }
        return 0;
    }

    int getTemperatureForecast(int id) {
        if (id <= capacity) {
            return temperatureForecast[id];
        }
        return 0;
    }

    int getPressureForecast(int id) {
        if (id <= capacity) {
            return pressureForecast[id];
        }
        return 0;
    }


}