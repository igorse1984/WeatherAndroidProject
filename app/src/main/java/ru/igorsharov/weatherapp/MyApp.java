package android_2.lesson01.app01;

import android.app.Application;
import android.util.Log;

import android_2.lesson01.app01.lib.DBEmpl;

public class MyApp extends Application {

    /* Static file for store a link to the data base object */
    private static DBEmpl mDBEmpl;

    /* Private field for store a LOG tag */
    public static final String LOG_TAG = "MyApp";

    @Override
    public void onCreate() {

		/* Invoke a parent method */
        super.onCreate();

		/* Create a data base object */
        mDBEmpl = new DBEmpl(getApplicationContext());
		
		/* Write a log */
        Log.d(LOG_TAG, "Application onCreate");
    }

    /**
     * Get a link to the data base object
     */
    public static DBEmpl getDb() {
        return mDBEmpl;
    }
}
