package ru.igorsharov.weatherapp.JSON.Loader;


import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class JSONLoader {

    private static final String LOG_TAG = JSONLoaderOfOpenWeather.class.getSimpleName();
    final static String RU = "ru";

    // сборка URL запроса, подходит для GoogleGeo и OpenWeather
    static String buildUrl(String baseUrl, String[] keys, String... param) {
        Uri.Builder uri = Uri.parse(baseUrl)
                .buildUpon();
        for (int i = 0; i < param.length; i++) {
            uri.appendQueryParameter(keys[i], param[i]);
        }
        return uri.build().toString();
    }

    // получает JSON String, преобразуя в JSON-обьект
    static JSONObject loadJSONObj(String url) {
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
