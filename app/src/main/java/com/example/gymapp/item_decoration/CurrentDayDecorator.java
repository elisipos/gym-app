package com.example.gymapp.item_decoration;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.example.gymapp.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class CurrentDayDecorator implements DayViewDecorator {

    private CalendarDay today = CalendarDay.today();
    private final Drawable drawable;

    public CurrentDayDecorator(Context context) {
        this.drawable = ContextCompat.getDrawable(context, R.drawable.current_day_background);
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(today);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(drawable);
    }
}
