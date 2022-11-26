package com.example.MyList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

interface DBContract {
    static final String TABLE_NAME="SCHEDULE_T";
    static final String COL_ID="ID";
    static final String COL_TITLE="SCHEDULE";
    static final String COL_WHEN="DATE";
    static final String COL_WHERE="PLACE";
    static final String COL_DETAIL="DETAIL";

    static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + "(" +
            COL_ID + " INTEGER NOT NULL PRIMARY KEY, " +
            COL_TITLE + " TEXT NOT NULL, " +
            COL_WHEN + " TEXT NOT NULL, " +
            COL_WHERE + " TEXT, " +
            COL_DETAIL + " TEXT NOT NULL)";
    static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    static final String SQL_LOAD = "SELECT * FROM " + TABLE_NAME;
    static final String SQL_SELECT = "SELECT * FROM "  + TABLE_NAME + " WHERE " + COL_TITLE + "=?";
    static final String SQL_SELECT_ID = "SELECT ID FROM "  + TABLE_NAME + " WHERE " + COL_TITLE + "=? and " + COL_WHEN + "=?";
}

class DBHelper extends SQLiteOpenHelper {
    static final String DB_FILE = "schedule_t.db";
    static final int DB_VERSION = 1;

    DBHelper(Context context) {
        super(context, DB_FILE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.SQL_DROP_TABLE);
        onCreate(db);
    }
}
