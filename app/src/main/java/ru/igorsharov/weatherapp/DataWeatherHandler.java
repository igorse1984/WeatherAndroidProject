package ru.igorsharov.weatherapp;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import org.json.JSONObject;

import ru.igorsharov.weatherapp.DBdata.DBWeather;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract.DBKeys;
import ru.igorsharov.weatherapp.JSON.JSONLoader;
import ru.igorsharov.weatherapp.JSON.JSONParser;

/**
 * Делает запросы парсеру на получение необходимых данных и
 * кладет их в базу данных, работает с запросами погодных параметров
 */

// TODO что будет если добавлять одновременно много городов?
public final class DataWeatherHandler {

    // TODO разобраться в вопросе статики для БД
    private static DBWeather dbWeather = AppDB.getDb();


    private DataWeatherHandler() {
    }


    static class NetUtils {
        // обращение к методу только из TodayFragment
        static String[] loadCityOfGoogleAndPutInDB(String tableName, String city, String id) {
            JSONParser.OfGoogleGeo.setJSONObject(JSONLoader.loadLocationOfGoogleGeo(city));
            String cityName = JSONParser.OfGoogleGeo.getCityName();
            String[] coordinates = JSONParser.OfGoogleGeo.getCityCoord();
            int updRes = 0;
            if (cityName != null) {
                // запись в базу
                updRes = updateLineDb(
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
        static JSONObject loadWeather(String lng, String lat, boolean isForecast) {
            //запрашивается погода с сервера и передается главныем обьектом парсеру
            return JSONLoader.loadWeather(lng, lat, isForecast);
        }
    }


    static abstract class ParsingUtils {
        // передача объекта парсеру перед началом парсинга
        static void setWeatherJSONParser(JSONObject jo) {
            JSONParser.OfOpenWeather.setGlobParsingObj(jo);
        }

        static String[] parseWeatherToday() {
            return new String[]{
                    JSONParser.OfOpenWeather.parseLocation(),
                    JSONParser.OfOpenWeather.parseTempToday(),
                    JSONParser.OfOpenWeather.parsePressureToday(),
                    JSONParser.OfOpenWeather.parseIconIdToday()};
        }

        static String[] getWeatherForecast() {
            return new String[]{
//                    JSONParser.OfOpenWeather.parseTempForecast(),
//                    JSONParser.OfOpenWeather.parsePressureForecast()};
            };
        }

        static String[] parseDateForecast() {
            return JSONParser.OfOpenWeather.parseDateForecast();
        }

        static String[] parseTempForecast() {
            return JSONParser.OfOpenWeather.parseTempForecast();
        }

        static String[] parsePressureForecast() {
            return JSONParser.OfOpenWeather.parsePressureForecast();
        }

        static String[] parseIconIdArr() {
            return JSONParser.OfOpenWeather.parseIconIdForecast();
        }


    }


    private static int updateLineDb(String tableName, String id, String[] keys, String[] values) {
        return dbWeather.update(tableName, id, keys, values);
    }

    // собирает и возвращает строку погоды для установки в TextView
    public static String weatherString(String value) {
        return value.concat("°");
    }

    // определяет и возвращает цвет для TextView в зависимости от значения температуры
    public static int colorOfTemp(Context c, String temperature) {
        return ContextCompat.getColor(c, getColorOfTemperature(temperature));
    }

    static void printMessage(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    // добавление плюсового знака положительной температуре
    static String plus(String temperature) {
        if (Double.valueOf(temperature) > 0) {
            return "+";
        }
        return "";
    }

    private static int getColorOfTemperature(String w) {
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


    static abstract class DbUtils {

        // TODO необходимо реализовать кэш, на случай отсутствия интернет соединения

        static long putWeatherDbLine(String tableName, String[] keys, String[] valuesOfKeys) {
            // возвращает id последней загруженной строки
            return dbWeather.put(tableName, keys, valuesOfKeys);
        }

        static void putWeatherValuesArrInDb(String tableName, String key, String[] valuesOfKey) {
            dbWeather.putArr(tableName, key, valuesOfKey);
        }

        static void updWeatherValuesArrInDb(String tableName, String key, String[] valuesOfKey) {
            dbWeather.updArr(tableName, key, valuesOfKey);
        }

        static void updWeatherDbLine(String tableName, String id, String[] keys, String[] valuesOfKeys) {
            // TODO доработать прогноз, добавить данные
            // запись всей погодной информации о городе в базу
            updateLineDb(tableName, id, keys, valuesOfKeys);
        }

        static void putForecastTableName(String id, String tableName) {
            dbWeather.update(
                    TodayFragment.T_NAME,
                    id,
                    new String[]{DBKeys.C_FORECAST_TABLE_NAME},
                    new String[]{tableName});
        }

        static void delTable(String id, String tableName) {
            if (tableName != null) {
                boolean b = dbWeather.deleteTable(
                        dbWeather.getWritableDatabase(),
                        tableName);
//                if (b) printMessage(c, "Таблица прогноза удалена");
//                else printMessage(c, "Ошибка удаления таблицы прогноза");
            }

//            cursorReNew();
        }

        // возврат погодных параметров
        private static String getTodayValueFromDb(String id, String key) {
            return dbWeather.getFromDb(TodayFragment.T_NAME, id, DBKeys._ID, key);
        }

        private static String getForecastValueFromDb(String nameForecastTable, String id, String key) {
            return dbWeather.getFromDb(nameForecastTable, id, DBKeys._ID, key);
        }

        static String getCityFromDb(String id) {
            return dbWeather.getFromDb(TodayFragment.T_NAME, id, DBKeys._ID, DBKeys.C_CITY);
        }

        static String getLocation(String request) {
            return getTodayValueFromDb(request, DBKeys.C_METEOLOCATION);
        }

        static String getTemperature(String request) {
            return getTodayValueFromDb(request, DBKeys.C_TEMPERATURE);
        }

        static String getPressure(String request) {
            return getTodayValueFromDb(request, DBKeys.C_PRESSURE);
        }

        static String getTemperatureForecast(String request) {
            return getTodayValueFromDb(request, DBKeys.C_TEMPERATURE);
        }

        static String getPressureForecast(String request) {
            return getTodayValueFromDb(request, DBKeys.C_PRESSURE);
        }

        static String getLongitude(String request) {
            return getTodayValueFromDb(request, DBKeys.C_LONGITUDE);
        }

        static String getLatitude(String request) {
            return getTodayValueFromDb(request, DBKeys.C_LATITUDE);
        }

        static String getNameForecastTable(String request) {
            return getTodayValueFromDb(request, DBKeys.C_FORECAST_TABLE_NAME);
        }


    }
}