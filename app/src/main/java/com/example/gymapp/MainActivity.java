package com.example.gymapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymapp.adapters.ExerciseAdapter;

import com.example.gymapp.adapters.SessionAdapter;
import com.example.gymapp.helpers.CalendarCallback;
import com.example.gymapp.helpers.CalendarHelper;
import com.example.gymapp.helpers.EditDialogHelper;
import com.example.gymapp.helpers.MyDatabaseHelper;
import com.example.gymapp.item_decoration.SelectedDayDecorator;
import com.example.gymapp.item_decoration.SessionDecorator;
import com.example.gymapp.models.Exercise;
import com.example.gymapp.models.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


public class MainActivity extends AppCompatActivity implements CalendarCallback {

    private MyDatabaseHelper dbHelper;

    private SessionDataAccess sda;
    private ExerciseDataAccess eda;

    private ImageButton settingsBtn;
    private TabLayout tabLayout;
    private RecyclerView exerciseRecyclerView;
    private ExerciseAdapter exerciseAdapter;
    private RecyclerView sessionListRecyclerView;
    private MaterialCalendarView calendarView;
    private CalendarHelper calendarHelper;
    private TextView dateTextView;
    private FloatingActionButton newEntryBtn;

    private EditDialogHelper editDialogHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("UserSettings", MODE_PRIVATE);
        int savedColor = prefs.getInt("background_color", Color.WHITE);


    /* Instantiating lines */

        dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        sda = new SessionDataAccess(db);
        eda = new ExerciseDataAccess(db);

        TextView welcomeView = findViewById(R.id.welcomeTxt);
        ImageButton settingsBtn = findViewById(R.id.settingsButton);
        tabLayout = findViewById(R.id.tabLayout);
        calendarView = findViewById(R.id.calendarView);
        sessionListRecyclerView = findViewById(R.id.sessionListRecyclerView);
        dateTextView = findViewById(R.id.dateTextView);
        calendarHelper = new CalendarHelper(
                calendarView,
                dateTextView,
                sda,
                sessionListRecyclerView,
                this::showSessionOptionsPopup,
                this);
        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView);
        newEntryBtn = findViewById(R.id.newEntryBtn);
        editDialogHelper = new EditDialogHelper(this, null, eda, sda, (res) -> handleDialogHelperUpdate(res));

        welcomeView.setText("Welcome (MainActivity)");

        List<Session> sessions = sda.getSessions();


    /* Settings Button */

        settingsBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        });


    /* Tab Layout */

        tabLayout.selectTab(tabLayout.getTabAt(0));
        calendarView.setVisibility(View.VISIBLE);
        dateTextView.setVisibility(View.VISIBLE);
        sessionListRecyclerView.setVisibility(View.VISIBLE);
        exerciseRecyclerView.setVisibility(View.GONE);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0){
                    calendarView.setVisibility(View.VISIBLE);
                    dateTextView.setVisibility(View.VISIBLE);
                    sessionListRecyclerView.setVisibility(View.VISIBLE);
                    exerciseRecyclerView.setVisibility(View.GONE);
                    calendarHelper.loadSessionRecycler(sessions, calendarHelper.getSelectedDay());
                } else if (position == 1) {
                    calendarView.setVisibility(View.GONE);
                    dateTextView.setVisibility(View.GONE);
                    sessionListRecyclerView.setVisibility(View.GONE);
                    exerciseRecyclerView.setVisibility(View.VISIBLE);
                    loadExerciseRecycler();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


    /* Calendar */

        calendarHelper.setSelectedDay(CalendarDay.today());
        calendarHelper.refresh();

        calendarView.setOnDateChangedListener(((widget, date, selected) -> {
            calendarHelper.setSelectedDay(date);
            calendarHelper.refresh();
        }));


    /* Floating "+" button */

        newEntryBtn.setOnClickListener(v -> {
            int position = tabLayout.getSelectedTabPosition();
            if(position == 0){
                editDialogHelper.showEditDialogSession(calendarHelper);
            }else if(position == 1){
                editDialogHelper.showEditDialogExercise();
            }
        });
    }


/* Helper methods */

    @Override
    public void showSessionOptionsPopup(View anchor, Session session) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.list_item_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit) {
                // handle edit
                editDialogHelper.showEditDialog(session);
                return true;
            } else if (itemId == R.id.action_delete) {
                // handle remove
                sda.deleteSession(session.getId());
                calendarHelper.refresh();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void showExerciseOptionsPopup(View anchor, Exercise exercise) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.list_item_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit) {
                editDialogHelper.showEditDialog(exercise);
                return true;
            } else if (itemId == R.id.action_delete) {
                eda.deleteExercise(exercise.getId());
                loadExerciseRecycler();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void loadExerciseRecycler() {
        eda = new ExerciseDataAccess(dbHelper.getWritableDatabase());
        List<Exercise> exercises = eda.getExercises();

        exerciseAdapter = new ExerciseAdapter(this, exercises);

        exerciseAdapter.setOnExerciseLongClickListener(new ExerciseAdapter.OnExerciseLongClickListener() {
            @Override
            public void onExerciseLongClick(long exerciseId, View view) {
                showExerciseOptionsPopup(view, eda.getExerciseById(exerciseId));
            }
        });

        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        exerciseRecyclerView.addItemDecoration(itemDecoration);
        exerciseRecyclerView.setAdapter(exerciseAdapter);
    }

    private void handleDialogHelperUpdate(boolean isAffectingExercises) {
        if(!isAffectingExercises) {
            calendarHelper.refresh();
        }else{
            loadExerciseRecycler();
        }
    }

}