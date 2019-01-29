package com.company;

public class Mentor {
    private static final int AGE_WEIGHT = 1;
    private static final int SEX_WEIGHT = 3;
    private int id;
    private int age;
    private boolean isMale;
    private int menteeLimit;

    private Mentee mentee;

    public Mentor(int age, boolean isMale, int id) {
        this.age = age;
        this.isMale = isMale;
        this.id = id;
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

    public boolean hasMentee() {
        return (mentee == null? false : true);
    }

    public void setMentee(Mentee mentee) {
        this.mentee = mentee;
    }

    public Mentee getMentee() {
        return mentee;
    }

    public int getScore(Mentee mentee) {
        int score = 0;

        score -= -(Math.abs(age - mentee.getAge()))*AGE_WEIGHT;
        if (isMale = mentee.isMale()) score += SEX_WEIGHT;

        return score;
    }

    public boolean prefers(Mentee other) {
        return getScore(mentee) < getScore(other);
    }
}
