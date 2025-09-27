package com.example.gymapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

import com.example.gymapp.adapters.SessionAdapter;
import com.example.gymapp.models.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;

    private SessionDataAccess sda;
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

        dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        sda = new SessionDataAccess(db);

        editDialogHelper = new EditDialogHelper(this, null, null, sda, () -> recreate());

        FloatingActionButton newSessionBtn = findViewById(R.id.newSessionBtn);
        ListView listView = findViewById(R.id.listViewElem);
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



        newSessionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewSessionDialog();
            }
        });
    }

    private void showNewSessionDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_new_session, null);

        EditText sessionNameInput = dialogView.findViewById(R.id.inputSessionName);
        EditText sessionDateInput = dialogView.findViewById(R.id.inputSessionDate);

        long nowMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("M-d-yyyy", Locale.getDefault());
        String todayFormatted = sdf.format(new Date(nowMillis));
        sessionDateInput.setText(todayFormatted);

        AlertDialog dialog = new AlertDialog.Builder(this)
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
                        Toast.makeText(this, "ParseException: "+e, Toast.LENGTH_LONG).show();
                    }
                    String sessionName = sessionNameInput.getText().toString();

                    if(sessionName.isEmpty() || sessionName.isBlank()){
                        Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    }else if(sessionDate <= 0){
                        Toast.makeText(this, "Date is not valid, M-d-yyyy", Toast.LENGTH_SHORT).show();
                    }else{
                        sda.addSession(sessionDate, sessionName);
                        recreate();
                    }
                })

                .setNegativeButton("Cancel", (d, i) -> d.dismiss())
                .create();

        dialog.show();
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
}