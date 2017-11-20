package ru.igorsharov.weatherapp.DBdata;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import ru.igorsharov.weatherapp.TodayFragment;

public class DBProvider extends ContentProvider {

    /* Строка идентификации (авторизации) контент провайдера */
    private static final String AUTHORITY = "ru.igorsharov.db";

    /* URI контент провайдера */
    public static final Uri CONTENT_URI = Uri.parse("content://" +
            AUTHORITY + "/items");

    /* Варианты URI */
    private static final int DB_ITEMS = 1;

    /* Uri Matcher для определения URI */
    private static final UriMatcher mUriMatcher;

    /* Секция статической инициализации */
    static {
        /* Шаблоны URI */
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, "items", DB_ITEMS);
    }

    /* Объект базы данных */
    private DBWeather mDBWeather;

    @Override
    public boolean onCreate() {

		/* Создание объекта БД */
        mDBWeather = new DBWeather(this.getContext());

		/* Всё прошло хорошо - вернем true */
        return true;

    }

    /**
     * Получает данные из БД в виде курсора.
     *
     * @param projection столбцы, котороые необходимо получить, null - все
     *                   столбцы
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

		/* Получаем курсор */
        Cursor cursor = mDBWeather.getReadableCursor(TodayFragment.T_NAME);

		/*
		 * Подписываемся на обновления данных по URI для контент-провайдера 
		 * */
        cursor.setNotificationUri(this.getContext().getContentResolver(), uri);
		
		/* Возвращаем курсор */
        return cursor;

    }

    /**
     * Возвращает тип данных по URI.
     */
    @Override
    public String getType(Uri uri) {
        if (uri != null)
            return "text/plain";
        else
            return null;
    }

    /*
     * Добавляем данные в БД. Метод вернет URI на новый объект.
     * */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
		/* Проверка URI */
        if (mUriMatcher.match(uri) != DB_ITEMS)
            throw new IllegalArgumentException("Unknow URI " + uri);

		/* Добавляем данные в БД */
        long id = mDBWeather.put(TodayFragment.T_NAME, values);
		
		/* 
		 * Подготовка данных для возврата из метода и уведомление об изменениях 
		 * */
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }
			
		/* Если что-то пошло не так - null */
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Auto-generated method stub
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // Auto-generated method stub
        return 0;
    }

}
