package com.example.gymapp;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.gymapp.models.Exercise;
import com.example.gymapp.models.Session;
import com.example.gymapp.models.SessionExercise;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

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
        void onExerciseUpdated(boolean isAffectingExercises);
    }

    public class LayoutSetter {
        TextView primary;
        TextView secondary;
        EditText primaryEdit;
        EditText secondaryEdit;
        CheckBox checkBox;
        public LayoutSetter(View v){
            this.primary = v.findViewById(R.id.txtViewRepsPrimary);
            this.secondary = v.findViewById(R.id.txtViewRepsSecondary);
            this.primaryEdit = v.findViewById(R.id.inputRepsPrimary);
            this.secondaryEdit = v.findViewById(R.id.inputRepsSecondary);
            this.checkBox = v.findViewById(R.id.checkBox);
        }
        public void setTxtLayoutTo(boolean split) {

            LinearLayout.LayoutParams repsPrimaryTxtViewLayoutParams = (LinearLayout.LayoutParams) primary.getLayoutParams();
            repsPrimaryTxtViewLayoutParams.weight = split ? 50 : 100;
            primary.setLayoutParams(repsPrimaryTxtViewLayoutParams);

            LinearLayout.LayoutParams repsSecondaryTxtViewLayoutParams = (LinearLayout.LayoutParams) secondary.getLayoutParams();
            repsSecondaryTxtViewLayoutParams.weight = split ? 50 : 0;
            secondary.setLayoutParams(repsSecondaryTxtViewLayoutParams);

            if(split){
                secondary.setVisibility(TextView.VISIBLE);

            }else{
                secondary.setVisibility(TextView.GONE);

            }
        }
        public void setEditLayoutTo(boolean split) {

            LinearLayout.LayoutParams inputRepsPrimaryLayoutParams = (LinearLayout.LayoutParams) primaryEdit.getLayoutParams();
            inputRepsPrimaryLayoutParams.weight = split ? 50 : 100;
            primaryEdit.setLayoutParams(inputRepsPrimaryLayoutParams);

            LinearLayout.LayoutParams inputRepsSecondaryLayoutParams = (LinearLayout.LayoutParams) secondaryEdit.getLayoutParams();
            inputRepsSecondaryLayoutParams.weight = split ? 50 : 0;
            secondaryEdit.setLayoutParams(inputRepsSecondaryLayoutParams);

            if(split){
                secondaryEdit.setVisibility(EditText.VISIBLE);
//                int repsSecondary = se.getRepsSecondary();
//                secondary.setText(repsSecondary > 0 ? String.valueOf(repsSecondary) : "");
            }else{
                secondaryEdit.setVisibility(EditText.GONE);
                secondaryEdit.setText("");
            }
        }
        public void refreshLayout(boolean[] split){
            if(split[0]) {
                checkBox.setChecked(true);
                this.setTxtLayoutTo(true);
                this.setEditLayoutTo(true);
                this.primary.setText("Left Side Reps");
                this.secondary.setText("Right Side Reps");
            }else{
                checkBox.setChecked(false);
                this.setTxtLayoutTo(false);
                this.setEditLayoutTo(false);
                this.primary.setText("Reps");
            }
        }
    }

    public void showEditDialog(SessionExercise se) {
//      Ex: Changing session exercise details inside of session exercise; "Edit" menu button

        View dialogView = inflater.inflate(R.layout.dialog_existing_exercise, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        EditText inputName = dialogView.findViewById(R.id.inputExerciseName);

        CheckBox checkBox = dialogView.findViewById(R.id.checkBox);

        EditText inputRepsPrimary = dialogView.findViewById(R.id.inputRepsPrimary);
        EditText inputRepsSecondary = dialogView.findViewById(R.id.inputRepsSecondary);

        EditText inputWeight = dialogView.findViewById(R.id.inputWeight);
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);

        dialogTitle.setText("Edit Exercise Details");
        inputName.setHint(se.getName());
        inputRepsPrimary.setText(String.valueOf(se.getRepsPrimary()));
        inputRepsSecondary.setText(String.valueOf(se.getRepsSecondary()));
        inputWeight.setText(String.valueOf(se.getWeight()));

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        Exercise e = eda.getExerciseById(se.getExerciseId());

        final boolean[] split = {e.getSplit()};

        LayoutSetter ls = new LayoutSetter(dialogView);

        ls.refreshLayout(split);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                split[0] = !split[0];
                ls.refreshLayout(split);
            }
        });

        /*                  */
        /* INPUT VALIDATION */
        /*                  */

        positiveButton.setOnClickListener(v -> {
            String inputRepsPrimaryStr = inputRepsPrimary.getText().toString();
            String inputRepsSecondaryStr = inputRepsSecondary.getText().toString();
            String inputWeightStr = inputWeight.getText().toString();

            boolean hasError = false;

            if(!validateStringInput(inputRepsPrimaryStr) || Integer.parseInt(inputRepsPrimaryStr) == 0){
                //FAIL
                inputRepsPrimary.setError("Reps cannot be 0 or empty.");
                hasError = true;
            }

            if(split[0]){
                if(!validateStringInput(inputRepsSecondaryStr) || Integer.parseInt(inputRepsSecondaryStr) == 0){
                    //FAIL
                    inputRepsSecondary.setError("Reps cannot be 0 or empty");
                    hasError = true;
                }
            }

            if(!validateStringInput(inputWeightStr) || Double.parseDouble(inputWeightStr) <= 0){
                //FAIL
                inputWeight.setError("Weight cannot be <= 0 or empty.");
                hasError = true;
            }

            if(!hasError){
                int newRepsPrimary = Integer.parseInt(inputRepsPrimaryStr);
                double newWeight = Double.parseDouble(inputWeightStr);
                if (split[0]) {
                    int newRepsSecondary = Integer.parseInt(inputRepsSecondaryStr);

                    SessionExercise update = new SessionExercise(
                            se.getId(),
                            se.getSessionId(),
                            se.getExerciseId(),
                            se.getExerciseOrder(),
                            newRepsPrimary,
                            newRepsSecondary,
                            newWeight
                    );
                    seda.updateSessionExercise(update);

                } else {

                    SessionExercise update = new SessionExercise(
                            se.getId(),
                            se.getSessionId(),
                            se.getExerciseId(),
                            se.getExerciseOrder(),
                            newRepsPrimary,
                            newWeight
                    );
                    seda.updateSessionExercise(update);

                }

                if(split[0] != e.getSplit()){
                    Exercise update = new Exercise(
                            e.getId(),
                            e.getName(),
                            split[0]
                    );
                    eda.updateExercise(update);

                }
                dialog.dismiss();
                listener.onExerciseUpdated(false);
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
        CheckBox checkBox = dialogView.findViewById(R.id.checkBox);
        TextView repsPrimaryTextView = dialogView.findViewById(R.id.txtViewRepsPrimary);
        EditText inputRepsPrimary = dialogView.findViewById(R.id.inputRepsPrimary);
        TextView repsSecondaryTextView = dialogView.findViewById(R.id.txtViewRepsSecondary);
        EditText inputRepsSecondary = dialogView.findViewById(R.id.inputRepsSecondary);
        EditText inputWeight = dialogView.findViewById(R.id.inputWeight);

        inputName.setHint(e.getName());

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        LayoutSetter ls = new LayoutSetter(dialogView);

        final boolean[] split = {e.getSplit()};

        ls.refreshLayout(split);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                split[0] = !split[0];
                ls.refreshLayout(split);
            }
        });

        /*                  */
        /* INPUT VALIDATION */
        /*                  */

        positiveButton.setOnClickListener(v -> {
            String inputRepsPrimaryStr = inputRepsPrimary.getText().toString();
            String inputRepsSecondaryStr = inputRepsSecondary.getText().toString();
            String inputWeightStr = inputWeight.getText().toString();

            boolean hasError = false;

            if(!validateStringInput(inputRepsPrimaryStr) || Integer.parseInt(inputRepsPrimaryStr) == 0){
                //FAIL
                inputRepsPrimary.setError("Reps cannot be 0 or empty.");
                hasError = true;
            }

            if(split[0]){
                if(!validateStringInput(inputRepsSecondaryStr) || Integer.parseInt(inputRepsSecondaryStr) == 0){
                    //FAIL
                    inputRepsSecondary.setError("Reps cannot be 0 or empty.");
                    hasError = true;
                }
            }

            if(!validateStringInput(inputWeightStr) || Double.parseDouble(inputWeightStr) <= 0){
                //FAIL
                inputWeight.setError("Weight cannot be <= 0 or empty.");
                hasError = true;
            }

            if(!hasError){
                int newExerciseOrder;
                List<SessionExercise> exerciseList = seda.getExercisesBySessionId(sessionId);
                newExerciseOrder = exerciseList.size() + 1;
                if(split[0]){
                    seda.addSessionExercise(
                            sessionId,
                            e.getId(),
                            newExerciseOrder,
                            Integer.parseInt(inputRepsPrimaryStr),
                            Integer.parseInt(inputRepsSecondaryStr),
                            Double.parseDouble(inputWeightStr)
                    );
                }else{
                    seda.addSessionExercise(
                            sessionId,
                            e.getId(),
                            newExerciseOrder,
                            Integer.parseInt(inputRepsPrimaryStr),
                            Double.parseDouble(inputWeightStr)
                    );
                }

                if(split[0] != e.getSplit()){
                    Exercise update = new Exercise(
                            e.getId(),
                            e.getName(),
                            split[0]
                    );
                    eda.updateExercise(update);
                }
                dialog.dismiss();
                listener.onExerciseUpdated(false);
            }
        });
    }

    public void showEditDialog(Session s) {
//      Ex: Editing session from Long Press Menu.
        View dialogNewSessionView = inflater.inflate(R.layout.dialog_new_session, null);

        EditText sessionNameInput = dialogNewSessionView.findViewById(R.id.inputSessionName);
        EditText sessionDateInput = dialogNewSessionView.findViewById(R.id.inputSessionDate);
        TextView dialogTitle = dialogNewSessionView.findViewById(R.id.dialogTitle);

        SimpleDateFormat sdf = new SimpleDateFormat("M-d-yyyy", Locale.getDefault());

        dialogTitle.setText("Edit session?");
        sessionDateInput.setText(sdf.format(s.getDate()));
        sessionNameInput.setText(s.getName());

        AtomicLong sessionDate = new AtomicLong();

        sessionDate.set(s.getDate());

        sessionDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarView(sessionDate, sdf, sessionDateInput);
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogNewSessionView)

                .setPositiveButton("OK", null) // Overridden once the dialog appears below.
                .setNegativeButton("Cancel", (d, i) -> d.dismiss())
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String sessionName = sessionNameInput.getText().toString();

                        if(!validateStringInput(sessionName)){
                            sessionNameInput.setError("Name cannot be empty");
                        }else{
                            Session update = new Session(s.getId(), sessionDate.get(), sessionName);
                            sda.updateSession(update);
                            dialog.dismiss();
                            listener.onExerciseUpdated(false);
                        }
                    }
                });
            }
        });

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

        CheckBox checkBox = dialogView.findViewById(R.id.checkBox);

        EditText inputRepsPrimary = dialogView.findViewById(R.id.inputRepsPrimary);
        EditText inputRepsSecondary = dialogView.findViewById(R.id.inputRepsSecondary);

        EditText inputWeight = dialogView.findViewById(R.id.inputWeight);

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        LayoutSetter ls = new LayoutSetter(dialogView);
        boolean[] split = {false};
        ls.refreshLayout(split);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                split[0] = !split[0];
                ls.refreshLayout(split);
            }
        });

        /*                  */
        /* INPUT VALIDATION */
        /*                  */

        positiveButton.setOnClickListener(v -> {
            String inputNameStr = inputName.getText().toString();
            String inputRepsPrimaryStr = inputRepsPrimary.getText().toString();
            String inputRepsSecondaryStr = inputRepsSecondary.getText().toString();
            String inputWeightStr = inputWeight.getText().toString();

            boolean hasError = false;

            if(!validateStringInput(inputNameStr)){
                //FAIL
                inputName.setError("Name cannot be empty or blank.");
                hasError = true;
            }

            if(!validateStringInput(inputRepsPrimaryStr) || Integer.parseInt(inputRepsPrimaryStr) == 0){
                //FAIL
                inputRepsPrimary.setError("Reps cannot be 0 or empty.");
                hasError = true;
            }

            if(split[0]){
                if(!validateStringInput(inputRepsSecondaryStr) || Integer.parseInt(inputRepsSecondaryStr) == 0){
                    //FAIL
                    inputRepsSecondary.setError("Reps cannot be 0 or empty.");
                    hasError = true;
                }
            }

            if(!validateStringInput(inputWeightStr) || Double.parseDouble(inputWeightStr) <= 0){
                //FAIL
                inputWeight.setError("Weight cannot be <= 0 or empty.");
                hasError = true;
            }

            if(!hasError){
                long newExerciseId = eda.addExercise(inputNameStr, checkBox.isChecked());

                int newExerciseOrder;
                List<SessionExercise> exerciseList = seda.getExercisesBySessionId(sessionId);
                newExerciseOrder = exerciseList.size() + 1;

                if(checkBox.isChecked()){
                    seda.addSessionExercise(
                            sessionId,
                            newExerciseId,
                            newExerciseOrder,
                            Integer.parseInt(inputRepsPrimaryStr),
                            Integer.parseInt(inputRepsSecondaryStr),
                            Double.parseDouble(inputWeightStr)
                    );
                }else{
                    seda.addSessionExercise(
                            sessionId,
                            newExerciseId,
                            newExerciseOrder,
                            Integer.parseInt(inputRepsPrimaryStr),
                            Double.parseDouble(inputWeightStr)
                    );
                }

                dialog.dismiss();
                listener.onExerciseUpdated(false);
            }
        });
    }

    public void showEditDialog(Exercise e) {
        View dialogView = inflater.inflate(R.layout.dialog_new_exercise_raw, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        EditText inputExerciseName = dialogView.findViewById(R.id.inputExerciseName);
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);

        inputExerciseName.setText(e.getName());
        dialogTitle.setText("Edit exercise?");

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(v -> {
            String inputExerciseStr = String.valueOf(inputExerciseName.getText());
            if(!validateStringInput(inputExerciseStr)) {
                // FAIL
                inputExerciseName.setError("Name cannot be empty.");
            } else {
                Exercise newExercise = new Exercise(e.getId(), inputExerciseStr, false); //TODO: CHANGE ONCE TOGGLE IS THERE
                eda.updateExercise(newExercise);
                dialog.dismiss();
                listener.onExerciseUpdated(true);
            }
        });
    }

    public void showEditDialogSession() {
        View dialogView = inflater.inflate(R.layout.dialog_new_session, null);

        EditText sessionNameInput = dialogView.findViewById(R.id.inputSessionName);
        EditText sessionDateInput = dialogView.findViewById(R.id.inputSessionDate);

        long nowMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("M-d-yyyy", Locale.getDefault());
        String todayFormatted = sdf.format(new Date(nowMillis));
        sessionDateInput.setText(todayFormatted);

        AtomicLong sessionDate = new AtomicLong(new Date().getTime());

        sessionDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarView(sessionDate, sdf, sessionDateInput);
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", (d, i) -> d.dismiss())
        .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String sessionName = sessionNameInput.getText().toString();

                        if(!validateStringInput(sessionName)){
                            sessionNameInput.setError("Name cannot be empty.");
                        }else{
                            sda.addSession(Long.parseLong(String.valueOf(sessionDate)), sessionName);
                            dialog.dismiss();
                            listener.onExerciseUpdated(false);
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    public void showEditDialogExercise() {
        View dialogView = inflater.inflate(R.layout.dialog_new_exercise_raw, null);

        EditText exerciseNameInput = dialogView.findViewById(R.id.inputExerciseName);
        CheckBox checkBox = dialogView.findViewById(R.id.checkBox);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", (d, i) -> d.dismiss())
        .create();

        boolean[] split = {false};

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                split[0] = !split[0];
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String exerciseName = String.valueOf(exerciseNameInput.getText());

                        if(!validateStringInput(exerciseName)){
                            //FAIL
                            exerciseNameInput.setError("Name cannot be empty.");
                        }else{
                            eda.addExercise(exerciseName, split[0]);
                            dialog.dismiss();
                            listener.onExerciseUpdated(true);
                        }
                    }
                });
            }
        });
        dialog.show();
    }
    private boolean validateStringInput(String input) {
        if(input.isEmpty()){
            return false;
        }else return !input.isBlank();
    }

    private void showCalendarView(AtomicLong sessionDate, SimpleDateFormat sdf, EditText sessionDateInput) {
        View dialogCalendarView = inflater.inflate(R.layout.dialog_calendar, null);
        final CalendarView calendar = dialogCalendarView.findViewById(R.id.calendarView);

        calendar.setDate(sessionDate.get(), false, true);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth, 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                sessionDate.set(cal.getTimeInMillis());
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogCalendarView)
                .setPositiveButton("OK", (d, i) -> {
                    sessionDateInput.setText(sdf.format(new Date(sessionDate.get())));
                })
                .setNegativeButton("Cancel", (d, i) -> d.dismiss())
                .create();
        dialog.show();
    }

}
