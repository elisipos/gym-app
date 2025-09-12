package com.example.gymapp.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.gymapp.models.Session;
import com.example.gymapp.models.SessionExercise;

import java.text.SimpleDateFormat;
import java.util.List;

public class SessionExerciseAdapter extends ArrayAdapter<SessionExercise> {
    private Context context;
    private List<SessionExercise> sessionExercises;
    private SimpleDateFormat sdf;

    public SessionExerciseAdapter(Context context, List<SessionExercise> sessionExercises) {
        super(context, 0, sessionExercises);
        this.context = context;
        this.sessionExercises = sessionExercises;
    }
}
