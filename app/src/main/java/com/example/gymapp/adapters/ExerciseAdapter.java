package com.example.gymapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymapp.R;
import com.example.gymapp.models.Exercise;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private final Context context;
    private final List<Exercise> exercises;
    private OnExerciseClickListener listener;

    public ExerciseAdapter(Context context, List<Exercise> exercises) {
        this.context = context;
        this.exercises = exercises;
    }

    public void setOnExerciseClickListener(OnExerciseClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise ex = exercises.get(position);
        holder.exerciseName.setText(ex.getName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExerciseClick(ex.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public interface OnExerciseClickListener {
        void onExerciseClick(long exerciseId);
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.textViewExerciseName);
        }
    }


}
