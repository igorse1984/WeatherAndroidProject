package ru.igorsharov.weatherapp;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;

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
public final class DataWeatherHandler {
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
            return new String[]{
                    WeatherEntry.C_METEOSTATION,
                    WeatherEntry.C_TEMPERATURE_TODAY,
                    WeatherEntry.C_PRESSURE_TODAY,
                    WeatherEntry.C_ICON_WEATHER};
        }

        static String[] keysForecast() {
            return new String[]{
                    WeatherEntry.C_TEMPERATURE_FORECAST,
                    WeatherEntry.C_PRESSURE_FORECAST};
        }

        static String[] valuesToday() {
            return new String[]{
                    JSONParser.OfOpenWeather.getLocation(),
                    JSONParser.OfOpenWeather.getTemperature(),
                    JSONParser.OfOpenWeather.getPressure(),
                    JSONParser.OfOpenWeather.getIconId()};
        }

        static String[] valuesForecast() {
            return new String[]{
                    JSONParser.OfOpenWeather.getTempForecast(),
                    JSONParser.OfOpenWeather.getPressureForecast()};
        }

        static String[] googleKeys() {
            return new String[]{
                    WeatherEntry.C_CITY,
                    WeatherEntry.C_LONGITUDE,
                    WeatherEntry.C_LATITUDE};
        }
    }


    static long addColumnCity() {
        // добавление информационной строки о загрузке в базу для отображения в списке
        return id = dbWeather.put(WeatherEntry.C_CITY, TEXT_LOAD);
    }


    // обращение к методу только из OneFragment
    static String[] loadCityOfGoogleAndPutInDB(String city) {
        JSONParser.OfGoogleGeo.setJSONObject(JSONLoader.loadLocationOfGoogleGeo(city));
        String cityName = JSONParser.OfGoogleGeo.getCityName();
        String[] coordinates = JSONParser.OfGoogleGeo.getCityCoord();
        int updRes = 0;
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
    // работает в двух режимах: погода сейчас и прогноз
    static void loadOfOpenWeatherAndUpdDB(long id, String lng, String lat, boolean isForecast) {

        //запрашивается погода с сервера и передается главныем обьектом парсеру
        setJSONParser(JSONLoader.loadWeather(lng, lat, isForecast));

        // TODO доработать прогноз, добавить данные
        // запись всей погодной информации о городе в базу
        updateBD(id,
                !isForecast ? KeysAndValuesArr.keysToday() : KeysAndValuesArr.keysForecast(),
                !isForecast ? KeysAndValuesArr.valuesToday() : KeysAndValuesArr.valuesForecast());
    }

    // передача объекта перед началом парсинга
    private static void setJSONParser(JSONObject jo) {
        JSONParser.OfOpenWeather.setGlobParsingObj(jo);
    }

    // собирает и возвращает строку погоды для установки в TextView
    public static String weatherString(String value) {
        return value.concat("°");
    }

    // определяет и возвращает цвет для TextView в зависимости от значения температуры
    public static int colorOfTemp(Context c, String temperature) {
        return ContextCompat.getColor(c, DBData.getColorOfTemperature(temperature));
    }

    public static abstract class DBData {
        // добавление плюсового знака положительной температуре
        static String plus(String temperature) {
            if (Double.valueOf(temperature) > 0) {
                return "+";
            }
            return "";
        }

        static int getColorOfTemperature(String w) {
            Log.d(LOG_TAG, w);
            double weather = Double.valueOf(w);
            if (weather >= 0 && weather < 10) {
                return R.color.color0;
            } else {
                if (weather >= 10 && weather < 20) {
                    return R.color.color10;
                } else {
                    if (weather >= 20 && weather < 30) {
                        return R.color.color20;
                    } else {
                        if (weather >= 30) {
                            return R.color.color30;
                        } else {
                            return R.color.colorCold;
                        }
                    }
                }
            }
        }

        // подбор иконки погоды
        public static String getIconId(String idIconWeatherToday) {
            int icon;
            switch (idIconWeatherToday) {
                case "01d":
                    icon = R.mipmap.weather_01d;
                    break;
                case "01n":
                    icon = R.mipmap.weather_01n;
                    break;
                case "02d":
                    icon = R.mipmap.weather_02d;
                    break;
                case "02n":
                    icon = R.mipmap.weather_02n;
                    break;
                case "03d":
                    icon = R.mipmap.weather_03d;
                    break;
                case "03n":
                    icon = R.mipmap.weather_03n;
                    break;
                case "04d":
                    icon = R.mipmap.weather_04d;
                    break;
                case "04n":
                    icon = R.mipmap.weather_04n;
                    break;
                case "10d":
                    icon = R.mipmap.weather_10d;
                    break;
                case "10n":
                    icon = R.mipmap.weather_10n;
                    break;
                case "13d":
                    icon = R.mipmap.weather_13d;
                    break;
                case "13n":
                    icon = R.mipmap.weather_13n;
                    break;
                case "50d":
                    icon = R.mipmap.weather_50d;
                    break;
                case "50n":
                    icon = R.mipmap.weather_50d;
                    break;
                default:
                    icon = 0;
            }
            return String.valueOf(icon);
        }

        // TODO необходимо реализовать кэш, на случай отсутствия интернет соединения
        // возврат погодных параметров
        private static String getCommonParam(String city, String key) {
            return dbWeather.getWeatherOfParams(city, key);
        }

        static String getLocation(String city) {
            return getCommonParam(city, WeatherEntry.C_METEOSTATION);
        }

        static String getTemperature(String city) {
            return getCommonParam(city, WeatherEntry.C_TEMPERATURE_TODAY);
        }

        static String getPressure(String city) {
            return getCommonParam(city, WeatherEntry.C_PRESSURE_TODAY);
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