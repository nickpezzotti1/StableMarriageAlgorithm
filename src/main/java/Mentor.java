import java.util.ArrayList;

public class Mentor extends User {

    private int menteeLimit; // the number of mentees he can mentor

    // A list of all the mentees the mentor is currently matched with
    private ArrayList<Mentee> mentees = new ArrayList<>();

    public Mentor(int age, boolean isMale, String id, int menteeLimit) {
        super(age, isMale, id);
        this.menteeLimit = menteeLimit;
    }

    public void addMentee(Mentee mentee) {
        mentees.add(mentee);
    }

    /**
     * Remove the mentee the mentor prefers the least from their
     * currently assigned mentees ("mentees" ArrayList)
     */
    public void removeLeastPreferredMentee() {
        mentees.remove(getLeastPreferredMentee());
    }

    /**
     * Find the mentee the mentor prefers the least from their
     * currently assigned mentees ("mentees" ArrayList)
     * @return the least preferred mentee
     */
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

    /**
     * Check if the mentor prefers another mentee to their least
     * preferred currently assigned mentees ("mentees" ArrayList)
     * @param other The other/new mentee to compare to
     * @return true if the mentee prefers the other/new mentee
     */
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
