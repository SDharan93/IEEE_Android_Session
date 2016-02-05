package com.curesoft.memorybox;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shane on 05/02/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    String DATABASE_NAME = "MemoryBox";
    String TABLE_NAME = ""

    public DatabaseHelper(Context context) {
        super(context, "", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
