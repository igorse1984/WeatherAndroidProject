package ru.igorsharov.weatherapp.DataHandler;

import org.json.JSONObject;

import ru.igorsharov.weatherapp.JSON.Parser.JSONParserOfOpenWeather;


public class ParsingUtils {

    // передача объекта парсеру перед началом парсинга
    public static void setWeatherJSONParser(JSONObject jo) {
        JSONParserOfOpenWeather.setGlobParsingObj(jo);
    }

    public static String[] parseWeatherToday() {
        return new String[]{
                JSONParserOfOpenWeather.parseLocation(),
                JSONParserOfOpenWeather.parseTempToday(),
                JSONParserOfOpenWeather.parsePressureToday(),
                JSONParserOfOpenWeather.parseIconIdToday()};
    }

    public static String[] parseDateForecast() {
        return JSONParserOfOpenWeather.parseDateForecast();
    }

    public static String[] parseTempForecast() {
        return JSONParserOfOpenWeather.parseTempForecast();
    }

    public static String[] parsePressureForecast() {
        return JSONParserOfOpenWeather.parsePressureForecast();
    }

    public static String[] parseIconIdArr() {
        return JSONParserOfOpenWeather.parseIconIdForecast();
    }
}
