package com.company;

public class Mentee extends Person {
    private static final int AGE_WEIGHT = 1;
    private static final int SEX_WEIGHT = 0;
    private int id;
    private int age;
    private boolean isMale;

    private Mentor mentor;

    public Mentee(int age, boolean isMale, int id) {
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

    public Mentor getMentor() {
        return mentor;
    }

    public boolean hasMentor() {
        return (mentor == null? false : true);
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    public int getScore(Mentor mentor) {
        int score = 0;

        score -= -(Math.abs(age - mentor.getAge()))*AGE_WEIGHT;
        if (isMale = mentor.isMale()) score += SEX_WEIGHT;

        return score;
    }

    public boolean prefers(Mentor other) {
        return getScore(mentor) < getScore(other);
    }
}
