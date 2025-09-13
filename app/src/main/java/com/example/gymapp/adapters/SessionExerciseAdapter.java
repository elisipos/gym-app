package com.example.gymapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gymapp.MyDatabaseHelper;
import com.example.gymapp.R;
import com.example.gymapp.SessionDetailsActivity;
import com.example.gymapp.SessionExerciseDataAccess;
import com.example.gymapp.models.SessionExercise;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SessionExerciseAdapter extends ArrayAdapter<SessionExercise> {
    private Context context;
    private List<SessionExercise> sessionExercises;
    private MyDatabaseHelper dbHelper;//
    private SessionExerciseDataAccess seda;//

    public SessionExerciseAdapter(Context context, List<SessionExercise> sessionExercises) {
        super(context, 0, sessionExercises);
        this.context = context;
        this.sessionExercises = sessionExercises;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Reuse old view if possible
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_exercise, parent, false);
        }

        // Get the current Session
        SessionExercise se = sessionExercises.get(position);

        // Bind data
        TextView nameText = convertView.findViewById(R.id.textViewExerciseName);
        TextView repsText = convertView.findViewById(R.id.textViewReps);
        TextView weightText = convertView.findViewById(R.id.textViewWeight);

        nameText.setText();
        repsText.setText(String.valueOf(se.getReps()));
        weightText.setText(String.valueOf(se.getWeight()));

//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(context, SessionDetailsActivity.class);
//                i.putExtra("session_id", session.getId());
//                context.startActivity(i);
//            }
//        });

        return convertView;
    }
}
