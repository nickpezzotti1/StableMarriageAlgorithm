package com.company;

public class Mentee extends User {

    protected Mentor mentor; // this value is assigned from the algorithm

    public Mentee(int age, boolean isMale, int id) {
        super(age, isMale, id);
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
}
