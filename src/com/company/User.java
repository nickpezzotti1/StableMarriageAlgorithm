package com.company;

/**
 * This class represents the general user. It will always be one of two types:
 * Mentor or Mentee, and therefore cannot be instatiated. This hierarchy is built
 * to be highly maintainable, to allow for changing requirements and specifications
 * regarding the data we collect on the user.
 * The get score class can be move to the subclasses if we desire to have asymmetrical
 * data preferences.
 */
abstract public class User {
    // these values are tuned to adjust importance give to parameters
    protected static final int AGE_WEIGHT = 1;
    protected static final int SEX_WEIGHT = 0;

    protected int id;
    protected int age;
    protected boolean isMale;

    public User(int age, boolean isMale, int id) {
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

    public boolean isMale() {
        return isMale;
    }

    public int getScore(Mentor mentor) {
        int score = 0;

        score += -(Math.abs(age - mentor.getAge()))*AGE_WEIGHT;
        if (isMale = mentor.isMale()) score += SEX_WEIGHT;

        return score;
    }

}
