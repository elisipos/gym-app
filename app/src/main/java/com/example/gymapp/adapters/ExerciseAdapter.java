package com.example.gymapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gymapp.R;
import com.example.gymapp.models.Exercise;

import java.text.SimpleDateFormat;
import java.util.List;

public class ExerciseAdapter extends ArrayAdapter<Exercise> {

    private OnExerciseClickListener listener;
    private Context context;
    private List<Exercise> exercises;
    private SimpleDateFormat sdf;

    public ExerciseAdapter(Context context, List<Exercise> exercises) {
        super(context, 0, exercises);
        this.context = context;
        this.exercises = exercises;
    }

    public void setOnExerciseClickListener(OnExerciseClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Reuse old view if possible
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_exercise, parent, false);
        }

        Exercise ex = exercises.get(position);

        TextView textViewExerciseName = convertView.findViewById(R.id.textViewExerciseName);

        textViewExerciseName.setText(ex.getName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onExerciseClick(ex.getId());
                }
            }
        });

        return convertView;
    }

    public interface OnExerciseClickListener {
        void onExerciseClick(long exerciseId);
    }
}
