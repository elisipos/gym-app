package com.example.gymapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;

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

        FloatingActionButton newSessionBtn = findViewById(R.id.newSessionBtn);

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
        String todayFormatted = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date(nowMillis));
        sessionDateInput.setText(todayFormatted);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("OK", (d, i) -> { /* add functionality later */ })
                .setNegativeButton("Cancel", (d, i) -> d.dismiss())
                .create();

        dialog.show();
    }
}