package com.example.gymapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymapp.R;
import com.example.gymapp.models.Exercise;
import com.example.gymapp.models.SessionExercise;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public class SessionExerciseAdapter extends RecyclerView.Adapter<SessionExerciseAdapter.SessionExerciseViewHolder> {
    private Context context;
    private List<SessionExercise> sessionExerciseList;
    private List<Exercise> exerciseList;
    private LongSparseArray<Exercise> exerciseById;
    private DecimalFormat df;
    private ItemTouchHelper itemTouchHelper;
    private OnExerciseLongClickListener longClickListener;
    private ConstraintLayout constraintLayout;

    public SessionExerciseAdapter(Context context, List<SessionExercise> sessionExerciseList, List<Exercise> exerciseList) {
        this.sessionExerciseList = sessionExerciseList;
        this.exerciseList = exerciseList;

        exerciseById = new LongSparseArray<>();
        for(Exercise ex : exerciseList) {
            exerciseById.put(ex.getId(), ex);
        }
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

        SessionExercise exercise = sessionExerciseList.get(position);
        Exercise exerciseMeta = exerciseById.get(exercise.getExerciseId());

        // Formatting //
        df = new DecimalFormat("#.##");


        holder.repsSecondaryText.setVisibility(View.VISIBLE);
        holder.repsPrimaryTextLiteral.setVisibility(View.VISIBLE);
        holder.repsPrimaryText.setVisibility(View.VISIBLE);
        holder.repsSecondaryTextLiteral.setVisibility(View.VISIBLE);

        constraintLayout = holder.layout;
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);

        if(exerciseMeta != null && exerciseMeta.getSplit()) {
            // the exercise IS split
            holder.repsPrimaryText.setText( String.valueOf( exercise.getRepsPrimary() ) );
            holder.repsSecondaryText.setText( String.valueOf( exercise.getRepsSecondary() ) );

            set.connect(R.id.textViewRepsPriLiteral, ConstraintSet.END, R.id.textViewWeight, ConstraintSet.START);
            set.connect(R.id.textViewRepsPrimary, ConstraintSet.END, R.id.textViewRepsPriLiteral, ConstraintSet.START);
            set.clear(R.id.textViewRepsPrimary, ConstraintSet.START);
            set.applyTo(constraintLayout);

        } else {
            // the exercise IS NOT split
            holder.repsPrimaryTextLiteral.setVisibility(View.GONE);
            holder.repsPrimaryTextLiteral.setText("");
            set.connect(R.id.textViewRepsPrimary, ConstraintSet.START, R.id.textViewWeight, ConstraintSet.START);
            set.connect(R.id.textViewRepsPrimary, ConstraintSet.END, R.id.textViewLbs, ConstraintSet.END);
            holder.repsPrimaryText.setText(exercise.getRepsPrimary() + " reps");

            set.applyTo(constraintLayout);

            holder.repsSecondaryText.setVisibility(View.INVISIBLE);
            holder.repsSecondaryTextLiteral.setVisibility(View.INVISIBLE);
//            holder.repsPrimaryText.setText( String.valueOf( exercise.getRepsPrimary() ) );

        }

        holder.nameText.setText(exercise.getName());
        holder.weightText.setText( df.format( exercise.getWeight() ) );
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
        ConstraintLayout layout;
        TextView nameText, repsPrimaryText, repsPrimaryTextLiteral, repsSecondaryText, repsSecondaryTextLiteral, weightText;
        ImageView dragHandle;
        SessionExerciseViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.listItemSessionExerciseLayout);
            nameText = itemView.findViewById(R.id.textViewExerciseName);
            repsPrimaryText = itemView.findViewById(R.id.textViewRepsPrimary);
            repsPrimaryTextLiteral = itemView.findViewById(R.id.textViewRepsPriLiteral);
            repsSecondaryText = itemView.findViewById(R.id.textViewRepsSecondary);
            repsSecondaryTextLiteral = itemView.findViewById(R.id.textViewRepsSecLiteral);
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


