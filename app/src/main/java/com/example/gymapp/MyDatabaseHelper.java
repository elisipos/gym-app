package com.example.gymapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gymapp.models.Session;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "gym-app-db.db";
    private static final int DATABASE_VERSION = 1;

    // Table Info
    public static final String TABLE_SESSION = "Session";
    public static final String COLUMN_SESSION_ID = "session_id";
    public static final String TABLE_EXERCISE = "Exercise";
    public static final String COLUMN_EXERCISE_ID = "exercise_id";
    public static final String COLUMN_REPS = "reps";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String TABLE_SESSION_EXERCISE = "SessionExercise";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NAME = "name";

    // Create Table SQL
    private static final String CREATE_TABLE_SESSION =
            "CREATE TABLE " + TABLE_SESSION + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NAME + " TEXT, "
                    + COLUMN_DATE + " INTEGER" + ")";

    private static final String CREATE_TABLE_EXERCISE =
            "CREATE TABLE " + TABLE_EXERCISE + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NAME + " TEXT" + ")";

    private static final String CREATE_TABLE_SESSION_EXERCISE =
            "CREATE TABLE " + TABLE_SESSION_EXERCISE + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_SESSION_ID + " INTEGER NOT NULL, "
                    + COLUMN_EXERCISE_ID + " INTEGER NOT NULL, "
                    + COLUMN_REPS + " INTEGER, "
                    + COLUMN_WEIGHT + " REAL, "
                    + "FOREIGN KEY(" + COLUMN_SESSION_ID + ")" + "REFERENCES " + TABLE_SESSION + "(" + COLUMN_ID + "), "
                    + "FOREIGN KEY(" + COLUMN_EXERCISE_ID + ")" + "REFERENCES " + TABLE_EXERCISE + "(" + COLUMN_ID + ")"
                    + ")";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SESSION);
        db.execSQL(CREATE_TABLE_EXERCISE);
        db.execSQL(CREATE_TABLE_SESSION_EXERCISE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If table structure changes in future
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION_EXERCISE);
        onCreate(db);
    }

    // -------------------
    // DB Helper Methods
    // -------------------

    public long addSession(long timestamp, String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, timestamp);
        values.put(COLUMN_NAME, name);

        long id = db.insert(TABLE_SESSION, null, values);
        db.close();

        return id;
    }

    public List<Session> getSessions(){
        List<Session> sessions = new ArrayList<>();
        String[] cols = new String[]{"id", "date", "name"};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SESSION, cols,
                null, null, null, null, "date DESC");

        while(cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            long date = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            sessions.add(new Session(id, date, name));
        }
        cursor.close();
        db.close();

        return sessions;
    }

    public Session getSessionById(long sessionId){
        Session session = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] cols = new String[]{"id", "date", "name"};
        String selection = "id = ?";
        String[] selectionArgs = { String.valueOf(sessionId) };

        Cursor cursor = db.query(TABLE_SESSION, cols, selection, selectionArgs,
                null, null, null);

        if(cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            long date = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            session = new Session(id, date, name);
        }
        cursor.close();
        db.close();

        return session;
    }

    public int deleteSession(long sessionId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = "id = ?";
        String[] selectionArgs = { String.valueOf(sessionId) };

        int deletedRows = db.delete(TABLE_SESSION, selection, selectionArgs);

        db.close();

        return deletedRows;
    }

}

