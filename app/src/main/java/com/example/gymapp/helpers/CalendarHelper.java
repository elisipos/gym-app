package com.example.gymapp.helpers;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymapp.SessionDataAccess;
import com.example.gymapp.adapters.SessionAdapter;
import com.example.gymapp.item_decoration.CurrentDayDecorator;
import com.example.gymapp.item_decoration.SelectedDayDecorator;
import com.example.gymapp.item_decoration.SessionDecorator;
import com.example.gymapp.models.Session;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class CalendarHelper {
    MaterialCalendarView calendarView;
    TextView dateTextView;
    SessionDataAccess sda;
    RecyclerView sessionListRecyclerView;
    Context context;
    SessionAdapter sessionAdapter;
    private final CalendarCallback callback;

    SelectedDayDecorator selectedDayDecorator;
    SessionDecorator sessionDecorator;
    CurrentDayDecorator currentDayDecorator;

    public AtomicReference<CalendarDay> selectedDay = new AtomicReference<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");

    public CalendarHelper(
            MaterialCalendarView calendarView,
            TextView dateTextView,
            SessionDataAccess sda,
            RecyclerView sessionListRecyclerView,
            CalendarCallback callback,
            Context context) {
        this.calendarView = calendarView;
        this.dateTextView = dateTextView;
        this.sda = sda;
        this.sessionListRecyclerView = sessionListRecyclerView;
        this.callback = callback;
        this.context = context;
        this.selectedDayDecorator = new SelectedDayDecorator(context);
        this.sessionDecorator = new SessionDecorator(context);
        this.currentDayDecorator = new CurrentDayDecorator(context);
    }

    public void setSelectedDay(CalendarDay day) {
        selectedDay.set(day);
    }

    public CalendarDay getSelectedDay() {
        return this.selectedDay.get();
    }
    private List<CalendarDay> getSessionDays() {
        List<CalendarDay> sessionDays = new ArrayList<>();
        List<Session> sessions = sda.getSessions();

        for(Session s : sessions){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(s.getDate());

            sessionDays.add(CalendarDay.from(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ));
        }
        return sessionDays;
    }

    public void refresh() {
        List<Session> sessions = sda.getSessions();
        List<CalendarDay> sessionDays = getSessionDays();

        loadSessionRecycler(sessions, selectedDay.get());

        calendarView.removeDecorators();
        selectedDayDecorator.setDate(selectedDay.get());
        calendarView.addDecorator(currentDayDecorator);
        calendarView.addDecorator(selectedDayDecorator);

        sessionDecorator.setDates(sessionDays);
        calendarView.addDecorator(sessionDecorator);

        dateTextView.setText(dateFormat.format(selectedDay.get().getDate()));
        calendarView.invalidateDecorators();
    }

    public void loadSessionRecycler(List<Session> sessions, CalendarDay date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-d-yyyy");
        List<Session> daySpecificSessions = new ArrayList<>();
        String sFormatted;
        String dFormatted;
        for(Session s : sessions){
            sFormatted = sdf.format( s.getDate() );
            dFormatted = sdf.format( date.getDate().getTime() );
            if(Objects.equals(sFormatted, dFormatted)){
                daySpecificSessions.add(s);
            }
        }
        sessionAdapter = new SessionAdapter(context, daySpecificSessions);
        sessionListRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        sessionAdapter.setSessionLongClickListener((long sessionId, View view) -> {
            callback.showSessionOptionsPopup(view, sda.getSessionById(sessionId));
        });

        sessionListRecyclerView.setAdapter(sessionAdapter);
    }
}
