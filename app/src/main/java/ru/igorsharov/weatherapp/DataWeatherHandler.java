package ru.igorsharov.weatherapp;


import org.json.JSONObject;

import ru.igorsharov.weatherapp.DBdata.DBWeather;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract.WeatherEntry;
import ru.igorsharov.weatherapp.JSON.JSONLoader;
import ru.igorsharov.weatherapp.JSON.JSONParser;

/**
 * Делает запросы парсеру на получение необходимых данных и
 * кладет их в базу данных, работает с запросами погодных параметров
 */

// TODO что будет если добавлять одновременно много городов?
final class DataWeatherHandler {
    private static final String LOG_TAG = DataWeatherHandler.class.getSimpleName();
    static final String TEXT_LOAD = "Идет загрузка...";

    private DataWeatherHandler() {
    }

    // TODO разобраться в вопросе статики для БД
    private static DBWeather dbWeather = AppDB.getDb();
    // id последней загруженной строки
    private static long id;

    private static abstract class KeysAndValuesArr {
        static String[] keysToday() {
            return new String[]{WeatherEntry.C_LOCATION, WeatherEntry.C_TEMPERATURE, WeatherEntry.C_PRESSURE};
        }

        static String[] keysForecast() {
            return new String[]{WeatherEntry.C_TEMPERATURE_FORECAST, WeatherEntry.C_PRESSURE_FORECAST};
        }

        static String[] valuesToday() {
            return new String[]{JSONParser.OfOpenWeather.getLocation(),
                    JSONParser.OfOpenWeather.getTemperature(), JSONParser.OfOpenWeather.getPressure()};
        }

        static String[] valuesForecast() {
            return new String[]{JSONParser.OfOpenWeather.getTempForecast(), JSONParser.OfOpenWeather.getPressureForecast()};
        }

        static String[] googleKeys() {
            return new String[]{WeatherEntry.C_CITY, WeatherEntry.C_LONGITUDE, WeatherEntry.C_LATITUDE};
        }
    }


    static void addColumnCity() {
        // добавление информационной строки о загрузке в базу для отображения в списке
        id = dbWeather.put(WeatherEntry.C_CITY, TEXT_LOAD);
    }

    // обращение к методу только из OneFragment
    static String[] requestOfGoogleGeo(String city) {
        JSONParser.OfGoogleGeo.setJSONObject(JSONLoader.loadLocationOfGoogleGeo(city));
        String cityName = JSONParser.OfGoogleGeo.getCityName();
        String[] coordinates = JSONParser.OfGoogleGeo.getCityCoord();
        int updRes = 1;
        if (cityName != null) {
            // запись в базу
            updRes = updateBD(id,
                    KeysAndValuesArr.googleKeys(),
                    new String[]{cityName, coordinates[0], coordinates[1]});

            // удаляем пустой item в случае дубрирования городов
            if (updRes == 0) {
                dbWeather.delete(id);
            }
        }
        return new String[]{cityName, String.valueOf(updRes)};
    }

    private static int updateBD(long id, String[] keys, String[] values) {
        return dbWeather.update(id, keys, values);
    }

    // загрузка погоды в БД выбранного города
    static void loadWeather(long id, String lng, String lat, boolean isForecast) {
        //запрашивается погода с сервера и передается парсеру
        setJSONParser(JSONLoader.loadWeather(lng, lat, isForecast));

        // TODO доработать прогноз
        // запись всей погодной информации о городе в базу
        updateBD(id,
                !isForecast ? KeysAndValuesArr.keysToday() : KeysAndValuesArr.keysForecast(),
                !isForecast ? KeysAndValuesArr.valuesToday() : KeysAndValuesArr.valuesForecast());
    }

    // передача объекта перед началом парсинга
    private static void setJSONParser(JSONObject jo) {
        JSONParser.OfOpenWeather.setJSONObject(jo);
    }

    static abstract class WeatherParam {
        // TODO необходимо реализовать кэш, на случай отсутствия интернет соединения
        // возврат погодных параметров
        private static String getCommonParam(String city, String key) {
            return dbWeather.getWeatherOfParams(city, key);
        }

        static String getLocation(String city) {
            return getCommonParam(city, WeatherEntry.C_LOCATION);
        }

        static String getTemperature(String city) {
            return getCommonParam(city, WeatherEntry.C_TEMPERATURE);
        }

        static String getPressure(String city) {
            return getCommonParam(city, WeatherEntry.C_PRESSURE);
        }

        static String getTemperatureForecast(String city) {
            return getCommonParam(city, WeatherEntry.C_TEMPERATURE_FORECAST);
        }

        static String getPressureForecast(String city) {
            return getCommonParam(city, WeatherEntry.C_PRESSURE_FORECAST);
        }

        static String getLongitude(String city) {
            return getCommonParam(city, WeatherEntry.C_LONGITUDE);
        }

        static String getLatitude(String city) {
            return getCommonParam(city, WeatherEntry.C_LATITUDE);
        }
    }
}