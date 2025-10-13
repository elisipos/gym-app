package com.example.gymapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymapp.adapters.ExerciseAdapter;
import com.example.gymapp.adapters.SessionAdapter;
import com.example.gymapp.models.Exercise;
import com.example.gymapp.models.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;

    private SessionDataAccess sda;
    private ExerciseDataAccess eda;
    private EditDialogHelper editDialogHelper;

    private TabLayout tabLayout;
    private ListView listView;
    private RecyclerView recyclerView;
    private FloatingActionButton newSessionBtn;
    private ExerciseAdapter exerciseAdapter;
    private int tabLayoutTabSelected = 0;

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

        newSessionBtn = findViewById(R.id.newSessionBtn);

        tabLayout = findViewById(R.id.tabLayout);
        listView = findViewById(R.id.listViewElem);
        recyclerView = findViewById(R.id.exerciseRecyclerView);

        TextView welcomeView = findViewById(R.id.welcomeTxt);
        welcomeView.setText("Welcome (MainActivity)");

        List<Session> sessions = sda.getSessions();

        SessionAdapter adapter = new SessionAdapter(this, sessions);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Session clickedSession = (Session) parent.getItemAtPosition(position);
            showSessionOptionsPopup(view, clickedSession);
            return true;
        });

        tabLayout.selectTab(tabLayout.getTabAt(0));
        listView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        newSessionBtn.setVisibility(View.VISIBLE);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0){
                    listView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    newSessionBtn.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    listView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    newSessionBtn.setVisibility(View.GONE);
                    loadExerciseRecycler();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


        newSessionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewSessionDialog();
            }
        });
    }

    private void showNewSessionDialog() {
        editDialogHelper.showEditDialog();
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
                //TODO: Handle edit
                editDialogHelper.showEditDialog(exercise);
                return true;
            } else if (itemId == R.id.action_delete) {
                //TODO: Handle remove
                eda.deleteExercise(exercise.getId());
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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
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