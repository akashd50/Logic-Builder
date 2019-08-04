package com.akashd50.lb.persistense;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.akashd50.lb.objects.BoardData;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LB.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.DBEntry.TABLE_NAME + " (" +
                    DBContract.DBEntry._ID + " INTEGER PRIMARY KEY," +
                    DBContract.DBEntry.C_BOARD_ID + " INTEGER," +
                    DBContract.DBEntry.C_INDEX_X + " INTEGER," +
                    DBContract.DBEntry.C_INDEX_Y + " INTEGER,"+
                    DBContract.DBEntry.C_TYPE + " INTEGER," +
                    DBContract.DBEntry.C_STYLE + " INTEGER)";

    private static final String SQL_CREATE_BOARD_ENTRIES =
            "CREATE TABLE " + DBContract.DBEntry.BOARD_TABLE_NAME + " (" +
            DBContract.DBEntry._ID + " INTEGER PRIMARY KEY," +
            DBContract.DBEntry.C_BOARD_ID + " INTEGER,"+
            DBContract.DBEntry.BOARD_NAME + " TEXT,"+
            DBContract.DBEntry.DIMENSIONS_X + " INTEGER,"+
            DBContract.DBEntry.DIMENSIONS_Y + " INTEGER)";

    private static final String SQL_CREATE_APP_VARS_ENTRIES =
            "CREATE TABLE " + DBContract.DBEntry.APP_VARS_TABLE + " (" +
                    DBContract.DBEntry._ID + " INTEGER PRIMARY KEY," +
                    DBContract.DBEntry.APP_VARS_BOARD_ID+ " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.DBEntry.TABLE_NAME;

    private static final String SQL_DELETE_BOARD_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.DBEntry.BOARD_TABLE_NAME;

    private static final String SQL_DELETE_APP_VARS_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.DBEntry.APP_VARS_TABLE;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_BOARD_ENTRIES);
        db.execSQL(SQL_CREATE_APP_VARS_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_BOARD_ENTRIES);
        db.execSQL(SQL_DELETE_APP_VARS_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
