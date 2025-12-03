package com.example.gymapp.item_decoration;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;

import com.example.gymapp.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

public class SessionDecorator implements DayViewDecorator {
    private HashSet<CalendarDay> dates;
    private final Drawable highlight;

    public SessionDecorator(Collection<CalendarDay> dates, Context context) {
        this.dates = new HashSet<>(dates);
        this.highlight = ContextCompat.getDrawable(context, R.drawable.session_day_background);
    }

    public SessionDecorator(Context context) {
        this.highlight = ContextCompat.getDrawable(context, R.drawable.session_day_background);
    }

    public void setDates(Collection<CalendarDay> dates) {
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day){
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view){
        view.setBackgroundDrawable(highlight);
//        view.addSpan(new ForegroundColorSpan(Color.WHITE));
    }
}
