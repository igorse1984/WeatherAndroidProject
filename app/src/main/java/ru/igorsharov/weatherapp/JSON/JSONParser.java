package ru.igorsharov.weatherapp.JSON;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * TODO убрать ХАРДКОД и определиться где будут храниться ключи парсера
 */

public class JSONParser {

    private JSONParser() {
    }

    public static class OfOpenWeather {
        private static JSONObject jsonObject;
        private final static double hPa2mmHG = 0.750063755419211f;
        private final static String JSON_OBJ_MAIN = "main";
        private final static String JSON_ARR_WEATHER = "weather";
        private final static String JSON_ARR_LIST = "list";
        private final static String NAME = "name";
        private final static String TEMPERATURE = "temp";
        private final static String PRESSURE = "pressure";
        private final static String ICON = "icon";
        private final static String FORMAT_1 = "%.1f";

        // TODO добавить проверку результата запроса
        // если в ответе содержится код = 200, возвращаем JSONObject
//            if (jsonObjects[0].getInt(RESPONSE) == OK) {
//        return jsonObjects[0];
//    }

        // установка классу объекта для последующего парсинга
        public static void setGlobParsingObj(JSONObject jsonObject) {
            OfOpenWeather.jsonObject = jsonObject;
        }

        public static String getLocation() {
            try {
                return jsonObject.getString(NAME);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private static String getFormatDoubleOfJSONObj(String nameJSONObj, String paramJSON, String format) {
            try {
                return String.format(
                        Locale.US, format, jsonObject
                                .getJSONObject(nameJSONObj)
                                .getDouble(paramJSON));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private static String getFormatDoubleOfJSONArr(String nameJSONArr, int numberJSONObj, String nameJSONObj, String gettingParam, String format) {
            try {
                return String.format(
                        Locale.US, format, jsonObject
                                .getJSONArray(nameJSONArr)
                                .getJSONObject(numberJSONObj)
                                .getJSONObject(nameJSONObj)
                                .getDouble(gettingParam));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private static String getStringOfJSONArr(String nameJSONArr, int numberJSONObj, String gettingParam) {
            try {
                return jsonObject
                        .getJSONArray(nameJSONArr)
                        .getJSONObject(numberJSONObj)
                        .getString(gettingParam);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        /**
         * Парсинг погодных параметров на сегодня
         */


        public static String getTemperature() {
            return getFormatDoubleOfJSONObj(JSON_OBJ_MAIN, TEMPERATURE, FORMAT_1);
        }

        public static String getPressure() {
            return getFormatDoubleOfJSONObj(JSON_OBJ_MAIN, PRESSURE, FORMAT_1);
        }

        public static String getIconId() {
            return getStringOfJSONArr(JSON_ARR_WEATHER, 0, ICON);
        }

        /**
         * Парсинг прогноза погодных параметров
         */


        // TODO добавить чтение всего массива погодных параметров из прогноза
        // TODO сделать универсальный метод парсинга
        public static String getTempForecast() {
            // пробегая по массиву получаем прогноз на каждые 3 часа
            return getFormatDoubleOfJSONArr(JSON_ARR_LIST, 0, JSON_OBJ_MAIN, TEMPERATURE, FORMAT_1);
        }

        public static String getPressureForecast() {
            // пробегая по массиву получаем прогноз на каждые 3 часа
            return getFormatDoubleOfJSONArr(JSON_ARR_LIST, 0, JSON_OBJ_MAIN, PRESSURE, FORMAT_1);
        }


    }


    // TODO также оптимизировать
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
