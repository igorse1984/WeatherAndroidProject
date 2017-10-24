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
        public final static String C_LOCATION = "LOCATION";
        public final static String C_TEMPERATURE = "TEMPERATURE";
        public final static String C_PRESSURE = "PRESSURE";
        public final static String C_TEMPERATURE_FORECAST = "TEMPERATURE_FORECAST";
        public final static String C_PRESSURE_FORECAST = "PRESSURE_FORECAST";

        /**
         * SQL query for a create this table.
         */
        final static String SQL_CREATE = "CREATE TABLE " + T_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + C_CITY + " TEXT NOT NULL, "
                + C_LOCATION + " TEXT NOT NULL DEFAULT '', "
                + C_TEMPERATURE + " INTEGER NOT NULL DEFAULT 0, "
                + C_PRESSURE + " INTEGER NOT NULL DEFAULT 0, "
                + C_TEMPERATURE_FORECAST + " INTEGER NOT NULL DEFAULT 0, "
                + C_PRESSURE_FORECAST + " INTEGER NOT NULL DEFAULT 0);";

    }
}
