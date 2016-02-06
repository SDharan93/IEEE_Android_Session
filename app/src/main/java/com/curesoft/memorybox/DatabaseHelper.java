package com.curesoft.memorybox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by shane on 05/02/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "MEMORY_BOX";
    private static final int DATABASE_VERSION = 1;
    private static final String UID = "_ID";

    private static final String MEMORY_TABLE_NAME = "MEMORY_TB";
    private static final String MEMORY = "MEMORY";

    private static final String KEY_TABLE_NAME = "KEY_TB";
    private static final String MEMORY_ID = "MEMORY_ID";
    private static final String KEY = "KEY";

    private static final String FASTTABLE = "DICTONARY_TB";

    private static final String TAG = "DB";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //String createMemoryTable = "CREATE TABLE IF NOT EXISTS " + MEMORY_TABLE_NAME + " ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+MEMORY+" VARCHAR(255));";
        //String createKeyTable = "CREATE TABLE IF NOT EXISTS " + KEY_TABLE_NAME + " ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+MEMORY_ID+" INTEGER, "+KEY+" VARCHAR(50));";

        //create a fast table search ...
        String createFTS3 = "CREATE VIRTUAL TABLE "+FASTTABLE+" USING fts4()";

        try {
            //db.execSQL(createMemoryTable);
            //db.execSQL(createKeyTable);
            db.execSQL(createFTS3);
            Log.v(TAG, "Created the table or table exists.");
        } catch(SQLException e) {
            Log.d(TAG, "Error with creating tables.");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertData (String phrase, ArrayList<String> keywords) {
        SQLiteDatabase db = this.getWritableDatabase();
        //ContentValues content = new ContentValues();
        //content.put(MEMORY, phrase);
        db.execSQL("INSERT INTO " + FASTTABLE + " VALUES('" + phrase + "');");
        //long id = db.insert(MEMORY_TABLE_NAME, null, content);

        /*
        if(id == -1) {
            return false;
        } else {

            for(String word : keywords) {
                ContentValues temp = new ContentValues();
                temp.put(KEY, word);
                temp.put(MEMORY_ID, id);
                db.insert(KEY_TABLE_NAME,null,temp);
            }

            return true;
        }*/
    }

    /*
    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res //= db.rawQuery("SELECT DISTINCT "+MEMORY+ " FROM "+KEY_TABLE_NAME+ " LEFT JOIN "+MEMORY_TABLE_NAME+ "USING("+MEMORY_ID+") WHERE WORD IN", null);
        return res;
    }
*/
    public void dropTables () {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DROP TABLE IF EXISTS " + MEMORY_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS" + KEY_TABLE_NAME);
        }

        catch(SQLException e) {
            Log.d(TAG, "Error with dropping tables.");
        }
    }
}