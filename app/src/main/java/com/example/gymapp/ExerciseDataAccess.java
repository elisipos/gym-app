package com.example.gymapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    public long addExercise(String name, boolean split) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("split", split ? 1 : 0);

        long id = db.insert("Exercise", null, values);

        return id;
    }

    public List<Exercise> getExercises(){
        List<Exercise> exercises = new ArrayList<>();
        String[] cols = new String[]{"id", "name", "split"};

        Cursor cursor = db.query("Exercise", cols,
                null, null, null, null, null);

        while(cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            int split = cursor.getInt(cursor.getColumnIndexOrThrow("split"));
            exercises.add(new Exercise(id, name, split == 1));
        }
        cursor.close();

        return exercises;
    }

    public Exercise getExerciseById(long exerciseId){
        Exercise exercise = null;
        String[] cols = new String[]{"id", "name", "split"};
        String selection = "id = ?";
        String[] selectionArgs = { String.valueOf(exerciseId) };

        Cursor cursor = db.query("Exercise", cols, selection, selectionArgs,
                null, null, null);

        if(cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            int split = cursor.getInt(cursor.getColumnIndexOrThrow("split"));
            exercise = new Exercise(id, name, split == 1);
        }
        cursor.close();

        return exercise;
    }

    public int updateExercise(Exercise exercise) {
        long id = exercise.getId();
        String name = exercise.getName();
        boolean split = exercise.getSplit();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("split", split ? 1 : 0);

        String whereClause = "id = ?";
        String[] whereArgs = new String[]{ String.valueOf(id) };

        return db.update("Exercise", values, whereClause, whereArgs);
    }

    public int deleteExercise(long exerciseId) {
        String selection = "id = ?";
        String[] selectionArgs = { String.valueOf(exerciseId) };

        int deletedRows = db.delete("Exercise", selection, selectionArgs);

        return deletedRows;
    }
}
