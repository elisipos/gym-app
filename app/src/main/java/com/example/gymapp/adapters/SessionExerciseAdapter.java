package com.example.gymapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

public class SessionExerciseAdapter extends RecyclerView.Adapter<SessionExerciseAdapter.SessionExerciseViewHolder> {
    private Context context;
    private List<SessionExercise> sessionExerciseList;
    private DecimalFormat df;
    private ItemTouchHelper itemTouchHelper;
    private OnExerciseLongClickListener longClickListener;

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
    public SessionExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_session_exercise, parent, false);
        return new SessionExerciseViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull SessionExerciseViewHolder holder, int position) {

        // Formatting //
        SessionExercise exercise = sessionExerciseList.get(position);
        df = new DecimalFormat("#.##");
        holder.nameText.setText(exercise.getName());
        holder.repsText.setText(exercise.getReps() + " reps");
        holder.weightText.setText(df.format(exercise.getWeight()) + " lbs");
        // End Formatting //

        // Defining Touch Events //
        holder.dragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (itemTouchHelper != null) {
                    itemTouchHelper.startDrag(holder);
                }
            }
            return false;
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(longClickListener != null) {
                    longClickListener.onExerciseLongClick(v, holder.getAdapterPosition());
                }
                return true;
            }
        });
        // End Touch Events //
    }

    @Override
    public int getItemCount() {
        return sessionExerciseList.size();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(sessionExerciseList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    static class SessionExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, repsText, weightText;
        ImageView dragHandle;
        SessionExerciseViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textViewExerciseName);
            repsText = itemView.findViewById(R.id.textViewReps);
            weightText = itemView.findViewById(R.id.textViewWeight);
            dragHandle = itemView.findViewById(R.id.dragHandle);
        }
    }

    public interface OnExerciseLongClickListener {
        void onExerciseLongClick(View view, int position);
    }

    public void setOnExerciseLongClickListener(OnExerciseLongClickListener listener) {
        this.longClickListener = listener;
    }

}


