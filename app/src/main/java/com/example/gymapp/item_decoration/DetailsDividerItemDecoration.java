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

        for(int i = 1; i <= childCount; i++){
            View child = parent.getChildAt(i - 1);

            View leftLookup = parent.getLayoutManager().findViewByPosition(i - 1).findViewById(R.id.textViewRepsPrimary);
            float left = leftLookup.getLeft() - extraMargin;
            float right;
            Log.d("DDID", String.valueOf(items.get(i)));
            if(items.get(i)){
                right = parent.getLayoutManager().findViewByPosition(i - 1).findViewById(R.id.textViewRepsSecondary).getRight() + extraMargin;
                Log.d("DDID", "Boolean is TRUE");
            } else {
                right = leftLookup.getRight() + extraMargin;
                Log.d("DDID", "Boolean is FALSE");
            }

            float top = child.getBottom();
            float bottom = top + dividerHeight;

            c.drawRect(left, top, right, bottom, paint);
        }
    }

}
