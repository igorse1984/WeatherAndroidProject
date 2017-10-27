package ru.igorsharov.weatherapp.JSON;


import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class JSONLoader implements JSONContract {

    private JSONLoader() {
    }

    private static final String LOG_TAG = JSONLoader.class.getSimpleName();
    private static String lng;
    private static String lat;

    // получение названия города от GoogleGeo
    public static JSONObject loadLocationOfGoogleGeo(String city) {
        return loadJSONObj(
                urlBuild(GOOGLE_GEO_API_URL, GOOGLE_KEYS, GOOGLE_GEO_API_KEY, RU, city));
    }

    // общий метод получения погоды от погодного сервиса
    private static JSONObject loadWeatherOfOpenWeather(String flag) {
        // сборка ссылки для запроса в OpenWeather
        String url = urlBuild(OPENWEATHER_API_URL + flag,
                OPEN_WEATHER_KEYS, OPENWEATHER_API_KEY, RU, lng, lat, UNITS);
        // получение погоды от OpenWeather
        return loadJSONObj(url);
    }

    public static JSONObject loadWeather(String lng, String lat, boolean isForecast) {
        JSONLoader.lng = lng;
        JSONLoader.lat = lat;
        return !isForecast ? loadTodayWeather() : loadForecastWeather();
    }

    private static JSONObject loadTodayWeather() {
        return loadWeatherOfOpenWeather(OPENWEATHER_TODAY);
    }

    private static JSONObject loadForecastWeather() {
        return loadWeatherOfOpenWeather(OPENWEATHER_FORECAST);
    }

    // сборка URL запроса, подходит для GoogleGeo и OpenWeather
    private static String urlBuild(String baseUrl, String[] keys, String... param) {
        Uri.Builder uri = Uri.parse(baseUrl)
                .buildUpon();
        for (int i = 0; i < param.length; i++) {
            uri.appendQueryParameter(keys[i], param[i]);
        }
        return uri.build().toString();
    }

    // получает JSON String, преобразуя в JSON-обьект
    private static JSONObject loadJSONObj(String url) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(request(url));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    // запращивает и получает у сервера данные JSON
    private static String request(String url) {
        String okResult = null;

        OkHttpClient okClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response;
        try {
            response = okClient.newCall(request).execute();
            okResult = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(LOG_TAG, "URL " + url);
        Log.d(LOG_TAG, "okResult " + okResult);
        return okResult;
    }
}
