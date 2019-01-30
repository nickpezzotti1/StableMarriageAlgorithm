package com.company;

import java.util.ArrayList;

public class Mentor extends User {

    private int menteeLimit; // the number of mentees he can mentor
    private ArrayList<Mentee> mentees = new ArrayList<>();

    public Mentor(int age, boolean isMale, int id, int menteeLimit) {
        super(age, isMale, id);
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

    public int getScore(Mentee mentee) {
        int score = 0;

        score += -(Math.abs(age - mentee.getAge()))*AGE_WEIGHT;
        if (isMale = mentee.isMale()) score += SEX_WEIGHT;

        return score;
    }

    public boolean prefersToLeastWantedMentee(Mentee other) {
        return getScore(mentees.get(0)) < getScore(other);
    }

    /**
     * @return True if the mentor still hasn't reached its capacity of mentees.
     */
    public boolean isNotFull() {
        return mentees.size() < menteeLimit;
    }
}
