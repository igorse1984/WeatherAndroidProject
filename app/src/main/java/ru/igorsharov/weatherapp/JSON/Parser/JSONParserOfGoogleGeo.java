package ru.igorsharov.weatherapp.JSON.Parser;


import org.json.JSONException;
import org.json.JSONObject;

public class JSONParserOfGoogleGeo extends JSONParser {

    public static void setJSONObject(JSONObject jsonObj) {
        globJSONObject = jsonObj;
    }

    // Парсинг координат
    public static String[] getCityCoord() {
        String[] coord = null;
        JSONObject jo = getObjOfObjX3(
                "results",
                "geometry",
                "location");

        try {
            if (jo != null) {
                coord = new String[]{jo.getString("lng"), jo.getString("lat")};
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return coord;
    }

    public static String getCityName() {
        return getStringOfArrNumObjX2(
                "results",
                0,
                "address_components",
                "long_name");
    }
}
