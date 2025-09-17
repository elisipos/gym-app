package com.example.gymapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymapp.R;
import com.example.gymapp.models.SessionExercise;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public class SessionExerciseAdapter extends RecyclerView.Adapter<SessionExerciseAdapter.ExerciseViewHolder> {
    private Context context;
    private List<SessionExercise> sessionExerciseList;
    private DecimalFormat df;
    private ItemTouchHelper itemTouchHelper;

    public SessionExerciseAdapter(Context context, List<SessionExercise> sessionExerciseList) {
//        super(context, 0, sessionExercises);
//        this.context = context;
        this.sessionExerciseList = sessionExerciseList;
    }

    public void setItemTouchHelper(ItemTouchHelper helper) {
        this.itemTouchHelper = helper;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_session_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        SessionExercise exercise = sessionExerciseList.get(position);
        df = new DecimalFormat("#.##");
        holder.nameText.setText(exercise.getName() + ", " + exercise.getExerciseOrder());
        holder.repsText.setText(exercise.getReps() + " reps");
        holder.weightText.setText(df.format(exercise.getWeight()) + " lbs");

        holder.dragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (itemTouchHelper != null) {
                    itemTouchHelper.startDrag(holder);
                }
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return sessionExerciseList.size();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(sessionExerciseList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, repsText, weightText;
        ImageView dragHandle;
        ExerciseViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textViewExerciseName);
            repsText = itemView.findViewById(R.id.textViewReps);
            weightText = itemView.findViewById(R.id.textViewWeight);
            dragHandle = itemView.findViewById(R.id.dragHandle);
        }
    }

//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        // Reuse old view if possible
//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_session_exercise, parent, false);
//        }
//
//        // Get the current Session
//        SessionExercise se = sessionExercises.get(position);
//
//        // Bind data
//        TextView nameText = convertView.findViewById(R.id.textViewExerciseName);
//        TextView repsText = convertView.findViewById(R.id.textViewReps);
//        TextView weightText = convertView.findViewById(R.id.textViewWeight);
//

//
//        nameText.setText(se.getName() + ", " + se.getExerciseOrder());
//        repsText.setText(se.getReps() + " reps");
//        weightText.setText(df.format(se.getWeight())  + " lbs");

//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(context, SessionDetailsActivity.class);
//                i.putExtra("session_id", session.getId());
//                context.startActivity(i);
//            }
//        });

//        return convertView;
//    }
}
