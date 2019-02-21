import java.util.ArrayList;
import java.util.List;

public class Mentor extends User {

    private int menteeLimit; // the number of mentees he can mentor
    private ArrayList<Mentee> mentees = new ArrayList<>();

    public Mentor(String id, int menteeLimit, Integer age, Boolean isMale, List<String> hobbies, List<String> interests) {
        super(id, age, isMale, hobbies, interests);
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
