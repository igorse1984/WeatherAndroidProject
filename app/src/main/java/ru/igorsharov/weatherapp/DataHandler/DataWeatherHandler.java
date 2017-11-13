package ru.igorsharov.weatherapp.DataHandler;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import ru.igorsharov.weatherapp.AppDB;
import ru.igorsharov.weatherapp.DBdata.DBWeather;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract;
import ru.igorsharov.weatherapp.R;

/**
 * Делает запросы парсеру на получение необходимых данных и
 * кладет их в базу данных, работает с запросами погодных параметров
 */

// TODO что будет если добавлять одновременно много городов?
public abstract class DataWeatherHandler {

    // TODO разобраться в вопросе статики для БД
    static DBWeather dbWeather = AppDB.getDb();


    public static void loadForecast(String... params) {
        String TABLE_NAME = params[0];
        String lng = params[1];
        String lat = params[2];
        JSONObject jo = NetUtils.loadWeather(lng, lat, true);
        ParsingUtils.setWeatherJSONParser(jo);

        ArrayList<String[]> al = new ArrayList<>();
        al.add(ParsingUtils.parseDateForecast());
        al.add(ParsingUtils.parseTempForecast());
        al.add(ParsingUtils.parsePressureForecast());
        al.add(ParsingUtils.parseIconIdArr());

        DbUtils.addWeatherArrInDb(
                TABLE_NAME,
                DBWeatherContract.DBKeys.keysOfForecastAdapter,
                al);
    }



    // собирает и возвращает строку погоды для установки в TextView
    public static String addDegree(String value) {
        return value.concat("°");
    }

    // определяет и возвращает цвет для TextView в зависимости от значения температуры
    public static int colorOfTemp(Context c, String temperature) {
        return ContextCompat.getColor(c, getColorOfTemperature(temperature));
    }

    public static void printMessage(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
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


}