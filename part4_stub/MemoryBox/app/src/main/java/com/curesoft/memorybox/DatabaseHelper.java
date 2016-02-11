package com.curesoft.memorybox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "MEMORY_BOX";
    private static final int DATABASE_VERSION = 2;
    private static final String UID = "_ID";

    private static final String MEMORY_TABLE_NAME = "MEMORY_TB";
    private static final String MEMORY = "MEMORY";

    private static final String KEY_TABLE_NAME = "KEY_TB";
    private static final String MEMORY_ID = "MEMORY_ID";
    private static final String KEY = "KEY";

    private static final String FASTTABLE = "DICTONARY_TB";
    private static final String DATETIME = "DATETIME";

    private static final String TAG = "DB";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create a fast text search ...
        String createFTS3 = "CREATE VIRTUAL TABLE "+FASTTABLE+" USING fts4("+MEMORY+", "+DATETIME+ ");";

        try {
            db.execSQL(createFTS3);
        } catch(SQLException e) {
            Log.e(TAG, "Error with creating tables.");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+FASTTABLE);
        onCreate(db);
    }

    public void insertData (String phrase) {
        SQLiteDatabase db = this.getWritableDatabase();

        //get the current time
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        ContentValues content = new ContentValues();
        content.put(MEMORY, phrase);
        content.put(DATETIME, formattedDate);

        long id = db.insert(FASTTABLE, null, content);
        if(id == -1) {
            Log.e(TAG, "Failed to insert");
        }
    }

    public Cursor getData(ArrayList<String> keywords) {
        SQLiteDatabase db = this.getWritableDatabase();
        //String used for search query with all the keywords.
        String search = "";
        int counter = 0;
        for(String word: keywords) {

            //do not add space to the first word.
            if(counter == 0) {
                search += word;
            } else {
                search += " OR "+word;
            }
        }

        String query = "SELECT * FROM "+FASTTABLE+" WHERE "+MEMORY+" MATCH '"+search+"' ORDER BY "+DATETIME+" DESC;";
        Cursor res = db.rawQuery(query, null);
        return res;
    }
}
