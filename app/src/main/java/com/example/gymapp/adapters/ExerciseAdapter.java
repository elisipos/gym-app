package com.example.gymapp.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.gymapp.models.Exercise;

import java.text.SimpleDateFormat;
import java.util.List;

public class ExerciseAdapter extends ArrayAdapter<Exercise> {

    private Context context;
    private List<Exercise> exercises;
    private SimpleDateFormat sdf;

    public ExerciseAdapter(Context context, List<Exercise> exercises) {
        super(context, 0, exercises);
        this.context = context;
        this.exercises = exercises;
    }
}
