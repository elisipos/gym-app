package com.example.gymapp.item_decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymapp.models.SessionExercise;

import java.util.List;

public class GroupDividerItemDecoration extends RecyclerView.ItemDecoration {

    private final Paint paint;
    private final int dividerHeight;
    private final int margin;
    private final List<SessionExercise> items;

    public GroupDividerItemDecoration(Context context, int color, int dividerHeight, int margin, List<SessionExercise> items) {
        this.paint = new Paint();
        this.paint.setColor(color);
        this.dividerHeight = dividerHeight;
        this.margin = margin;
        this.items = items;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int totalItemCount = items.size();

        for (int i = 0; i < childCount - 1; i++) {
            View child = parent.getChildAt(i);
            int adapterPosition = parent.getChildAdapterPosition(child);

            if (adapterPosition == RecyclerView.NO_POSITION || adapterPosition >= totalItemCount - 1) {
                continue;
            }

            SessionExercise current = items.get(adapterPosition);
            SessionExercise next = items.get(adapterPosition + 1);

            if (current.getExerciseId() != next.getExerciseId()) {
                float left = parent.getPaddingLeft() + margin;
                float right = parent.getWidth() - parent.getPaddingRight() - margin;
                float top = child.getBottom();
                float bottom = top + dividerHeight;

                c.drawRect(left, top, right, bottom, paint);
            }
        }
    }
}
