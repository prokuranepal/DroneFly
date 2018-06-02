package com.example.swainstha.dronefly;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by swainstha on 6/2/18.
 */

public class StatusDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + StatusSave.StatusEntry.TABLE_NAME + " (" +
                    StatusSave.StatusEntry._ID + " INTEGER PRIMARY KEY," +
                    StatusSave.StatusEntry.COLUMN_NAME_1 + " REAL," +
                    StatusSave.StatusEntry.COLUMN_NAME_2 + " REAL," +
                    StatusSave.StatusEntry.COLUMN_NAME_3 + " REAL," +
                    StatusSave.StatusEntry.COLUMN_NAME_4 + " INTEGER," +
                    StatusSave.StatusEntry.COLUMN_NAME_5 + " TEXT," +
                    StatusSave.StatusEntry.COLUMN_NAME_6 + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + StatusSave.StatusEntry.TABLE_NAME;

    public StatusDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
