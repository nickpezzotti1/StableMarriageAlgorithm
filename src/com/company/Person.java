package com.company;

public class Person {
    private static final int AGE_WEIGHT = 1;
    private static final int SEX_WEIGHT = 0;
    private int id;
    private int age;
    private boolean isMale;

    public static int getAgeWeight() {
        return AGE_WEIGHT;
    }

    public static int getSexWeight() {
        return SEX_WEIGHT;
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

    public int getScore(Person other) {
        int score = 0;

        score -= -(Math.abs( this.getAge() - other.getAge())) * this.getAgeWeight();
        if ( this.isMale() == other.isMale()) score += getSexWeight();

        return score;
    }

}