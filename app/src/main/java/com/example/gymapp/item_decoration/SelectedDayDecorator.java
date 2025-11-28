package com.example.gymapp.item_decoration;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.example.gymapp.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class SelectedDayDecorator implements DayViewDecorator {
    private CalendarDay selectedDate;
    private final Drawable drawable;

    public SelectedDayDecorator(Context context) {
        this.drawable = ContextCompat.getDrawable(context, R.drawable.selection_day_background);
    }

    public void setDate(CalendarDay date){
        this.selectedDate = date;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return selectedDate != null && day.equals(selectedDate);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }
}
