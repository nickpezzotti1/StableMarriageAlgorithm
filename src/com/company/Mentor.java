package com.company;

import java.util.ArrayList;

public class Mentor extends Person {
    private int menteeLimit;
    private ArrayList<Mentee> mentees = new ArrayList<>();

    public Mentor(int age, boolean isMale, int id, int menteeLimit) {
        Super.age = age;
        Super.isMale = isMale;
        Super.id = id;
        this.menteeLimit = menteeLimit;
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

    public boolean prefersToLeast(Mentee other) {
        return getScore(mentees.get(0)) < getScore(other);
    }

    public boolean isNotFull() {
        return mentees.size() < menteeLimit;
    }
}
