package ru.igorsharov.weatherapp.DBdata;

import android.provider.BaseColumns;

/**
 * Public final class that contains information about table wInfo
 */

public final class DBWeatherContract {

    private DBWeatherContract() {
    }

    public static final class WeatherEntry implements BaseColumns {

        /* Private field for store SQL WHERE for one element (by id) */
        static final String SQL_WHERE_BY_ID = BaseColumns._ID + "=?";

        /* Public constant that store a name of data base */
        static final String DB_NAME = "DBWeather.db";

        /* Public constant that store a version of data base */
        static final int DB_VERSION = 1;

        /**
         * Name of this table.
         */
        public final static String T_NAME = "wInfo";

        final static String _ID = BaseColumns._ID;
        public final static String C_CITY = "CITY";
        public final static String C_METEOSTATION = "LOCATION";
        public final static String C_LONGITUDE = "LONGITUDE";
        public final static String C_LATITUDE = "LATITUDE";
        public final static String C_TEMPERATURE_TODAY = "TEMPERATURE";
        public final static String C_PRESSURE_TODAY = "PRESSURE";
        public final static String C_TEMPERATURE_FORECAST = "TEMPERATURE_FORECAST";
        public final static String C_PRESSURE_FORECAST = "PRESSURE_FORECAST";
        public final static String C_ICON_WEATHER = "IMG_WEATHER";

        /**
         * SQL query for a create this table.
         */
        final static String SQL_CREATE = "CREATE TABLE " + T_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + C_CITY + " TEXT, "
                + C_METEOSTATION + " TEXT, "
                + C_LONGITUDE + " TEXT, "
                + C_LATITUDE + " TEXT, "
                + C_TEMPERATURE_TODAY + " INTEGER, "
                + C_PRESSURE_TODAY + " INTEGER, "
                + C_TEMPERATURE_FORECAST + " INTEGER, "
                + C_PRESSURE_FORECAST + " INTEGER,"
                + C_ICON_WEATHER + " TEXT,"
                // позволяет игнорировать добавление повторяющихся данных
                + "UNIQUE (" + C_CITY + ") ON CONFLICT IGNORE);";

    }
}
