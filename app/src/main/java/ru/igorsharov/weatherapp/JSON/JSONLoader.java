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

    // основной метод класса
    public static JSONObject getJSONWeather(final String city) {
        final JSONObject[] jsonObj = {null};

        Thread reqThread = new Thread() {
            public void run() {

                // запрос в GoogleGeo
                jsonObj[0] = loadJSONObj(
                        buildURL(
                                GOOGLE_GEO_API_URL, GOOGLE_KEYS, GOOGLE_GEO_API_KEY, RU, city));

                // получение названия города
                String cityOFGoogleApi = JSONParser.OfGoogleGeo.getCityName(jsonObj[0]);

                Log.d(LOG_TAG, cityOFGoogleApi);

                // сборка ссылки для запроса в OpenWeather
                String urlOfOpenWeather = buildURL(
                        OPENWEATHER_API_BASE_URL, OPEN_WEATHER_KEYS, OPENWEATHER_API_KEY, RU, cityOFGoogleApi, UNITS);

                try {
                    // получение погоды от OpenWeather и ДОБАВЛЕНИЕ города от GoogleGeo
                    jsonObj[0] = loadJSONObj(urlOfOpenWeather).put(CITY_NAME_OF_GOOGLE, cityOFGoogleApi);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        reqThread.start();
        try {
            reqThread.join();
        } catch (InterruptedException e) {
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

        Log.d("###", url);
        return url;
    }

    // вспомогательный метод, через вспомогательный метод получает JSON String, преобразуя в обьект
    private static JSONObject loadJSONObj(final String url) {
        try {
            return new JSONObject(request(url));
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
            Log.d(LOG_TAG, okResult);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return okResult;
    }
}
