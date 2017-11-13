package ru.igorsharov.weatherapp.DataHandler;

import org.json.JSONObject;

import ru.igorsharov.weatherapp.DBdata.DBWeatherContract;
import ru.igorsharov.weatherapp.JSON.Loader.JSONLoaderOfGoogleGeo;
import ru.igorsharov.weatherapp.JSON.Loader.JSONLoaderOfOpenWeather;
import ru.igorsharov.weatherapp.JSON.Parser.JSONParserOfGoogleGeo;


public class NetUtils extends DataWeatherHandler{

    // обращение к методу только из TodayFragment
    public static String[] loadCityOfGoogleAndPutInDB(String tableName, String city, String id) {
        JSONParserOfGoogleGeo.setJSONObject(JSONLoaderOfGoogleGeo.loadLocation(city));
        String cityName = JSONParserOfGoogleGeo.getCityName();
        String[] coordinates = JSONParserOfGoogleGeo.getCityCoord();
        int updRes = 0;
        if (cityName != null) {
            // запись в базу
            updRes = DbUtils.updateLineDb(
                    tableName,
                    id,
                    DBWeatherContract.DBKeys.googleKeys,
                    new String[]{cityName, coordinates[0], coordinates[1]});

            // удаляем пустой item в случае дубрирования городов
            if (updRes == 0) {
                dbWeather.delete(
                        tableName,
                        id);
            }
        }
        return new String[]{cityName, String.valueOf(updRes)};
    }

    // загрузка погоды и парсинг результата в БД
    // работает в двух режимах: погода сейчас и прогноз
    public static JSONObject loadWeather(String lng, String lat, boolean isForecast) {
        //запрашивается погода с сервера и передается главныем обьектом парсеру
        return JSONLoaderOfOpenWeather.loadWeather(lng, lat, isForecast);
    }
}
