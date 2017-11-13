package ru.igorsharov.weatherapp.JSON.Loader;


import org.json.JSONObject;

public class JSONLoaderOfGoogleGeo extends JSONLoader{

    private final static String GOOGLE_GEO_API_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
    private final static String GOOGLE_GEO_API_KEY = "AIzaSyBn8KpTUk8DJk9TAkwJ7IfXsoZSONtBBCA";
    private final static String GOOGLE_KEYS[] = {"key", "language", "address"};

    // получение названия города от GoogleGeo
    public static JSONObject loadLocation(String city) {
        return loadJSONObj(
                buildUrl(GOOGLE_GEO_API_URL, GOOGLE_KEYS, GOOGLE_GEO_API_KEY, RU, city));
    }
}
