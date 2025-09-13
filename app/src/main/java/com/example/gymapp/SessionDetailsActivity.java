package com.example.gymapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymapp.adapters.ExerciseAdapter;
import com.example.gymapp.adapters.SessionExerciseAdapter;
import com.example.gymapp.models.Exercise;
import com.example.gymapp.models.Session;
import com.example.gymapp.models.SessionExercise;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SessionDetailsActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private SessionDataAccess sda;
    private SessionExerciseDataAccess seda;
    private ExerciseDataAccess eda;
    private SimpleDateFormat sdf;
    private SessionExerciseAdapter adapter;

    long sessionId;


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
        eda = new ExerciseDataAccess(db);
        sdf = new SimpleDateFormat("M-d-yyyy", Locale.getDefault());

        TextView sessionNameText = findViewById(R.id.sessionNameText);
        TextView sessionDateText = findViewById(R.id.sessionDateText);

        ListView listView = findViewById(R.id.sessionListView);

        sessionId = getIntent().getLongExtra("session_id", -1);

        if(sessionId != -1){
            Session session = sda.getSessionById(sessionId);
            List<SessionExercise> exerciseList = seda.getExercisesWithNamesBySessionId(sessionId);

            sessionNameText.setText(session.getName());
            sessionDateText.setText(sdf.format(session.getDate()));

            adapter = new SessionExerciseAdapter(this, exerciseList);
            listView.setAdapter(adapter);
        }


        FloatingActionButton addExerciseBtn = findViewById(R.id.addExerciseBtn);

        addExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExerciseChoiceDialog();
            }
        });
    }

    private void showExerciseChoiceDialog() {
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_add_exercise, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(sheetView);

// Handle clicks
        Button btnExisting = sheetView.findViewById(R.id.btnAddExistingExercise);
        Button btnNew = sheetView.findViewById(R.id.btnAddNewExercise);
        Button btnCancel = sheetView.findViewById(R.id.btnCancel);

        btnExisting.setOnClickListener(v -> {
            // Launch add existing flow
            bottomSheetDialog.dismiss();
        });

        btnNew.setOnClickListener(v -> {
            showAddNewExerciseDialog();
            bottomSheetDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private boolean validateStringInput(String input) {
        if(input.isEmpty()){
            return false;
        }else return !input.isBlank();
    }

    private void showAddNewExerciseDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_exercise, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        EditText inputName = dialogView.findViewById(R.id.inputExerciseName);
        EditText inputReps = dialogView.findViewById(R.id.inputReps);
        EditText inputWeight = dialogView.findViewById(R.id.inputWeight);

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        /*                  */
        /* INPUT VALIDATION */
        /*                  */

        positiveButton.setOnClickListener(v -> {
            String inputNameStr = inputName.getText().toString();
            String inputRepsStr = inputReps.getText().toString();
            String inputWeightStr = inputWeight.getText().toString();

            if(!validateStringInput(inputNameStr)){
                //FAIL
                inputName.setError("Name cannot be empty or blank.");
            }else
            if(!validateStringInput(inputRepsStr) || Integer.parseInt(inputRepsStr) == 0){
                //FAIL
                inputReps.setError("Reps cannot be 0 or empty.");
            }else
            if(!validateStringInput(inputWeightStr) || Double.parseDouble(inputWeightStr) <= 0){
                //FAIL
                inputWeight.setError("Weight cannot be <= 0 or empty.");
            }else{
                long newExerciseId = eda.addExercise(inputNameStr);
                int newExerciseOrder;
                List<SessionExercise> exerciseList = seda.getExercisesBySessionId(sessionId);
                newExerciseOrder = exerciseList.size() + 1;
                seda.addSessionExercise(
                        sessionId,
                        newExerciseId,
                        newExerciseOrder,
                        Integer.parseInt(inputRepsStr),
                        Double.parseDouble(inputWeightStr)
                );
                Toast.makeText(this, "Successfully added exercise '" + inputNameStr + "'.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

// Grab handles

//        Button btnSave = dialogView.findViewById(R.id.btnSaveExercise);
//        Button btnCancel = dialogView.findViewById(R.id.btnCancelExercise);
//
//        btnSave.setOnClickListener(v -> {
//            String exerciseName = input.getText().toString().trim();
//            if (!exerciseName.isEmpty()) {
//                // Save to DB, etc.
//                dialog.dismiss();
//            } else {
//                input.setError("Enter a name");
//            }
//        });
//
//        btnCancel.setOnClickListener(v -> dialog.dismiss());

    }
}