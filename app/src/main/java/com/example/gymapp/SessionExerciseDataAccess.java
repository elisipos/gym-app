package com.example.gymapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gymapp.models.Session;
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

        return db.insert("SessionExercise", null, values);
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

        return list;
    }

    public SessionExercise getSessionExerciseById(long esId){
        SessionExercise sessionExercise = null;
        String[] cols = new String[]{"id", "sessionId", "exerciseId", "exerciseOrder", "reps", "weight"};
        String selection = "id = ?";
        String[] selectionArgs = { String.valueOf(esId) };
        Cursor cursor = db.query("SessionExercise", cols, selection, selectionArgs,
                null, null, null);

        if(cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            long sessionId = cursor.getLong(cursor.getColumnIndexOrThrow("sessionId"));
            long exerciseId = cursor.getLong(cursor.getColumnIndexOrThrow("exerciseId"));
            int exerciseOrder = cursor.getInt(cursor.getColumnIndexOrThrow("exerciseOrder"));
            int reps = cursor.getInt(cursor.getColumnIndexOrThrow("reps"));
            double weight = cursor.getDouble(cursor.getColumnIndexOrThrow("weight"));
            sessionExercise = new SessionExercise(id, sessionId, exerciseId, exerciseOrder, reps, weight);
        }
        cursor.close();

        return sessionExercise;
    }

    public List<SessionExercise> getExercisesBySessionId(long sId) {
        List<SessionExercise> list = new ArrayList<>();
        String[] cols = new String[]{"id", "sessionId", "exerciseId", "exerciseOrder", "reps", "weight"};
        String selection = "sessionId = ?";
        String[] selectionArgs = {String.valueOf(sId)};

        Cursor cursor = db.query("SessionExercise", cols, selection, selectionArgs,
                null, null, "exerciseOrder ASC");

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

        return list;
    }

    public int updateSessionExercise(SessionExercise se) {
        ContentValues values = new ContentValues();
        values.put("sessionId", se.getSessionId());
        values.put("exerciseId", se.getExerciseId());
        values.put("exerciseOrder", se.getExerciseOrder());
        values.put("reps", se.getReps());
        values.put("weight", se.getWeight());

        String whereClause = "id = ?";
        String[] whereArgs = new String[]{ String.valueOf(se.getId()) };


        return db.update("SessionExercise", values, whereClause, whereArgs);
    }

    public int deleteSessionExercise(long seId){
        String whereClause = "id = ?";
        String[] whereArgs = new String[]{ String.valueOf(seId) };


        return db.delete("SessionExercise", whereClause, whereArgs);
    }
}
