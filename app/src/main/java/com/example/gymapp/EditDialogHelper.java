package com.example.gymapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.gymapp.models.Exercise;
import com.example.gymapp.models.Session;
import com.example.gymapp.models.SessionExercise;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditDialogHelper {
    private final Context context;
    private final LayoutInflater inflater;
    private final OnActionCompletedListener listener;

    private final SessionExerciseDataAccess seda;
    private final ExerciseDataAccess eda;
    private final SessionDataAccess sda;


    public EditDialogHelper(Context context, SessionExerciseDataAccess seda, ExerciseDataAccess eda, SessionDataAccess sda, OnActionCompletedListener listener){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.seda = seda;
        this.eda = eda;
        this.sda = sda;
    }

    public interface OnActionCompletedListener {
        void onExerciseUpdated();
    }

    public void showEditDialog(SessionExercise se) {
//      Ex: Changing session exercise details inside of session exercise; "Edit" menu button

        View dialogView = inflater.inflate(R.layout.dialog_existing_exercise, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        EditText inputName = dialogView.findViewById(R.id.inputExerciseName);
        EditText inputReps = dialogView.findViewById(R.id.inputReps);
        EditText inputWeight = dialogView.findViewById(R.id.inputWeight);

        inputName.setHint(se.getName());
        inputReps.setText(String.valueOf(se.getReps()));
        inputWeight.setText(String.valueOf(se.getWeight()));

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
                        se.getId(),
                        se.getSessionId(),
                        se.getExerciseId(),
                        se.getExerciseOrder(),
                        newReps,
                        newWeight
                );
                seda.updateSessionExercise(update);
                dialog.dismiss();
                listener.onExerciseUpdated();
            }
        });
    }

    public void showEditDialog(Exercise e, long sessionId) {
//      Ex: Adding exercise from existing list, appears to input weight and reps

        View dialogView = inflater.inflate(R.layout.dialog_existing_exercise, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        EditText inputName = dialogView.findViewById(R.id.inputExerciseName);
        EditText inputReps = dialogView.findViewById(R.id.inputReps);
        EditText inputWeight = dialogView.findViewById(R.id.inputWeight);

        inputName.setHint(e.getName());

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
                        e.getId(),
                        newExerciseOrder,
                        Integer.parseInt(inputRepsStr),
                        Double.parseDouble(inputWeightStr)
                );
                dialog.dismiss();
                listener.onExerciseUpdated();
            }
        });
    }

    public void showEditDialog(Session s) {
        View dialogView = inflater.inflate(R.layout.dialog_new_session, null);

        EditText sessionNameInput = dialogView.findViewById(R.id.inputSessionName);
        EditText sessionDateInput = dialogView.findViewById(R.id.inputSessionDate);

        SimpleDateFormat sdf = new SimpleDateFormat("M-d-yyyy", Locale.getDefault());
        sessionDateInput.setText(sdf.format(s.getDate()));
        sessionNameInput.setText(s.getName());

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)

                .setPositiveButton("OK", (d, i) -> {
                    long sessionDate = 0;
                    try {
                        sessionDate = Long.parseLong(
                                String.valueOf(
                                        sdf.parse(sessionDateInput.getText().toString()).getTime()
                                )
                        );
                    } catch (ParseException e) {
                        Log.e("MainActivity", "ParseException: "+e);
                    }
                    String sessionName = sessionNameInput.getText().toString();

                    if(sessionName.isEmpty() || sessionName.isBlank()){
                        sessionNameInput.setError("Name cannot be empty");
                    }else if(sessionDate <= 0){
                        sessionDateInput.setError("Date cannot be before Unix Epoch");
                    }else{
                        Session update = new Session(s.getId(), sessionDate, sessionName);
                        sda.updateSession(update);
                    }
                })

                .setNegativeButton("Cancel", (d, i) -> d.dismiss())
                .create();

        dialog.show();
    }

    public void showEditDialog(long sessionId) {
//      Ex: Add new exercise, start from scratch.
        View dialogView = inflater.inflate(R.layout.dialog_new_exercise, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                dialog.dismiss();
                listener.onExerciseUpdated();
            }
        });
    }

    private boolean validateStringInput(String input) {
        if(input.isEmpty()){
            return false;
        }else return !input.isBlank();
    }

}
