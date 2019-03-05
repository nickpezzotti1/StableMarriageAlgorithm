import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the general user. It will always be one of two types:
 * Mentor or Mentee, and therefore cannot be instantiated. This hierarchy is built
 * to be highly maintainable, to allow for changing requirements and specifications
 * regarding the data we collect on the user.
 * The get score class can be move to the subclasses if we desire to have asymmetrical
 * data preferences.
 */
public class User {
    protected String id;
    protected Integer age;
    protected Boolean isMale;
    protected ArrayList<String> hobbies = new ArrayList<>();
    protected ArrayList<String> interests = new ArrayList<>();
    private int partnerLimit; // the number of mentees he can mentor

    public ArrayList<User> getPartners() {
        return partners;
    }

    // A list of all the mentees the mentor is currently matched with
    private ArrayList<User> partners = new ArrayList<>();

    public User(String id, Integer age, Boolean isMale, List<String> hobbies, List<String> interests, int partnerLimit) {
        this.id = id;
        this.age = age;
        this.isMale = isMale;
        if (hobbies != null) this.hobbies = new ArrayList<>(hobbies);
        if (interests != null) this.interests = new ArrayList<>(interests);
        this.partnerLimit = partnerLimit;
    }

    public String getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public boolean isMale() {
        return isMale;
    }

    public ArrayList<String> getHobbies() {
        return hobbies;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public int getScore(User other) {
        int score = 0;

        // if the age is similiar
        if (age != null && other.age != null) {
            score += -(Math.abs(age - other.getAge())) * MatchingAlgorithm.ageWeight;
        }

        // if they identify as the same gender
        if (isMale != null && isMale == other.isMale()) {
            score += MatchingAlgorithm.sexWeight;
        }

        // if they share common hobbies
        for (String hobby : hobbies) {
            if (other.getHobbies().contains(hobby)) score += MatchingAlgorithm.hobbyWeight;
        }

        // if they share interests
        for (String interest : interests) {
            if (other.getInterests().contains(interest)) score += MatchingAlgorithm.interestWeight;
        }

        return score;
    }

    public void addPartner(User partner) {
        partners.add(partner);
    }

    /**
     * Remove the mentee the mentor prefers the least from their
     * currently assigned mentees ("mentees" ArrayList)
     */
    public void removeLeastPreferredPartner() {
        partners.remove(getLeastPreferredPartner());
    }

    /**
     * Find the partner the individual prefers the least from their
     * currently assigned mentees ("mentees" ArrayList)
     * @return the least preferred mentee
     */
    public User getLeastPreferredPartner() {
        User minMentee = partners.get(0);
        for(User m : partners) {
            if (getScore(m) < getScore(minMentee)) {
                minMentee = m;
            }
        }

        return minMentee;
    }

    public boolean prefersToLeastWantedPartner(User other) {
        return getScore(partners.get(0)) < getScore(other);
    }

    /**
     * @return True if the mentor still hasn't reached its capacity of mentees.
     */
    public boolean isFull() {
        return partners.size() >= partnerLimit;
    }

    public int getPartnerLimit() {
        return partnerLimit;
    }

    public void setPartnerLimit(int partnerLimit) {
        this.partnerLimit = partnerLimit;
    }

    public void engage(User newPartner)  {
        partners.add(newPartner);
        newPartner.addPartner(this);
    }

    public void disengage(User oldPartner) {
        partners.remove(oldPartner);
        oldPartner.getPartners().remove(this);
    }
}
