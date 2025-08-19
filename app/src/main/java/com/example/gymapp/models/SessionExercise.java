package com.example.gymapp.models;

public class SessionExercise {
    long id;
    long sessionId;
    long exerciseId;
    int exerciseOrder;
    int reps;
    double weight;

    public SessionExercise(long id, long sessionId, long exerciseId, int exerciseOrder, int reps, double weight) {
        this.id = id;
        this.sessionId = sessionId;
        this.exerciseId = exerciseId;
        this.exerciseOrder = exerciseOrder;
        this.reps = reps;
        this.weight = weight;
    }

    public long getId() {return id;}

    public long getSessionId() {return sessionId;}

    public long getExerciseId() {return exerciseId;}

    public int getExerciseOrder() {return exerciseOrder;}

    public int getReps() {return reps;}

    public double getWeight() {return weight;}
}
