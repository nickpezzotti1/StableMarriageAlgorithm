import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MatchingAlgorithm {
    // these values are tuned to adjust importance give to parameters.
    // The following are the default values if none are specified
    public static int AGE_WEIGHT = 1;
    public static int SEX_WEIGHT = 10;
    public static int HOBBY_WEIGHT = 5;
    public static int INTEREST_WEIGHT = 5;

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
    public static String match(ArrayList<Mentor> mentors, ArrayList<Mentee> mentees) {
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

        while(existFreeMentees(menteePreferences)){ // looping until proposers exist
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

        return writeToJson(mentees);
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

    private static Mentee getFirstAvailableMentee(Set<Mentee> mentees) {
        for (Mentee mentee : mentees) {
            if (!mentee.hasMentor()) return mentee;
        }
        System.out.println("Mentors expired");
        return null;
    }

    private static boolean existFreeMentees(HashMap<Mentee, ArrayList<Mentor>> menteePreferences) {
        for (Mentee mentee : menteePreferences.keySet()) {
            if (!mentee.hasMentor()) {
                return true;
            }
        }
        return false;
    }

    /**
     * TODO checking data format (mentee limit > 0), make required fields and not required
     * @param json
     * @return
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
                SEX_WEIGHT = sex_importance.intValue();
            }

            if (age_importance != null) {
                AGE_WEIGHT = age_importance.intValue();
            }

            if (hobbies_importance != null) {
                HOBBY_WEIGHT = hobbies_importance.intValue();
            }

            if (interests_importance != null) {
                INTEREST_WEIGHT = interests_importance.intValue();
            }
        } else { // set them to default
            AGE_WEIGHT = 1;
            SEX_WEIGHT = 10;
            HOBBY_WEIGHT = 5;
            INTEREST_WEIGHT = 5;
        }

        return new AbstractMap.SimpleEntry<>(mentors, mentees);
    }

    public static String match(String json) {
        Map.Entry<ArrayList<Mentor>, ArrayList<Mentee>> inputs = fetchMentorsFromJson(json);

        return match(inputs.getKey(), inputs.getValue());
    }
}
