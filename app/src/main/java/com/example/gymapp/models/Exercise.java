package com.example.gymapp.models;

public class Exercise {

    private long id;
    private String name;

    public Exercise(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
