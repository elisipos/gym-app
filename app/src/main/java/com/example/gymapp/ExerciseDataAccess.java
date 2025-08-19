package com.example.gymapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gymapp.models.Exercise;
import com.example.gymapp.models.Exercise;
import com.example.gymapp.models.Exercise;

import java.util.ArrayList;
import java.util.List;

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

    public List<Exercise> getExercises(){
        List<Exercise> exercises = new ArrayList<>();
        String[] cols = new String[]{"id", "name"};

        Cursor cursor = db.query("Exercise", cols,
                null, null, null, null, null);

        while(cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            exercises.add(new Exercise(id, name));
        }
        cursor.close();
        db.close();

        return exercises;
    }

    public Exercise getExerciseById(long exerciseId){
        Exercise exercise = null;
        String[] cols = new String[]{"id", "name"};
        String selection = "id = ?";
        String[] selectionArgs = { String.valueOf(exerciseId) };

        Cursor cursor = db.query("Exercise", cols, selection, selectionArgs,
                null, null, null);

        if(cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            exercise = new Exercise(id, name);
        }
        cursor.close();
        db.close();

        return exercise;
    }

    public int updateExercise(Exercise exercise) {
        long id = exercise.getId();
        String name = exercise.getName();

        ContentValues values = new ContentValues();
        values.put("name", name);

        String whereClause = "id = ?";
        String[] whereArgs = new String[]{ String.valueOf(id) };

        return db.update("Exercise", values, whereClause, whereArgs);
    }

    public int deleteExercise(long exerciseId) {
        String selection = "id = ?";
        String[] selectionArgs = { String.valueOf(exerciseId) };

        int deletedRows = db.delete("Exercise", selection, selectionArgs);

        db.close();

        return deletedRows;
    }
}
