package com.example.gymapp.helpers;

import android.view.View;

import com.example.gymapp.models.Session;

public interface CalendarCallback {
    void showSessionOptionsPopup(View anchor, Session session);
}
