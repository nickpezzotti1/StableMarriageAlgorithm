import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MatchingAlgorithm {

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
    public static ArrayList<Mentee> match(ArrayList<Mentor> mentors, ArrayList<Mentee> mentees) {
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
     * @param mentees The list of mentees
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
     * TODO: abstract menteePreferences parameter (into keyset?)
     * Check if there is a mentee that does not have a mentor assigned
     * @param menteePreferences A hashmap containing mentees and a list of their preferred mentors
     * @return true if a mentee is not matched
     */
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
            Mentor newMentor = new Mentor(((Number) mentor.get("age")).intValue(),
                    (boolean) mentor.getOrDefault("isMale", false),
                    mentor.get("ID").toString(),
                    ((Number) mentor.getOrDefault("menteeLimit", 1)).intValue());
            mentors.add(newMentor);
        }

        // get mentees
        List<Map> menteeJson = (List) jsonRootObject.get("mentees");
        ArrayList<Mentee> mentees = new ArrayList<>();
        for (Map mentee : menteeJson) {
            Mentee newMentee = new Mentee(((Number) mentee.get("age")).intValue(),
                    (boolean) mentee.get("isMale"),
                    (mentee.get("ID").toString()));
            mentees.add(newMentee);
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

        return writeToJson(match(inputs.getKey(), inputs.getValue()));
    }

    public static void main(String[] args) {
        String jsonRequestBody = null;

        try {
            jsonRequestBody = String.join("\n", Files.readAllLines(Paths.get("src/res/requestExample.txt"))) ;
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(match(jsonRequestBody));
    }
}
