package com.example.gymapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class ExerciseDataAccess {
    private SQLiteDatabase db;

    public ExerciseDataAccess(SQLiteDatabase db) {
        this.db = db;
    }

    // ----------------
    // Exercise Methods
    // ----------------

    public long addExercise(long timestamp, String name) {
        ContentValues values = new ContentValues();
        values.put("date", timestamp);
        values.put("name", name);

        long id = db.insert("Exercise", null, values);
        db.close();

        return id;
    }
}
