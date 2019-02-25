import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MatchingAlgorithm {
    // these values are tuned to adjust importance give to parameters.
    // The following are the default values if none are specified
    public static final int AGE_WEIGHT = 1;
    public static final int SEX_WEIGHT = 10;
    public static final int HOBBY_WEIGHT = 5;
    public static final int INTEREST_WEIGHT = 5;

    public static int ageWeight = AGE_WEIGHT;
    public static int sexWeight = SEX_WEIGHT;
    public static int hobbyWeight = HOBBY_WEIGHT;
    public static int interestWeight = INTEREST_WEIGHT;

    /**
     * Computation of the stable marriage algorithm. It is an adapted variant of
     * the Gale-Shapely algorithm to allow one woman to accept multiple men (one
     * mentor can accept multiple mentees).
     *
     * TODO ASSUMPTIONS:
     *      - more mentors than mentees
     * @param mentors The men in the algorithm.
     * @param mentees The women.
     */
    private static ArrayList<Mentee> performMatch(ArrayList<Mentor> mentors, ArrayList<Mentee> mentees) {
        // initialize preferences
        HashMap<Mentee, ArrayList<Mentor>> menteePreferences = new HashMap<>();
        HashMap<Mentor, ArrayList<Mentee>> mentorPreferences = new HashMap<>();

        // generate preferences for mentees
        for (Mentee mentee : mentees) {
            ArrayList<Mentor> preferences = new ArrayList<>();
            for (Mentor mentor : mentors) {
                preferences.add(mentor);
            }
            preferences.sort((a, b) -> ((Integer)mentee.getScore(b)).compareTo((mentee.getScore(a))));

            menteePreferences.put(mentee, preferences);
        }

        //generate preferences for mentors
        for (Mentor mentor : mentors) {
            ArrayList<Mentee> preferences = new ArrayList<>();
            for (Mentee mentee : mentees) {
                preferences.add(mentee);
            }
            preferences.sort((a, b) -> ((Integer)mentor.getScore(b)).compareTo((mentor.getScore(a))));

            mentorPreferences.put(mentor, preferences);
        }

        while(existFreeMentees(menteePreferences.keySet())){ // looping until proposers exist
            Mentee proposer = getFirstAvailableMentee(menteePreferences.keySet());

            for (int i = 0; i < menteePreferences.get(proposer).size(); i++) {
                Mentor candidate = menteePreferences.get(proposer).get(i);

                 if (candidate.isNotFull()) {
                    candidate.addMentee(proposer);
                    proposer.setMentor(candidate);
                    break;
                } else if (candidate.prefersToLeastWantedMentee(proposer)) {
                    candidate.getLeastPreferredMentee().setMentor(null); // unengage

                    candidate.removeLeastPreferredMentee();

                    candidate.addMentee(proposer);
                    proposer.setMentor(candidate);
                    break;
                }
            }
        }

        return mentees;
    }

    /**
     * Format a json string containing a list of mentee/mentor pairs
     * @param mentees the list of all mentees to be matched
     * @return a string of the json formatted pairs
     */
    private static String writeToJson(ArrayList<Mentee> mentees) {
        String json = "{ \"assignments\": [ ";
        for (Mentee mentee : mentees) {
            json += "{ ";
            json += "\"mentee_id\": \"" + mentee.getId() + "\", ";
            json += "\"mentor_id\": \"" + mentee.getMentor().getId();
            json += "\"}, ";
        }

        // remove trailing comma
        if (!mentees.isEmpty()) json = json.substring(0, json.length() - 2);
        json += "]}";

        return json;
    }

    /**
     * Get the first available mentee that hasn't been matched to a mentor
     * @param mentees A set of the mentees
     * @return The mentee which hasn't been matched - or null if all mentees are matched
     */
    private static Mentee getFirstAvailableMentee(Set<Mentee> mentees) {
        for (Mentee mentee : mentees) {
            if (!mentee.hasMentor()) return mentee;
        }
        System.out.println("Mentees expired");
        return null;
    }

    /**
     * Check if there is a mentee that does not have a mentor assigned
     * @param mentees A set of the mentees
     * @return true if a mentee is not matched otherwise false
     */
    private static boolean existFreeMentees(Set<Mentee> mentees) {
        for (Mentee mentee : mentees) {
            if (!mentee.hasMentor()) {
                return true;
            }
        }
        return false;
    }

    /**
     * TODO checking data format (mentee limit > 0), make required fields and not required
     * Deserialise a json formatted string containing data for mentees and mentors
     * @param json A json formatted string containing the mentee/mentor data
     * @return a map entry of ArrayLists: key - mentors, value - mentees
     */
    private static Map.Entry<ArrayList<Mentor>, ArrayList<Mentee>> fetchMentorsFromJson(String json) {
        Map jsonRootObject = new Gson().fromJson(json, Map.class);

        // get mentors
        List<Map> mentorsJson = (List) jsonRootObject.get("mentors");
        ArrayList<Mentor> mentors = new ArrayList<>();
        for (Map mentor : mentorsJson) {
            Mentor newMentor = new Mentor(
                    mentor.get("ID").toString(),
                    ((Number) mentor.getOrDefault("menteeLimit", 1)).intValue(),
                    ((Number) mentor.get("age")).intValue(),
                    (Boolean) mentor.get("isMale"),
                    (List<String>) mentor.get("hobbies"),
                    (List<String>) mentor.get("interests"));
            mentors.add(newMentor);
        }

        // get mentees
        List<Map> menteeJson = (List) jsonRootObject.get("mentees");
        ArrayList<Mentee> mentees = new ArrayList<>();
        for (Map mentee : menteeJson) {
            Mentee newMentee = new Mentee(mentee.get("ID").toString(),
                    ((Number) mentee.get("age")).intValue(),
                    (Boolean) mentee.get("isMale"),
                    (List<String>) mentee.get("hobbies"),
                    (List<String>) mentee.get("interests"));
            mentees.add(newMentee);
        }

        // parsing the config options
        if (jsonRootObject.get("configurations") != null) {
            Number sex_importance = (Number) ((Map) jsonRootObject.get("configurations")).get("sex_importance");
            Number age_importance = (Number) ((Map) jsonRootObject.get("configurations")).get("age_importance");
            Number hobbies_importance = (Number) ((Map) jsonRootObject.get("configurations")).get("hobbies_importance");
            Number interests_importance = (Number) ((Map) jsonRootObject.get("configurations")).get("interests_importance");

            if (sex_importance != null) {
                sexWeight = sex_importance.intValue();
            }

            if (age_importance != null) {
                ageWeight = age_importance.intValue();
            }

            if (hobbies_importance != null) {
                hobbyWeight = hobbies_importance.intValue();
            }

            if (interests_importance != null) {
                interestWeight = interests_importance.intValue();
            }
        } else { // set them to default
            ageWeight = 1;
            sexWeight = 10;
            hobbyWeight = 5;
            interestWeight = 5;
        }

        return new AbstractMap.SimpleEntry<>(mentors, mentees);
    }

    /**
     * Match mentors to their mentees based on the json data provided
     * @param json A json formatted list of mentors and mentees with their information
     * @return A json formatted list of mentor-mentee assignments, paired by id
     */
    public static String match(String json) {
        Map.Entry<ArrayList<Mentor>, ArrayList<Mentee>> inputs = fetchMentorsFromJson(json);

        return writeToJson(performMatch(inputs.getKey(), inputs.getValue()));
    }

    /**
     * Match mentors to their mentees based on the data provided in the ArrayLists
     * @param mentors the mentors to be matched
     * @param mentees the mentees to be matched
     * @return A json formatted list of mentor-mentee assignments, paired by id
     */
    public static String match(ArrayList<Mentor> mentors, ArrayList<Mentee> mentees) {
        return writeToJson(performMatch(mentors, mentees));
    }
}
