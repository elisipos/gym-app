package com.example.gymapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gymapp.R;
import com.example.gymapp.models.SessionExercise;

import java.text.DecimalFormat;
import java.util.List;

public class SessionExerciseAdapter extends ArrayAdapter<SessionExercise> {
    private Context context;
    private List<SessionExercise> sessionExercises;
    private DecimalFormat df;

    public SessionExerciseAdapter(Context context, List<SessionExercise> sessionExercises) {
        super(context, 0, sessionExercises);
        this.context = context;
        this.sessionExercises = sessionExercises;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Reuse old view if possible
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_session_exercise, parent, false);
        }

        // Get the current Session
        SessionExercise se = sessionExercises.get(position);

        // Bind data
        TextView nameText = convertView.findViewById(R.id.textViewExerciseName);
        TextView repsText = convertView.findViewById(R.id.textViewReps);
        TextView weightText = convertView.findViewById(R.id.textViewWeight);

        df = new DecimalFormat("#.##");

        nameText.setText(se.getName());
        repsText.setText(se.getReps() + " reps");
        weightText.setText(df.format(se.getWeight())  + " lbs");

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
