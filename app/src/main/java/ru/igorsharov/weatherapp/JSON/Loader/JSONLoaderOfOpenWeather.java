package ru.igorsharov.weatherapp.JSON.Loader;


import org.json.JSONObject;

public final class JSONLoaderOfOpenWeather extends JSONLoader {


    private final static String API_URL = "https://api.openweathermap.org/data/2.5/";
    private final static String IS_TODAY = "weather?";
    private final static String IS_FORECAST = "forecast?";
    private final static String API_KEY = "f7e04c998fc34bed211349ba89c5d7e7";
    private final static String REQUEST_KEYS[] = {"appid", "lang", "lon", "lat", "units"};
    private final static String UNITS = "metric";

    private JSONLoaderOfOpenWeather() {
    }

    private static final String LOG_TAG = JSONLoaderOfOpenWeather.class.getSimpleName();
    private static String lng;
    private static String lat;

    // получить погоду
    private static JSONObject loadWeather(String flag) {
        // сборка ссылки для запроса в OpenWeather
        String url = buildUrl(API_URL + flag,
                REQUEST_KEYS, API_KEY, RU, lng, lat, UNITS);
        // получение погоды от OpenWeather
        return loadJSONObj(url);
    }

    public static JSONObject loadWeather(String lng, String lat, boolean isForecast) {
        JSONLoaderOfOpenWeather.lng = lng;
        JSONLoaderOfOpenWeather.lat = lat;
        return !isForecast ? loadWeather(IS_TODAY) : loadWeather(IS_FORECAST);
    }
}
