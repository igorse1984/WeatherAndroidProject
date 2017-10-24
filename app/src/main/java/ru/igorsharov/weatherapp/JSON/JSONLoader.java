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
    private static final JSONObject[] jsonObj = {null};


    // получение названия города от GoogleGeo
    public static String getCityNameOfGoogleGeo(final String city) {
        // запрос в GoogleGeo
        jsonObj[0] = loadJSONObj(
                buildURL(
                        GOOGLE_GEO_API_URL, GOOGLE_KEYS, GOOGLE_GEO_API_KEY, RU, city));

        return JSONParser.OfGoogleGeo.getCityName(jsonObj[0]);
    }

    // получение погоды от погодного сервиса
    public static JSONObject getJSONWeather(final String city) {

        // сборка ссылки для запроса в OpenWeather
        String urlOfOpenWeather = buildURL(
                OPENWEATHER_API_BASE_URL, OPEN_WEATHER_KEYS, OPENWEATHER_API_KEY, RU, city, UNITS);

        try {
            // получение погоды от OpenWeather и ДОБАВЛЕНИЕ города от GoogleGeo
            jsonObj[0] = loadJSONObj(urlOfOpenWeather).put(CITY_NAME_OF_GOOGLE, city);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // возвращаем готовый JSON от OpenWeather
        return jsonObj[0];
    }

    // собираем URL запрос
    private static String buildURL(String baseUrl, String[] keys, String... param) {

        Uri.Builder uri = Uri.parse(baseUrl)
                .buildUpon();


        for (int i = 0; i < param.length; i++) {
            uri.appendQueryParameter(keys[i], param[i]);
        }

        String url = uri.build().toString();

        return url;
    }

    // вспомогательный метод, через вспомогательный метод получает JSON String, преобразуя в обьект
    private static JSONObject loadJSONObj(final String url) {
        try {
            return new JSONObject(request(url));
//        } catch (JSONException | NullPointerException e) {
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    // вспомогательный метод, запращивает и получает у сервера данные JSON
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

        return okResult;
    }
}
