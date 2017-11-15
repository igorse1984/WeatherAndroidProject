package ru.igorsharov.weatherapp.JSON.Parser;


import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * TODO убрать ХАРДКОД и определиться где будут храниться ключи парсера
 */

public class JSONParserOfOpenWeather extends JSONParser {

    private JSONParserOfOpenWeather() {
    }

    private final static double hPa2mmHG = 0.750063755419211f;

    private final static String J_OBJ_MAIN = "main";
    private final static String J_ARR_WEATHER = "weather";
    private final static String J_ARR_LIST = "list";
    private final static String J_DATE = "dt";
    private final static String J_NAME = "name";
    private final static String J_CNT = "cnt";
    private final static String J_TEMPERATURE = "temp";
    private final static String J_PRESSURE = "pressure";
    private final static String J_ICON = "icon";


    // TODO добавить проверку ответа
    // если в ответе содержится код = 200, возвращаем JSONObject
//            if (jsonObjects[0].getInt(RESPONSE) == OK) {
//        return jsonObjects[0];
//    }

    /**
     * Парсинг погодных параметров на сегодня
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
        return format2String1(getDoubleOfObj(J_OBJ_MAIN, J_TEMPERATURE));
    }

    public static String parsePressureToday() {
        return format2String1(getDoubleOfObj(J_OBJ_MAIN, J_PRESSURE) * hPa2mmHG);
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

    public static String[] parseDateForecast() {
        Locale locale = new Locale("ru", "RU");
        SimpleDateFormat df = new SimpleDateFormat("E, HH:mm dd MMMM", locale);
        String[] arr = new String[getCnt()];
        for (int i = 0; i < getCnt(); i++) {
            arr[i] = df.format(new Date(getLongOfArrNumObj(J_ARR_LIST, i, J_DATE) * 1000));
        }
        return arr;
    }

    public static String[] parseTempForecast() {
        String[] arr = new String[getCnt()];
        for (int i = 0; i < getCnt(); i++) {
            arr[i] = format2String1(getDoubleOfArrNumObjNameObj(J_ARR_LIST, i, J_OBJ_MAIN, J_TEMPERATURE));
        }
        return arr;
    }

    public static String[] parsePressureForecast() {
        String[] arr = new String[getCnt()];
        for (int i = 0; i < getCnt(); i++) {
            arr[i] = format2String0(getDoubleOfArrNumObjNameObj(J_ARR_LIST, i, J_OBJ_MAIN, J_PRESSURE) * hPa2mmHG);
        }
        return arr;
    }

    public static String[] parseIconIdForecast() {
        String[] arr = new String[getCnt()];
        for (int i = 0; i < getCnt(); i++) {
            arr[i] = getStringOfArrNumObjX2(J_ARR_LIST, i, J_ARR_WEATHER, J_ICON);
        }
        return arr;
    }
}

