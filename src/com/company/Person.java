package com.company;

public class Person {
    public static final int AGE_WEIGHT = 1;
    public static final int SEX_WEIGHT = 0;

    private int id;
    private int age;
    private boolean isMale;

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

    public int getScore(Person other) {
        int score = 0;

        score += -(Math.abs( age - other.getAge())) * AGE_WEIGHT;
        if ( isMale == other.isMale()) score += SEX_WEIGHT;

        return score;
    }

}
