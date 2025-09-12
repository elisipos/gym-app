package com.example.gymapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gymapp.R;
import com.example.gymapp.SessionDetailsActivity;
import com.example.gymapp.models.Session;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SessionAdapter extends ArrayAdapter<Session> {
    private Context context;
    private List<Session> sessions;

    private SimpleDateFormat sdf;

    public SessionAdapter(Context context, List<Session> sessions) {
        super(context, 0, sessions);
        this.context = context;
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Reuse old view if possible
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_session, parent, false);
        }

        // Get the current Session
        Session session = sessions.get(position);

        // Bind data
        TextView nameText = convertView.findViewById(R.id.sessionNameText);
        TextView dateText = convertView.findViewById(R.id.sessionDateText);

        sdf = new SimpleDateFormat("M-d-yyyy", Locale.getDefault());

        nameText.setText(String.valueOf(session.getName()));
        dateText.setText(sdf.format(session.getDate()));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SessionDetailsActivity.class);
                i.putExtra("session_id", session.getId());
                context.startActivity(i);
            }
        });

        return convertView;
    }
}
