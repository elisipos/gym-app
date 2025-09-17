package com.example.gymapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymapp.adapters.SessionExerciseAdapter;
import com.example.gymapp.models.Exercise;
import com.example.gymapp.models.Session;
import com.example.gymapp.models.SessionExercise;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SessionDetailsActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private SessionDataAccess sda;
    private SessionExerciseDataAccess seda;
    private ExerciseDataAccess eda;
    private SimpleDateFormat sdf;
    private SessionExerciseAdapter adapter;
    private ActivityResultLauncher<Intent> addExerciseLauncher;
    List<SessionExercise> exerciseList;

    private long sessionId;


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

        RecyclerView recyclerView = findViewById(R.id.sessionRecyclerView);

        sessionId = getIntent().getLongExtra("session_id", -1);

        if(sessionId != -1){
            Session session = sda.getSessionById(sessionId);
            exerciseList = seda.getExercisesWithNamesBySessionId(sessionId);

            sessionNameText.setText(session.getName() + " (SessionDetails)");
            sessionDateText.setText(sdf.format(session.getDate()));

            adapter = new SessionExerciseAdapter(this, exerciseList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            ItemTouchHelper.SimpleCallback callback =
                new ItemTouchHelper.SimpleCallback(
                        ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        int fromPos = viewHolder.getAdapterPosition();
                        int toPos = target.getAdapterPosition();

                        Collections.swap(exerciseList, fromPos, toPos);
                        adapter.notifyItemMoved(fromPos, toPos);
                        return true;
                    }

                    @Override
                    public boolean isLongPressDragEnabled() {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        // we’re not doing swipes here
                    }
                };

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
            adapter.setItemTouchHelper(itemTouchHelper);

//            recyclerView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                    showPopupMenu(view, position);
//                    return true;
//                }
//            });
        }

        addExerciseLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if(data != null) {
                            // Do thing with result.
                            long exerciseId = result.getData().getLongExtra("exercise_id", -1);
                            showAddExistingExerciseDialog(eda.getExerciseById(exerciseId));
                        }
                    }
                }
        );

        FloatingActionButton addExerciseBtn = findViewById(R.id.addExerciseBtn);

        addExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExerciseChoiceDialog();
            }
        });
    }

    private void showPopupMenu(View anchorView, int position) {
        PopupMenu popup = new PopupMenu(this, anchorView);
        popup.getMenuInflater().inflate(R.menu.exercise_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            SessionExercise selected = exerciseList.get(position);

            if(itemId == R.id.action_edit) {
                //EDIT
                editExercise(selected);
                return true;
            }else if(itemId == R.id.action_delete) {
                //REMOVE
                removeExercise(selected);
                return true;
            }
            return false;
        });

        popup.setGravity(Gravity.END);
        popup.show();
    }

    private void editExercise(SessionExercise exercise) {
        showUpdateExistingExerciseDialog(exercise);
    }

    private void removeExercise(SessionExercise exercise) {
        // TODO: Refresh exercise order of all exercises in the list after removing an exercise.
        Toast.makeText(this, "Rows affected: " + String.valueOf(seda.deleteSessionExercise(exercise.getId())), Toast.LENGTH_SHORT).show();
        seda.fixExerciseOrders(seda.getExercisesBySessionId(sessionId));
        recreate();
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
            Intent intent = new Intent(this, ExerciseDetailsActivity.class);
            addExerciseLauncher.launch(intent);
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
                Toast.makeText(this, "Successfully added '" + inputNameStr + "'.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                recreate();
            }
        });
    }

    private void showAddExistingExerciseDialog(Exercise exercise) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_existing_exercise, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        EditText inputName = dialogView.findViewById(R.id.inputExerciseName);
        EditText inputReps = dialogView.findViewById(R.id.inputReps);
        EditText inputWeight = dialogView.findViewById(R.id.inputWeight);

        inputName.setHint(exercise.getName());

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        /*                  */
        /* INPUT VALIDATION */
        /*                  */

        positiveButton.setOnClickListener(v -> {
            String inputRepsStr = inputReps.getText().toString();
            String inputWeightStr = inputWeight.getText().toString();

            if(!validateStringInput(inputRepsStr) || Integer.parseInt(inputRepsStr) == 0){
                //FAIL
                inputReps.setError("Reps cannot be 0 or empty.");
            }else
            if(!validateStringInput(inputWeightStr) || Double.parseDouble(inputWeightStr) <= 0){
                //FAIL
                inputWeight.setError("Weight cannot be <= 0 or empty.");
            }else{
                int newExerciseOrder;
                List<SessionExercise> exerciseList = seda.getExercisesBySessionId(sessionId);
                newExerciseOrder = exerciseList.size() + 1;
                seda.addSessionExercise(
                        sessionId,
                        exercise.getId(),
                        newExerciseOrder,
                        Integer.parseInt(inputRepsStr),
                        Double.parseDouble(inputWeightStr)
                );
                dialog.dismiss();
                recreate();
            }
        });
    }

    private void showUpdateExistingExerciseDialog(SessionExercise e) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_existing_exercise, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        EditText inputName = dialogView.findViewById(R.id.inputExerciseName);
        EditText inputReps = dialogView.findViewById(R.id.inputReps);
        EditText inputWeight = dialogView.findViewById(R.id.inputWeight);

        inputName.setHint(e.getName());
        inputReps.setText(String.valueOf(e.getReps()));
        inputWeight.setText(String.valueOf(e.getWeight()));

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        /*                  */
        /* INPUT VALIDATION */
        /*                  */

        positiveButton.setOnClickListener(v -> {
            String inputRepsStr = inputReps.getText().toString();
            String inputWeightStr = inputWeight.getText().toString();

            if(!validateStringInput(inputRepsStr) || Integer.parseInt(inputRepsStr) == 0){
                //FAIL
                inputReps.setError("Reps cannot be 0 or empty.");
            }else
            if(!validateStringInput(inputWeightStr) || Double.parseDouble(inputWeightStr) <= 0){
                //FAIL
                inputWeight.setError("Weight cannot be <= 0 or empty.");
            }else{
                int newReps = Integer.parseInt(inputRepsStr);
                double newWeight = Double.parseDouble(inputWeightStr);

                SessionExercise update = new SessionExercise(
                        e.getId(),
                        e.getSessionId(),
                        e.getExerciseId(),
                        e.getExerciseOrder(),
                        newReps,
                        newWeight
                );
                Toast.makeText(this, String.valueOf(seda.updateSessionExercise(update)), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                recreate();
            }
        });
    }
}