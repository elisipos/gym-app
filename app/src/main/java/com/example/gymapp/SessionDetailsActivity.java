package com.example.gymapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymapp.adapters.ExerciseAdapter;
import com.example.gymapp.adapters.SessionExerciseAdapter;
import com.example.gymapp.models.Exercise;
import com.example.gymapp.models.Session;
import com.example.gymapp.models.SessionExercise;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SessionDetailsActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private SessionDataAccess sda;
    private SessionExerciseDataAccess seda;
    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_session_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        sda = new SessionDataAccess(db);
        seda = new SessionExerciseDataAccess(db);
        sdf = new SimpleDateFormat("M-d-yyyy", Locale.getDefault());

        TextView sessionNameText = findViewById(R.id.sessionNameText);
        TextView sessionDateText = findViewById(R.id.sessionDateText);

        ListView listView = findViewById(R.id.sessionListView);

        long sessionId = getIntent().getLongExtra("session_id", -1);
        if(sessionId != -1){
            Session session = sda.getSessionById(sessionId);
            List<SessionExercise> exerciseList = seda.getExercisesBySessionId(sessionId);

            sessionNameText.setText(session.getName());
            sessionDateText.setText(sdf.format(session.getDate()));

            SessionExerciseAdapter adapter = new SessionExerciseAdapter(this, exerciseList);
        }
    }
}