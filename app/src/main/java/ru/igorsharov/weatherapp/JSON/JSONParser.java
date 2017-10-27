package ru.igorsharov.weatherapp.JSON;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * TODO убрать ХАРДКОД и определиться где будут храниться ключи парсера
 */

public final class JSONParser {


    private JSONParser() {
    }


    public static class OfOpenWeather {
        private static JSONObject jsonObject;
        final static double hPa2mmHG = 0.750063755419211f;

        // TODO добавить проверку результата запроса
        // если в ответе содержится код = 200, возвращаем JSONObject
//            if (jsonObjects[0].getInt(RESPONSE) == OK) {
//        return jsonObjects[0];
//    }
        public static void setJSONObject(JSONObject jsonObject) {
            OfOpenWeather.jsonObject = jsonObject;
        }

        // TODO как вариант использовать для универсального метода всех запросов парсеру
        private static String getCommon(String key) {
            try {
                return jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String getLocation() {
            return getCommon("name");
        }

        public static String getTemperature() {
            try {
                return String.format(
                        Locale.US, "%.1f", jsonObject
                                .getJSONObject("main")
                                .getDouble("temp"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String getPressure() {
            try {
                return String.format(
                        Locale.US, "%.2f", jsonObject
                                .getJSONObject("main")
                                .getDouble("pressure") * hPa2mmHG);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        // TODO добавить чтение всего массива погодных параметров из прогноза
        // TODO сделать универсальный метод парсинга
        public static String getTempForecast() {
            try {
                return String.format(
                        Locale.US, "%.1f",
                        jsonObject.getJSONArray("list")
                                // пробегая по массиву получаем прогноз на каждые 3 часа
                                .getJSONObject(0)
                                .getJSONObject("main")
                                .getDouble("temp"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String getPressureForecast() {
            try {
                return String.format(
                        Locale.US, "%.1f",
                        jsonObject.getJSONArray("list")
                                // пробегая по массиву получаем прогноз на каждые 3 часа
                                .getJSONObject(0)
                                .getJSONObject("main")
                                .getDouble("pressure") * hPa2mmHG);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class OfGoogleGeo {
        private static JSONObject jsonObj;

        public static void setJSONObject(JSONObject jsonObj) {
            OfGoogleGeo.jsonObj = jsonObj;
        }

        // Парсинг координат
        public static String[] getCityCoord() {
            String[] coord = new String[2];
            try {
                JSONObject jo = jsonObj.getJSONArray("results")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location");

                coord = new String[]{jo.getString("lng"), jo.getString("lat")};
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return coord;
        }

        public static String getCityName() {
            try {
                return jsonObj.getJSONArray("results")
                        .getJSONObject(0)
                        .getJSONArray("address_components")
                        .getJSONObject(0)
                        .getString("long_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
