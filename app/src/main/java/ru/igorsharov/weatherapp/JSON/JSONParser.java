package ru.igorsharov.weatherapp.JSON;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * TODO убрать ХАРДКОД и определиться где будут храниться ключи данного парсера
 */


public final class JSONParser {


    private JSONParser() {
    }


    public static class OfOpenWeather {
        private static JSONObject jsonObject;

        // если в ответе содержится код = 200, возвращаем JSONObject
//            if (jsonObjects[0].getInt(RESPONSE) == OK) {
//        return jsonObjects[0];
//    }
        public static void setJSONObject(JSONObject jsonObject) {
            OfOpenWeather.jsonObject = jsonObject;
        }

        private static String getCommon(String key){
            try {
                return jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String getCityName() {
            return getCommon(JSONContract.CITY_NAME_OF_GOOGLE);
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
            double hPa2mmHG = 0.750063755419211f;
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
    }

    static class OfGoogleGeo {
        // JSON координаты
//            loadJSONObj(urlOfGoogleGeo)
//                    .getJSONArray("results")
//                    .getJSONObject(0)
//                    .getJSONObject("geometry")
//                    .getJSONObject("location");

        static String getCityName(JSONObject jsnObj) {
            try {
                return jsnObj.getJSONArray("results")
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
