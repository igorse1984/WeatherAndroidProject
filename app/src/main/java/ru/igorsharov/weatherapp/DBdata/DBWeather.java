package ru.igorsharov.weatherapp.DBdata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import ru.igorsharov.weatherapp.AppDB;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract.DBKeys;
import ru.igorsharov.weatherapp.TodayFragment;

public class DBWeather extends DBHelper {

    /**
     * Constructor with one parameter that describe a link
     * to the Context object.
     *
     * @param context The context object.
     */
    public DBWeather(Context context) {
        super(context, DBKeys.DB_NAME, null, DBKeys.DB_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is
     * where the creation of tables and the initial population of the
     * tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        /* Create tables */
        execSQL(db, DBKeys.SQL_CREATE_WEATHER_TODAY);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything
     * else it needs to upgrade to the new schema version.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		/* Drop tables */
        dropTable(db, TodayFragment.T_NAME);

		/* Invoke onCreate method */
        this.onCreate(db);
    }


    private ContentValues fillContentValuesOfArr(String[] keys, String[] values) {
        // заполняем базу через объект вспомогательного класса
        ContentValues v = new ContentValues();
        for (int i = 0; i < keys.length; i++) {
            // Заполение значений
            v.put(keys[i], values[i]);
            Log.d("ContentValuesPut", keys[i] + " = " + values[i]);
        }
        return v;
    }

    /**
     * Добавляет информацию о погоде в базу данных
     */
    public long put(String tableName, String[] keys, String[] values) {

        ContentValues v = fillContentValuesOfArr(keys, values);

        // записываем значения в базу и возвращаем ID вставленной строки
        return getWritableDatabase().insert(tableName, null, v);
    }

    public void putArr(String tableName, String key, String[] values) {
        ContentValues v = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        for (String value : values) {
            v.put(key, value);
            db.insert(tableName, null, v);
        }
    }

    public void updArr(String tableName, String key, String[] values) {
        ContentValues v = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            v.put(key, value);
            db.update(
                    tableName,
                    v,
                    DBKeys.SQL_WHERE_BY_ID,
                    // TODO вместо i должен быть id, не могу понять откуда его правильнее брать
                    new String[]{String.valueOf(i+1)});
        }
    }

    /**
     * Метод для добавления данных в базу через контент-провайдера
     *
     * @return id of new element
     */
    long put(String tableName, ContentValues values) {
        return this.getWritableDatabase().insert(tableName, null, values);
    }

    /**
     * Получение значения из БД
     *
     * @param tableName     имя таблицы поиска
     * @param requestIn     текст запроса
     * @param requestInKey  по какому столбцу будем проходить в поиске совпадения с текстом запроса
     * @param requestOutKey из какого столбца брать ответ
     */
    public String getFromDb(String tableName, String requestIn, String requestInKey, String requestOutKey) {
        String sqlRequest = "SELECT " + requestOutKey
                + " FROM " + tableName
                + " WHERE " + requestInKey
                + "=" + "'" + requestIn + "'";
        Cursor cursor = AppDB.getDb().getRawReadableCursor(sqlRequest);
        if (cursor.moveToFirst()) {
            /* Calculate indexes of columns and getFromDb*/
            String str = cursor.getString(
                    cursor.getColumnIndex(requestOutKey));
            cursor.close();
            return str;
        }
        return null;
    }

    /**
     * Обновление информации о погоде в базе
     */
    public int update(String tableName, String id, String[] keys, String[] values) {

        ContentValues v = fillContentValuesOfArr(keys, values);

		/* Update information и возврат статуса выполнения*/
        return getWritableDatabase().update(
                tableName,
                v,
                DBKeys.SQL_WHERE_BY_ID,
                new String[]{id});
    }

    /**
     * Delete weather info from a data base.
     *
     * @param id of element that will be deleted
     */
    public void delete(String tableName, String id) {
        getWritableDatabase().delete(
                tableName,
                DBKeys.SQL_WHERE_BY_ID,
                new String[]{id});
    }

    public boolean deleteTable(SQLiteDatabase db, String tableName) {
        return execSQL(db, "DROP TABLE IF EXISTS " + tableName);
    }

    private Cursor getRawReadableCursor(String sqlRequest) {
        return getReadableDatabase().rawQuery(sqlRequest, null);
    }

    public boolean putTable(SQLiteDatabase db, String tableName) {
        String str = DBKeys.buildSqlCreateTableString(tableName);
        Log.d("@@@", str);
        return execSQL(db, str);
    }

}
