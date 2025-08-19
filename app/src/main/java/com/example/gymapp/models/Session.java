package com.example.gymapp.models;

public class Session {

    private long id;
    private long date;
    private String name;

    public Session(long id, long date, String name) {
        this.id = id;
        this.date = date;
        this.name = name;
    }

    public long getId() {return id; }
    public long getDate() {return date;}

    public String getName() {return name;}
}
