package com.curesoft.memorybox;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by shane on 05/02/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "MemoryBox";
    private static final int DATABASE_VERSION = 1;
    private static final String UID = "_id";

    private static final String MEMORY_TABLE_NAME = "Memory_tb";
    private static final String Memory = "Memory";

    private static final String KEY_TABLE_NAME = "Key_tb";
    private static final String Key = "Key";

    private static final String TAG = "DB";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createMemoryTable = "CREATE TABLE IF NOT EXISTS " + MEMORY_TABLE_NAME + " ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+Memory+" VARCHAR(255));";
        String createKeyTable = "CREATE TABLE IF NOT EXISTS " + KEY_TABLE_NAME + " ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+Key+" VARCHAR(50));";

        try {
            db.execSQL(createMemoryTable);
            db.execSQL(createKeyTable);
        } catch(SQLException e) {
            Log.d(TAG, "Error with creating tables");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}