package com.example.gymapp.models;

import java.util.ArrayList;

public class SessionExercise {
    long id;
    long sessionId;
    long exerciseId;
    int exerciseOrder;
    int repsPrimary;
    int repsSecondary;
    double weight;
    String name;

    public SessionExercise(long id, long sessionId, long exerciseId, int exerciseOrder, int repsPrimary, int repsSecondary, double weight) {
        this.id = id;
        this.sessionId = sessionId;
        this.exerciseId = exerciseId;
        this.exerciseOrder = exerciseOrder;
        this.repsPrimary = repsPrimary;
        this.repsSecondary = repsSecondary;
        this.weight = weight;
    }

    public SessionExercise(long id, long sessionId, long exerciseId, int exerciseOrder, int repsPrimary, double weight) {
        this.id = id;
        this.sessionId = sessionId;
        this.exerciseId = exerciseId;
        this.exerciseOrder = exerciseOrder;
        this.repsPrimary = repsPrimary;
        this.weight = weight;
    }

    public SessionExercise(long id, long sessionId, long exerciseId, int exerciseOrder, int repsPrimary, int repsSecondary, double weight, String name) {
        this.id = id;
        this.sessionId = sessionId;
        this.exerciseId = exerciseId;
        this.exerciseOrder = exerciseOrder;
        this.repsPrimary = repsPrimary;
        this.repsSecondary = repsSecondary;
        this.weight = weight;
        this.name = name;
    }

    public SessionExercise(long id, long sessionId, long exerciseId, int exerciseOrder, int repsPrimary, double weight, String name) {
        this.id = id;
        this.sessionId = sessionId;
        this.exerciseId = exerciseId;
        this.exerciseOrder = exerciseOrder;
        this.repsPrimary = repsPrimary;
        this.weight = weight;
        this.name = name;
    }

    public long getId() {return id;}

    public long getSessionId() {return sessionId;}

    public long getExerciseId() {return exerciseId;}

    public int getExerciseOrder() {return exerciseOrder;}

    public int getRepsPrimary() {return repsPrimary;}
    public int getRepsSecondary() {return repsSecondary;}
    public ArrayList<Integer> getRepsAll() {
        ArrayList<Integer> allReps = new ArrayList<Integer>();
        allReps.add(repsPrimary);
        allReps.add(repsSecondary);
        return allReps;
    }

    public double getWeight() {return weight;}

    public String getName() {return name;}
}
