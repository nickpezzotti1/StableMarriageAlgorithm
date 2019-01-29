package com.company;

import java.util.*;

public class MatchingAlgorithm {

    /**
     * Mentees are men in stable marriage problem. mentors are women (getting proposed to).
     * TODO:
     *      - Only one mentor to mentee
     *      - more mentors than mentees
     * @param mentors
     * @param mentees
     */
    private static String match(ArrayList<Mentor> mentors, ArrayList<Mentee> mentees) {
        // initialize preferences
        HashMap<Mentee, ArrayList<Mentor>> menteePreferences = new HashMap<>();
        HashMap<Mentor, ArrayList<Mentee>> mentorPreferences = new HashMap<>();

        // generate preferences for mentees
        for (Mentee mentee : mentees) {
            ArrayList<Mentor> preferences = new ArrayList<>();
            for (Mentor mentor : mentors) {
                preferences.add(mentor);
            }
            preferences.sort((a, b) -> ((Integer)mentee.getScore(a)).compareTo((mentee.getScore(b))));

            menteePreferences.put(mentee, preferences);
        }

        //generate preferences for mentors
        for (Mentor mentor : mentors) {
            ArrayList<Mentee> preferences = new ArrayList<>();
            for (Mentee mentee : mentees) {
                preferences.add(mentee);
            }
            preferences.sort((a, b) -> ((Integer)mentor.getScore(a)).compareTo((mentor.getScore(b))));

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
                } else if (candidate.prefersToLeast(proposer)) {
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
            json += "\"mentee_id\": " + mentee.getId() + ", ";
            json += "\"mentor_id\": " + mentee.getMentor().getId();
            json += "}, ";
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


    public static void main(String[] args) {
        // dummy data
        Mentor a = new Mentor(20, true, 0, 2);
        Mentor b = new Mentor(15, true, 1, 1);
        Mentor c = new Mentor(10, true, 2, 1);

        Mentee d = new Mentee(18, true, 3);
        Mentee e = new Mentee(19, true, 4);
        Mentee f = new Mentee(12, true, 5);
        Mentee g = new Mentee(20, true, 6);
        //Mentee h = new Mentee(21, true, 7);

        ArrayList<Mentor> mentors = new ArrayList<>();
        mentors.add(a);
        mentors.add(b);
        mentors.add(c);

        ArrayList<Mentee> mentees = new ArrayList<>();
        mentees.add(d);
        mentees.add(e);
        mentees.add(f);
        mentees.add(g);
        //mentees.add(h);

        System.out.println(match(mentors, mentees));
    }
}
