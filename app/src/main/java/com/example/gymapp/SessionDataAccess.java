package com.example.gymapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gymapp.models.Session;

import java.util.ArrayList;
import java.util.List;

public class SessionDataAccess {
    private SQLiteDatabase db;

    public SessionDataAccess(SQLiteDatabase db) {
        this.db = db;
    }

    // ---------------
    // Session Methods
    // ---------------

    public long addSession(long timestamp, String name) {
        ContentValues values = new ContentValues();
        values.put("date", timestamp);
        values.put("name", name);

        long id = db.insert("Session", null, values);
        db.close();

        return id;
    }

    public List<Session> getSessions(){
        List<Session> sessions = new ArrayList<>();
        String[] cols = new String[]{"id", "date", "name"};

        Cursor cursor = db.query("Session", cols,
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
        String[] cols = new String[]{"id", "date", "name"};
        String selection = "id = ?";
        String[] selectionArgs = { String.valueOf(sessionId) };

        Cursor cursor = db.query("Session", cols, selection, selectionArgs,
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

    public int updateSession(Session session) {
        long id = session.getId();
        long date = session.getDate();
        String name = session.getName();

        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("name", name);

        String whereClause = "id = ?";
        String[] whereArgs = new String[]{ String.valueOf(id) };

        return db.update("Session", values, whereClause, whereArgs);
    }

    public int deleteSession(long sessionId) {
        String selection = "id = ?";
        String[] selectionArgs = { String.valueOf(sessionId) };

        int deletedRows = db.delete("Session", selection, selectionArgs);

        db.close();

        return deletedRows;
    }
}
