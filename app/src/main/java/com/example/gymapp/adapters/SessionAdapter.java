package com.example.gymapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymapp.R;
import com.example.gymapp.SessionDetailsActivity;
import com.example.gymapp.models.Session;
import com.example.gymapp.models.SessionExercise;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {
    private Context context;
    private List<Session> sessions;

    private SimpleDateFormat sdf;

    public SessionAdapter(Context context, List<Session> sessions) {
        this.context = context;
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session session = sessions.get(position);

        sdf = new SimpleDateFormat("M-d-yyyy", Locale.getDefault());

        holder.sessionName.setText(String.valueOf(session.getName()));
        holder.sessionDate.setText(sdf.format(session.getDate()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SessionDetailsActivity.class);
                i.putExtra("session_id", session.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView sessionName;
        TextView sessionDate;
        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            sessionName = itemView.findViewById(R.id.sessionNameText);
            sessionDate = itemView.findViewById(R.id.sessionDateText);
        }
    }
}
