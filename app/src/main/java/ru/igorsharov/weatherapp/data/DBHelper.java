package ru.igorsharov.weatherapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


abstract class DBHelper extends SQLiteOpenHelper {

    private static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";
    /* Private fields for store a link to the Context value */
    private Context mContext;

    /* Constructor from super class */
    DBHelper(Context context, String name, CursorFactory factory,
             int version) {

		/* Invoke a parent method */
        super(context, name, factory, version);

		/* Setting up Context value  */
        mContext = context;
    }

    /**
     * Get a link to the current Context object.
     *
     * @return Context object that using for this object.
     */
    Context getContext() {
        return mContext;
    }

    /**
     * Get readable cursor for a table.
     *
     * @param table the	name of table
     * @return Readable Cursor for this table.
     */
    public Cursor getReadableCursor(String table) {
        return getReadableDatabase().query(table, null, null, null, null,
                null, null);
    }


    /**
     * Execute a single SQL statement that is NOT a SELECT or any other SQL
     * statement that returns data.
     *
     * @param sql the SQL statement to be executed. Multiple statements
     *            separated by semicolons are not supported.
     */
    static boolean execSQL(SQLiteDatabase db, String sql) {

		/* Checking a DB object */
        if (db == null) return false;

		/* Try to execute SQL request */
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            return false;
        }

		/* Return true value */
        return true;
    }

    /**
     * Execute SQL query for drop table from data base.
     *
     * @param db    the data base
     * @param table name that will be deleted
     */
    static boolean dropTable(SQLiteDatabase db, String table) {
        return execSQL(db, DROP_TABLE_IF_EXISTS + table);
    }
}