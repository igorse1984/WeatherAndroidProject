package ru.igorsharov.weatherapp.JSON;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;

/**
 * TODO убрать ХАРДКОД и определиться где будут храниться ключи парсера
 */

public class JSONParser {

    private JSONParser() {
    }

    public static class OfOpenWeather {
        // TODO что будет если сделать final?
        private static JSONObject globJSONObject;
        private final static double hPa2mmHG = 0.750063755419211f;

        private final static String J_OBJ_MAIN = "main";
        private final static String J_ARR_WEATHER = "weather";
        private final static String J_ARR_LIST = "list";
        private final static String J_DATE = "dt_txt";
        private final static String J_NAME = "name";
        private final static String J_CNT = "cnt";
        private final static String J_TEMPERATURE = "temp";
        private final static String J_PRESSURE = "pressure";
        private final static String J_ICON = "icon";
        private final static String FORMAT_1 = "%.1f";

        // TODO добавить проверку результата запроса
        // если в ответе содержится код = 200, возвращаем JSONObject
//            if (jsonObjects[0].getInt(RESPONSE) == OK) {
//        return jsonObjects[0];
//    }

        // установка классу объекта для последующего парсинга
        public static void setGlobParsingObj(JSONObject jsonObject) {
            OfOpenWeather.globJSONObject = jsonObject;
        }



        private static String formatDouble2String(double value) {
            return String.format(
                    Locale.US, FORMAT_1, value);
        }

        private static double getDoubleOfObj(String nameJSONObj, String paramJSON) {
            try {
                return globJSONObject
                        .getJSONObject(nameJSONObj)
                        .getDouble(paramJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 0;
        }

        private static double getDoubleOfArrNumObjNameObj(String nameJSONArr, int numberJSONObj, String nameJSONObj, String gettingParam) {
            try {
                return globJSONObject
                        .getJSONArray(nameJSONArr)
                        .getJSONObject(numberJSONObj)
                        .getJSONObject(nameJSONObj)
                        .getDouble(gettingParam);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 0;
        }

        private static String getStringOfArrNumObjArrNumObj(String nameJSONArr, int numberJSONObj, String nameJSONArr1, String gettingParam) {
            try {
                return globJSONObject
                        .getJSONArray(nameJSONArr)
                        .getJSONObject(numberJSONObj)
                        .getJSONArray(nameJSONArr1)
                        .getJSONObject(0)
                        .getString(gettingParam);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private static String getStringOfArrNumObj(String nameJSONArr, int numberJSONObj, String gettingParam) {
            try {
                return globJSONObject
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
         *
         */

        public static String parseLocation() {
            try {
                return globJSONObject.getString(J_NAME);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String parseTempToday() {
            return formatDouble2String(getDoubleOfObj(J_OBJ_MAIN, J_TEMPERATURE));
        }

        public static String parsePressureToday() {
            return formatDouble2String(getDoubleOfObj(J_OBJ_MAIN, J_PRESSURE));
        }

        public static String parseIconIdToday() {
            return getStringOfArrNumObj(J_ARR_WEATHER, 0, J_ICON);
        }

        /**
         * Парсинг прогноза погодных параметров
         */
        private static int getCnt() {
            int i = 0;
            try {
                i = globJSONObject.getInt(J_CNT);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return i;
        }

        // TODO добавить чтение всего массива погодных параметров из прогноза
        // TODO сделать универсальный метод парсинга
        private static String[] parseForecast(String key, int parsingMode) {
            // получаем длину массива прогноза
            int cnt = getCnt();
            String[] arr = new String[cnt];

            // Парсинг режимы:
            // 0 - дата
            // 1 - температура и давление
            // 2 - иконки
            // пробегая по массиву получаем прогноз на каждые 3 часа
            if (parsingMode == 0) {
                for (int i = 0; i < cnt; i++) {
                    arr[i] = getStringOfArrNumObj(J_ARR_LIST, i, J_DATE);
                }
            } else {
                if (parsingMode == 1) {
                    for (int i = 0; i < cnt; i++) {
                        arr[i] = formatDouble2String(getDoubleOfArrNumObjNameObj(J_ARR_LIST, i, J_OBJ_MAIN, key));
                    }
                } else if (parsingMode == 2) {
                    for (int i = 0; i < cnt; i++) {
                        arr[i] = getStringOfArrNumObjArrNumObj(J_ARR_LIST, i, J_ARR_WEATHER, J_ICON);
                    }
                    Log.d("@icon", Arrays.toString(arr));
                }
            }
            return arr;
        }

        public static String[] parseDateForecast() {
            return parseForecast(J_DATE, 0);
        }

        public static String[] parseTempForecast() {
            return parseForecast(J_TEMPERATURE, 1);
        }

        public static String[] parsePressureForecast() {
            return parseForecast(J_PRESSURE, 1);
        }

        public static String[] parseIconIdForecast() {
            return parseForecast(J_ICON, 2);
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
