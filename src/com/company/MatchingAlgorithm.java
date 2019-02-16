package com.company;

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

    private static Mentee getFirstAvailableMentee(Set<Mentee> mentors) {
        for (Mentee mentor : mentors) {
            if (!mentor.hasMentor()) return mentor;
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
            Mentor newMentor = new Mentor(((Number) mentor.get("age")).intValue(),
                    (boolean) mentor.getOrDefault("isMale", false),
                    ((Number) mentor.get("ID")).intValue(),
                    ((Number) mentor.getOrDefault("menteeLimit", 1)).intValue());
            mentors.add(newMentor);
        }

        // get mentees
        List<Map> menteeJson = (List) jsonRootObject.get("mentees");
        ArrayList<Mentee> mentees = new ArrayList<>();
        for (Map mentee : menteeJson) {
            Mentee newMentee = new Mentee(((Number) mentee.get("age")).intValue(),
                    (boolean) mentee.get("isMale"),
                    ((Number) mentee.get("ID")).intValue());
            mentees.add(newMentee);
        }

        return new AbstractMap.SimpleEntry<>(mentors, mentees);
    }

    public static String match(String json) {
        Map.Entry<ArrayList<Mentor>, ArrayList<Mentee>> inputs = fetchMentorsFromJson(json);

        return match(inputs.getKey(), inputs.getValue());
    }

    public static void main(String[] args) {
        String jsonRequestBody = null;

        try {
            jsonRequestBody = String.join("\n", Files.readAllLines(Paths.get("src/requestExample.txt"))) ;
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(match(jsonRequestBody));
    }
}
