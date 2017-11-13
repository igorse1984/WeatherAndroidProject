package ru.igorsharov.weatherapp.JSON.Parser;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class JSONParser {

    static JSONObject globJSONObject;
    private final static String FORMAT_DOUBLE = "%.1f";

    // установка классу объекта для последующего парсинга
    public static void setGlobParsingObj(JSONObject jsonObject) {
        globJSONObject = jsonObject;
    }

    static String format2String(double value) {
        return String.format(Locale.US,
                FORMAT_DOUBLE, value);
    }

    static JSONObject getObjOfObjX3(String nameJSONArr, String nameJSONObj1, String nameJSONObj2) {
        try {
            return globJSONObject.getJSONArray(nameJSONArr)
                    .getJSONObject(0)
                    .getJSONObject(nameJSONObj1)
                    .getJSONObject(nameJSONObj2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    static double getDoubleOfObj(String nameJSONObj, String paramJSON) {
        try {
            return globJSONObject
                    .getJSONObject(nameJSONObj)
                    .getDouble(paramJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    static double getDoubleOfArrNumObjNameObj(String nameJSONArr, int numJSONObj, String nameJSONObj, String gettingParam) {
        try {
            return globJSONObject
                    .getJSONArray(nameJSONArr)
                    .getJSONObject(numJSONObj)
                    .getJSONObject(nameJSONObj)
                    .getDouble(gettingParam);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    static String getStringOfArrNumObjX2(String nameJSONArr, int numJSONObj, String nameJSONArr1, String gettingParam) {
        try {
            return globJSONObject
                    .getJSONArray(nameJSONArr)
                    .getJSONObject(numJSONObj)
                    .getJSONArray(nameJSONArr1)
                    .getJSONObject(0)
                    .getString(gettingParam);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String getStringOfArrNumObj(String nameJSONArr, int numJSONObj, String gettingParam) {
        try {
            return globJSONObject
                    .getJSONArray(nameJSONArr)
                    .getJSONObject(numJSONObj)
                    .getString(gettingParam);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    static long getLongOfArrNumObj(String nameJSONArr, int numJSONObj, String gettingParam) {
        try {
            return globJSONObject
                    .getJSONArray(nameJSONArr)
                    .getJSONObject(numJSONObj)
                    .getLong(gettingParam);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
