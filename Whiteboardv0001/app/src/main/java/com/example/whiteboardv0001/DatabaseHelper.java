package com.example.whiteboardv0001;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "messages.db";
    public static final String TABLE_NAME = "messages_table";
    public static final String COL1 = "MESSAGE_ID";
    public static final String COL2 = "USER_ID";
    public static final String COL3 = "CHANNEL_ID";
    public static final String COL4 = "ROOM_ID";
    public static final String COL5 = "MESSAGE_STRING";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String  createTable = "CREATE TABLE " + TABLE_NAME + "(MESSAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                              "USER_ID TEXT, CHANNEL_ID TEXT, ROOM_ID TEXT, MESSAGE_STRING TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String userID, String channelID, String roomID, String message){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,userID);
        contentValues.put(COL3,channelID);
        contentValues.put(COL4,roomID);
        contentValues.put(COL5,message);

        // Check if data added successfully, a result of -1 means there is an error and
        // the data was not added to the database.
        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1){
            return false;
        } else {
            return true;
        }
    }
}