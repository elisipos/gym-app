package com.example.gymapp.item_decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymapp.R;
import com.example.gymapp.models.SessionExercise;

import java.util.HashMap;
import java.util.List;

public class DetailsDividerItemDecoration extends RecyclerView.ItemDecoration {

    private final Paint paint;
    private final int dividerHeight;
    private final int extraMargin;
    private final HashMap<Integer, Boolean> items;

    public DetailsDividerItemDecoration(Context context, int color, int dividerHeight, int extraMargin, HashMap<Integer, Boolean> items){
        this.paint = new Paint();
        this.paint.setColor(color);
        this.dividerHeight = dividerHeight;
        this.extraMargin = extraMargin;
        this.items = items;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state){
        int childCount = parent.getChildCount();

        for(int i = 0; i < childCount; i++){
            View child = parent.getChildAt(i);
            int adapterPos = parent.getChildAdapterPosition(child);

            if(adapterPos == RecyclerView.NO_POSITION || adapterPos + 1 >= items.size()){
                continue;
            }

            View viewAtPos = parent.getLayoutManager().findViewByPosition(adapterPos);
            if (viewAtPos == null) {
                continue;
            }

            boolean isSecondary = items.get(adapterPos + 1);

            View leftLookup = viewAtPos.findViewById(R.id.textViewRepsPrimary);
            if(leftLookup == null){
                continue;
            }

            float left = leftLookup.getLeft() - extraMargin;
            float right;

            if(isSecondary){
                View secLiteral = viewAtPos.findViewById(R.id.textViewRepsSecLiteral);
                right = secLiteral != null ? secLiteral.getRight() + extraMargin : left;
            } else {
                right = leftLookup.getRight() + extraMargin;
            }

            float top = child.getBottom();
            float bottom = top + dividerHeight;

            c.drawRect(left, top, right, bottom, paint);
        }
    }

}
