package com.company;

import java.util.ArrayList;

public class Mentor extends Person {

    private static final int AGE_WEIGHT = 1;
    private static final int SEX_WEIGHT = 0;
    private int id;
    private int age;
    private boolean isMale;
    private int menteeLimit;

    private ArrayList<Mentee> mentees = new ArrayList<>();

    public Mentor(int age, boolean isMale, int id, int menteeLimit) {
        this.age = age;
        this.isMale = isMale;
        this.id = id;
        this.menteeLimit = menteeLimit;
    }

    public int getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public void addMentee(Mentee mentee) {
        mentees.add(mentee);
    }

    public void removeLeastPreferredMentee() {
        mentees.remove(getLeastPreferredMentee());
    }

    public Mentee getLeastPreferredMentee() {
        Mentee minMentee = mentees.get(0);
        for(Mentee m : mentees) {
            if (getScore(m) < getScore(minMentee)) {
                minMentee = m;
            }
        }

        return minMentee;
    }

    public int getScore(Mentee mentee) {
        int score = 0;

        score += -(Math.abs(age - mentee.getAge()))*AGE_WEIGHT;
        if (isMale = mentee.isMale()) score += SEX_WEIGHT;

        return score;
    }

    public boolean prefersToLeast(Mentee other) {
        return getScore(mentees.get(0)) < getScore(other);
    }

    public boolean isNotFull() {
        return mentees.size() < menteeLimit;
    }
}
