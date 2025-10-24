package com.example.gymapp.models;

public class Exercise {

    private long id;
    private String name;
    private boolean split;

    public Exercise(long id, String name, boolean split) {
        this.id = id;
        this.name = name;
        this.split = split;
    }

    public long getId() {return id;}

    public String getName() {return name;}
    public boolean getSplit() {return split;}
}
