package com.example.gymapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymapp.item_decoration.DetailsDividerItemDecoration;
import com.example.gymapp.item_decoration.GroupDividerItemDecoration;
import com.example.gymapp.adapters.SessionExerciseAdapter;
import com.example.gymapp.models.Exercise;
import com.example.gymapp.models.Session;
import com.example.gymapp.models.SessionExercise;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    List<SessionExercise> sessionExerciseList;
    HashMap<SessionExercise, Boolean> sessionExerciseMap;
    List<Exercise> exerciseList;
    private EditDialogHelper editDialogHelper;

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

        editDialogHelper = new EditDialogHelper(this, seda, eda, sda, (res) -> recreate());

        TextView sessionNameText = findViewById(R.id.sessionNameText);
        TextView sessionDateText = findViewById(R.id.sessionDateText);

        RecyclerView recyclerView = findViewById(R.id.sessionRecyclerView);

        sessionId = getIntent().getLongExtra("session_id", -1);

        if(sessionId != -1){
            Session session = sda.getSessionById(sessionId);
            sessionExerciseList = seda.getExercisesWithNamesBySessionId(sessionId);
            sessionExerciseMap = seda.getExerciseSplitMapBySessionId(sessionId);
            exerciseList = new ArrayList<>();
            for(int i = 0; i < sessionExerciseList.size(); i++){
                exerciseList.add(
                        eda.getExerciseById(
                                sessionExerciseList.get(i).getExerciseId()
                        )
                );
            }

            sessionNameText.setText(session.getName() + " (SessionDetails)");
            sessionDateText.setText(sdf.format(session.getDate()));

            adapter = new SessionExerciseAdapter(this, sessionExerciseList, exerciseList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            int dividerColor = getResources().getColor(android.R.color.darker_gray);
            int dividerHeight = 3;

            recyclerView.addItemDecoration(new GroupDividerItemDecoration(
                    this,
                    dividerColor,
                    dividerHeight,
                    40,
                    sessionExerciseList
            ));

            HashMap<Integer, Boolean> seOrderMap = new HashMap<>();
            for(SessionExercise se : sessionExerciseMap.keySet()){
                seOrderMap.put(se.getExerciseOrder(), sessionExerciseMap.get(se));
            }

            Log.d("SDAC", seOrderMap.toString());

            recyclerView.addItemDecoration(new DetailsDividerItemDecoration(
                    this,
                    dividerColor,
                    2,
                    20,
                    seOrderMap
            ));

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

                        Collections.swap(sessionExerciseList, fromPos, toPos);
                        seda.fixExerciseOrders(sessionExerciseList);
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
            adapter.setOnExerciseLongClickListener(new SessionExerciseAdapter.OnExerciseLongClickListener() {
                @Override
                public void onExerciseLongClick(View view, int position) {
                    showPopupMenu(view, position);
                }
            });
        }

        addExerciseLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if(data != null) {
                            // Do thing with result.
                            long exerciseId = result.getData().getLongExtra("exercise_id", -1);
                            editDialogHelper.showEditDialog(eda.getExerciseById(exerciseId), sessionId);
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
        popup.getMenuInflater().inflate(R.menu.list_item_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            SessionExercise selected = sessionExerciseList.get(position);

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

    private void editExercise(SessionExercise se) {
        editDialogHelper.showEditDialog(se);
    }

    private void removeExercise(SessionExercise exercise) {
        seda.deleteSessionExercise(exercise.getId());
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
            editDialogHelper.showEditDialog(sessionId);
            bottomSheetDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

}