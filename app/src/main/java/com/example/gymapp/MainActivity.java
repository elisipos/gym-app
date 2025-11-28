package com.example.gymapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ListView;
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
import com.example.gymapp.item_decoration.SelectedDayDecorator;
import com.example.gymapp.item_decoration.SessionDecorator;
import com.example.gymapp.models.Exercise;
import com.example.gymapp.models.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;

    private SessionDataAccess sda;
    private ExerciseDataAccess eda;
    private EditDialogHelper editDialogHelper;

    private TabLayout tabLayout;
//    private ListView listView;
    private RecyclerView recyclerView;
    private FloatingActionButton newEntryBtn;
    private ExerciseAdapter exerciseAdapter;
    private MaterialCalendarView calendarView;

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

        dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        sda = new SessionDataAccess(db);
        eda = new ExerciseDataAccess(db);

        editDialogHelper = new EditDialogHelper(this, null, eda, sda, (res) -> handleDialogHelperUpdate(res));

        newEntryBtn = findViewById(R.id.newEntryBtn);

        tabLayout = findViewById(R.id.tabLayout);
//        listView = findViewById(R.id.listViewElem);
        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.exerciseRecyclerView);


        TextView welcomeView = findViewById(R.id.welcomeTxt);
        welcomeView.setText("Welcome (MainActivity)");

        List<Session> sessions = sda.getSessions();

        List<CalendarDay> sessionDays = new ArrayList<>();

        for(Session s : sessions){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(s.getDate());

            sessionDays.add(CalendarDay.from(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ));
        }

        CalendarDay today = CalendarDay.today();

        SelectedDayDecorator decorator = new SelectedDayDecorator(this);
        calendarView.addDecorator(decorator);
        calendarView.setOnDateChangedListener(((widget, date, selected) -> {
            decorator.setDate(date);
            widget.invalidateDecorators();
        }));
        calendarView.addDecorator(new SessionDecorator(sessionDays, this));
        /*
        SessionAdapter adapter = new SessionAdapter(this, sessions);
        listView.setAdapter(adapter);


        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Session clickedSession = (Session) parent.getItemAtPosition(position);
            showSessionOptionsPopup(view, clickedSession);
            return true;
        });
        */

        tabLayout.selectTab(tabLayout.getTabAt(0));
//        listView.setVisibility(View.VISIBLE);
//        calendarView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0){
//                    listView.setVisibility(View.VISIBLE);
//                    calendarView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else if (position == 1) {
//                    listView.setVisibility(View.GONE);
//                    calendarView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    loadExerciseRecycler();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


        newEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = tabLayout.getSelectedTabPosition();
                if(position == 0){
                    editDialogHelper.showEditDialogSession();
                }else if(position == 1){
                    editDialogHelper.showEditDialogExercise();
                }
            }
        });
    }

    private void showSessionOptionsPopup(View anchor, Session session) {
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
                recreate();
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

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(exerciseAdapter);
    }

    private void handleDialogHelperUpdate(boolean isAffectingExercises) {
        if(!isAffectingExercises) {
            recreate();
        }else{
            loadExerciseRecycler();
        }
    }
}