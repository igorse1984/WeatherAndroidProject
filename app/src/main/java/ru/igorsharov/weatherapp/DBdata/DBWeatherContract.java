package ru.igorsharov.weatherapp.DBdata;

import android.provider.BaseColumns;

import ru.igorsharov.weatherapp.TodayFragment;

/**
 * Public final class that contains information about table wInfo
 */

public final class DBWeatherContract {

    private DBWeatherContract() {
    }

    public static final class DBKeys implements BaseColumns {

        /* Private field for store SQL WHERE for one element (by id) */
        static final String SQL_WHERE_BY_ID = BaseColumns._ID + "=?";

        /* Public constant that store a name of data base */
        static final String DB_NAME = "DBWeather.db";

        /* Public constant that store a version of data base */
        static final int DB_VERSION = 1;

        /**
         * Name of this table.
         */


        public final static String _ID = BaseColumns._ID;
        public final static String C_CITY = "CITY";
        public final static String C_DATE = "DATE";
        public final static String C_METEOLOCATION = "LOCATION";
        public final static String C_LONGITUDE = "LONGITUDE";
        public final static String C_LATITUDE = "LATITUDE";
        public final static String C_TEMPERATURE = "TEMPERATURE";
        public final static String C_PRESSURE = "PRESSURE";
        public final static String C_ICON_WEATHER = "ICON_WEATHER";
        public final static String C_FORECAST_TABLE_NAME = "FORECAST_TABLE_NAME";

        public static String[] keysTodayArr =
                {
                        C_METEOLOCATION,
                        C_TEMPERATURE,
                        C_PRESSURE,
                        C_ICON_WEATHER
                };

        public static String[] googleKeys =
                {
                        C_CITY,
                        C_LONGITUDE,
                        C_LATITUDE
                };

        public static String[] keysOfForecastAdapter = {
                C_DATE,
                C_TEMPERATURE,
                C_PRESSURE,
                C_ICON_WEATHER};

        public static String[] keysForTodayAdapter = {
                C_CITY,
                C_TEMPERATURE,
                C_ICON_WEATHER};

        /**
         * SQL query for a create this table.
         */
        final static String SQL_CREATE_WEATHER_TODAY = "CREATE TABLE " + TodayFragment.T_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + C_CITY + " TEXT, "
                + C_METEOLOCATION + " TEXT, "
                + C_LONGITUDE + " TEXT, "
                + C_LATITUDE + " TEXT, "
                + C_TEMPERATURE + " INTEGER, "
                + C_PRESSURE + " INTEGER, "
                + C_ICON_WEATHER + " TEXT,"
                + C_FORECAST_TABLE_NAME + " TEXT, "
//                + C_FORECAST_TABLE_NAME + " TEXT);";
                // позволяет игнорировать добавление повторяющихся данных
                + "UNIQUE (" + C_CITY + ") ON CONFLICT IGNORE);";

        static String buildSqlCreateTableString(String tableName) {
            return "CREATE TABLE " + tableName + " ("
                    + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + C_DATE + " TEXT, "
                    + C_TEMPERATURE + " INTEGER, "
                    + C_PRESSURE + " INTEGER, "
                    + C_ICON_WEATHER + " TEXT);";
        }
    }
}
