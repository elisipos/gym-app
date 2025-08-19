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
    public static final String COLUMN_SESSION_ID = "sessionId";
    public static final String TABLE_EXERCISE = "Exercise";
    public static final String COLUMN_EXERCISE_ID = "exerciseId";
    public static final String COLUMN_EXERCISE_ORDER = "exerciseOrder";
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

}

