package com.company;

public class Mentee extends Person {
    private Mentor mentor;

    public Mentee(int age, boolean isMale, int id) {
        Super.age = age;
        Super.isMale = isMale;
        Super.id = id;
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
      
        score += -(Math.abs( getAge() - mentor.getAge()))* getAgeWeight();
        if ( isMale() == mentor.isMale()) score += getSexWeight();

        return score;
    }

    public boolean prefers(Mentor other) {
        return getScore(mentor) < getScore(other);
    }
}
