import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the general user. It will always be one of two types:
 * Mentor or Mentee, and therefore cannot be instatiated. This hierarchy is built
 * to be highly maintainable, to allow for changing requirements and specifications
 * regarding the data we collect on the user.
 * The get score class can be move to the subclasses if we desire to have asymmetrical
 * data preferences.
 */
abstract public class User {
    // these values are tuned to adjust importance give to parameters.
    // The following are the default values if none are specified
    protected static int AGE_WEIGHT = 1;
    protected static int SEX_WEIGHT = 10;
    protected static int HOBBY_WEIGHT = 5;
    protected static int INTEREST_WEIGHT = 5;

    protected String id;
    protected Integer age;
    protected Boolean isMale;
    protected ArrayList<String> hobbies = new ArrayList<>();
    protected ArrayList<String> interests = new ArrayList<>();

    public User(String id, Integer age, Boolean isMale, List<String> hobbies, List<String> interests) {
        this.id = id;
        this.age = age;
        this.isMale = isMale;
        if (hobbies != null) this.hobbies = new ArrayList<>(hobbies);
        if (interests != null) this.interests = new ArrayList<>(interests);
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
        if (age != null) {
            score += -(Math.abs(age - other.getAge())) * AGE_WEIGHT;
        }

        // if they identify as the same gender
        if (isMale != null && isMale == other.isMale()) {
            score += SEX_WEIGHT;
        }

        // if they share common hobbies
        for (String hobby : hobbies) {
            if (other.getHobbies().contains(hobby)) score += HOBBY_WEIGHT;
        }

        // if they share interests
        for (String interest : interests) {
            if (other.getInterests().contains(interest)) score += INTEREST_WEIGHT;
        }

        return score;
    }

}
