package com.example.gymapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gymapp.models.SessionExercise;

import java.util.ArrayList;
import java.util.List;

public class SessionExerciseDataAccess {
    SQLiteDatabase db;

    SessionExerciseDataAccess(SQLiteDatabase db) {
        this.db = db;
    }

    public long addSessionExercise(long sessionId, long exerciseId, int exerciseOrder, int reps, double weight) {
        ContentValues values = new ContentValues();
        values.put("sessionId", sessionId);
        values.put("exerciseId", exerciseId);
        values.put("exerciseOrder", exerciseOrder);
        values.put("reps", reps);
        values.put("weight", weight);
        long id = db.insert("SessionExercise", null, values);

        db.close();
        return id;
    }

    public List<SessionExercise> getSessionExercises() {
        List<SessionExercise> list = new ArrayList<>();
        String[] cols = new String[]{"id", "sessionId", "exerciseId", "exerciseOrder", "reps", "weight"};

        Cursor cursor = db.query("SessionExercise", cols,
                null, null, null, null, "exerciseOrder ASC");

        while(cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            long sessionId = cursor.getLong(cursor.getColumnIndexOrThrow("sessionId"));
            long exerciseId = cursor.getLong(cursor.getColumnIndexOrThrow("exerciseId"));
            int exerciseOrder = cursor.getInt(cursor.getColumnIndexOrThrow("exerciseOrder"));
            int reps = cursor.getInt(cursor.getColumnIndexOrThrow("reps"));
            double weight = cursor.getDouble(cursor.getColumnIndexOrThrow("weight"));
            list.add(new SessionExercise(id, sessionId, exerciseId, exerciseOrder, reps, weight));
        }
        cursor.close();
        db.close();

        return list;
    }
}
