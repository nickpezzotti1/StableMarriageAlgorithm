import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MatchingAlgorithm {
    // these values are tuned to adjust importance give to parameters.
    // The following are the default values if none are specified
    private static final int AGE_WEIGHT = 1;
    private static final int SEX_WEIGHT = 10;
    private static final int HOBBY_WEIGHT = 5;
    private static final int INTEREST_WEIGHT = 5;

    public static int ageWeight = AGE_WEIGHT;
    public static int sexWeight = SEX_WEIGHT;
    public static int hobbyWeight = HOBBY_WEIGHT;
    public static int interestWeight = INTEREST_WEIGHT;

    /**
     * Match mentors to their mentees based on the json data provided via the API
     *
     * @param json A json formatted list of mentors and mentees with their information
     * @return A json formatted list of mentor-mentee assignments, paired by id
     */
    public static String match(String json) {
        Map.Entry<ArrayList<User>, ArrayList<User>> inputs = fetchUsersFromJson(json);

        return writeToJson(performMatch(inputs.getKey(), inputs.getValue()));
    }

    /**
     * Computation of the stable marriage algorithm. It is an adapted variant of
     * the Gale-Shapely algorithm to allow one woman to accept multiple men (one
     * mentor can accept multiple mentees).
     * 
     * @param mentors The men in the algorithm.
     * @param mentees The women.
     */
    private static ArrayList<User> performMatch(ArrayList<User> mentors, ArrayList<User> mentees) {
        // initialize preferences
        HashMap<User, ArrayList<User>> menteePreferences = new HashMap<>();
        HashMap<User, ArrayList<User>> mentorPreferences = new HashMap<>();
        removeRandomExtraUsers(mentors, mentees);

        // generate preferences for mentees
        for (User mentee : mentees) {
            ArrayList<User> preferences = new ArrayList<>();
            for (User mentor : mentors) {
                preferences.add(mentor);
            }
            preferences.sort((a, b) -> ((Integer)mentee.getScore(b)).compareTo((mentee.getScore(a))));

            menteePreferences.put(mentee, preferences);
        }

        //generate preferences for mentors
        for (User mentor : mentors) {
            ArrayList<User> preferences = new ArrayList<>();
            for (User mentee : mentees) {
                preferences.add(mentee);
            }
            preferences.sort((a, b) -> ((Integer)mentor.getScore(b)).compareTo((mentor.getScore(a))));

            mentorPreferences.put(mentor, preferences);
        }

        while(existFreeUsers(menteePreferences.keySet())){ // looping until proposers exist
            User proposer = getFirstAvailableUser(menteePreferences.keySet());

            for (int i = 0; i < menteePreferences.get(proposer).size(); i++) {
                User candidate = menteePreferences.get(proposer).get(i);

                 if (!candidate.isFull()) {
                    proposer.engage(candidate);
                    break;
                } else if (candidate.prefersToLeastWantedPartner(proposer)) {
                    candidate.disengage(candidate.getLeastPreferredPartner());
                    proposer.engage(candidate);
                    break;
                }
            }
        }

        return mentees;
    }

    /**
     * If there are an uneven of number of mentors or mentees, the input has to be cleaned.
     * Some mentors/mentees will be removed at random.
     */
    private static void removeRandomExtraUsers(ArrayList<User> mentors, ArrayList<User> mentees) {
        // If there are more mentees than mentors then some at random will be left out
        int netUserCount = 0;
        for (User mentor : mentors) {
            netUserCount += mentor.getPartnerLimit();
        }

        for (User mentee : mentees) {
            netUserCount -= mentee.getPartnerLimit();
        }


        if (netUserCount < 0) {
            Collections.shuffle(mentees);

            int i = 0;
            while (netUserCount != 0 && mentees.size() > i) {
                User mentee = mentees.get(i);
                int numberOfPlacesRemoving = Math.min(mentee.getPartnerLimit() - 1, netUserCount*-1);
                mentee.setPartnerLimit(mentee.getPartnerLimit() - numberOfPlacesRemoving);
                netUserCount += numberOfPlacesRemoving;
                if (mentee.getPartnerLimit() == 0) mentees.remove(mentee);;
                i++;
            }

            for (int k = netUserCount; k < 0; k++) {
                mentees.remove(-1*k);
            }
        }
    }

    /**
     * Format a json string containing a list of mentee/mentor pairs
     * @param mentees the list of all mentees to be matched
     * @return a string of the json formatted pairs
     */
    private static String writeToJson(ArrayList<User> mentees) {
        String json = "{ \"assignments\": [ ";
        for (User mentee : mentees) {
            for (User partner : mentee.getPartners()) {
                json += "{ ";
                json += "\"mentee_id\": \"" + mentee.getId() + "\", ";
                json += "\"mentor_id\": \"" + partner.getId();
                json += "\"}, ";
            }
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
    private static User getFirstAvailableUser(Set<User> mentees) {
        for (User mentee : mentees) {
            if (!mentee.isFull()) return mentee;
        }
        throw new Error("Unreachable statement");
    }

    /**
     * Check if there is a mentee that does not have a mentor assigned
     * @param mentees A set of the mentees
     * @return true if a mentee is not matched otherwise false
     */
    private static boolean existFreeUsers(Set<User> mentees) {
        for (User user : mentees) {
            if (!user.isFull()) {
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
    private static Map.Entry<ArrayList<User>, ArrayList<User>> fetchUsersFromJson(String json) {
        Map jsonRootObject = new Gson().fromJson(json, Map.class);

        // get mentors
        List<Map> mentorsJson = (List) jsonRootObject.get("mentors");
        ArrayList<User> mentors = new ArrayList<>();
        for (Map mentor : mentorsJson) {
            User newUser = new User(
                    mentor.get("ID").toString(),
                    ((Number) mentor.get("age")).intValue(),
                    (Boolean) mentor.get("isMale"),
                    (List<String>) mentor.get("hobbies"),
                    (List<String>) mentor.get("interests"),
                    ((Number) mentor.getOrDefault("partner_limit", 1)).intValue());
            mentors.add(newUser);
        }

        // get mentees
        List<Map> menteeJson = (List) jsonRootObject.get("mentees");
        ArrayList<User> mentees = new ArrayList<>();
        for (Map mentee : menteeJson) {
            User newUser = new User(mentee.get("ID").toString(),
                    ((Number) mentee.get("age")).intValue(),
                    (Boolean) mentee.get("isMale"),
                    (List<String>) mentee.get("hobbies"),
                    (List<String>) mentee.get("interests"),
                    ((Number) mentee.getOrDefault("partner_limit", 1)).intValue());
            mentees.add(newUser);
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
            ageWeight = AGE_WEIGHT;
            sexWeight = SEX_WEIGHT;
            hobbyWeight = HOBBY_WEIGHT;
            interestWeight = INTEREST_WEIGHT;
        }

        return new AbstractMap.SimpleEntry<>(mentors, mentees);
    }
}
