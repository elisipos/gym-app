package com.example.gymapp;

import android.content.Intent;
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
import com.example.gymapp.models.Exercise;

import java.util.List;

public class ExerciseDetailsActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private ExerciseDataAccess eda;
    private ExerciseAdapter adapter;
    private long selectedExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        eda = new ExerciseDataAccess(db);

        ListView listView = findViewById(R.id.listViewElem);
        TextView titleView = findViewById(R.id.textViewExercises);

        titleView.setText("Existing Exercises (ExerciseDetails)");

        List<Exercise> exerciseList = eda.getExercises();

        adapter = new ExerciseAdapter(this, exerciseList);
        adapter.setOnExerciseClickListener(exerciseId -> {
            Intent data = new Intent();
            data.putExtra("exercise_id", exerciseId);
            setResult(RESULT_OK, data);
            finish();
        });
        listView.setAdapter(adapter);
    }
}