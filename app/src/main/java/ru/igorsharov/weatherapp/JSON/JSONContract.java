package ru.igorsharov.weatherapp.JSON;

interface JSONContract {

    String GOOGLE_GEO_API_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
    String GOOGLE_GEO_API_KEY = "AIzaSyBn8KpTUk8DJk9TAkwJ7IfXsoZSONtBBCA";
    String GOOGLE_KEYS[] = {"key", "language", "address"};

    String OPENWEATHER_API_BASE_URL = "https://api.openweathermap.org/data/2.5/weather?";
    String OPENWEATHER_API_FORECAST_URL = "https://api.openweathermap.org/data/2.5/forecast?";
    String OPENWEATHER_API_KEY = "f7e04c998fc34bed211349ba89c5d7e7";
    String OPEN_WEATHER_KEYS[] = {"appid", "lang", "q", "units"};
    String UNITS = "metric";
    // ключ местоположения погодной точки
    String CITY_NAME_OF_GOOGLE = "cityNameOfGoogle";


    String RU = "ru";
    String RESPONSE = "cod";
    int OK = 200;

}
