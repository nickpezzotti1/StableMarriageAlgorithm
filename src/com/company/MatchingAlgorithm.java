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

        for (Mentee mentee : mentees) {
            TreeMap<Integer, Mentor> preferences = new TreeMap<>();
            for (Mentor mentor : mentors) {
                preferences.put(mentee.getScore(mentor), mentor);
            }
            ArrayList<Mentor> mentorsTheyWant = new ArrayList<>();
            preferences.forEach((k, v) -> mentorsTheyWant.add(v));

            Collections.reverse(mentorsTheyWant);

            menteePreferences.put(mentee, mentorsTheyWant);
        }

        //generate for mentors
        for (Mentor mentor : mentors) {
            TreeMap<Integer, Mentee> preferences = new TreeMap<>();
            for (Mentee mentee : mentees) {
                preferences.put(mentor.getScore(mentee), mentee);
            }
            ArrayList<Mentee> menteesTheyWant = new ArrayList<>();
            preferences.forEach((k, v) -> menteesTheyWant.add(v));

            Collections.reverse(menteesTheyWant);

            mentorPreferences.put(mentor, menteesTheyWant);
        }

        while(existFreeMentees(menteePreferences)){ // looping until proposers exist
            Mentee proposer = getFirstAvailableMentee(menteePreferences.keySet());

            for (int i = 0; i < menteePreferences.get(proposer).size(); i++) {
                Mentor candidate = menteePreferences.get(proposer).get(i);

                if (!candidate.hasMentee()) {
                    candidate.setMentee(proposer);
                    proposer.setMentor(candidate);
                    break;
                } else if (candidate.prefers(proposer)) {
                    candidate.getMentee().setMentor(null); // unengage

                    candidate.setMentee(proposer);
                    proposer.setMentor(candidate);
                    break;
                }
                else {
                    System.out.println();
                }
            }
        }

        return writeToJson(mentees);
    }

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
        Mentor a = new Mentor(20, true, 0);
        Mentor b = new Mentor(15, true, 1);
        Mentor c = new Mentor(10, true, 2);

        Mentee d = new Mentee(18, true, 3);
        Mentee e = new Mentee(19, true, 4);
        Mentee f = new Mentee(12, true, 5);

        ArrayList<Mentor> mentors = new ArrayList<>();
        mentors.add(a);
        mentors.add(b);
        mentors.add(c);

        ArrayList<Mentee> mentees = new ArrayList<>();
        mentees.add(d);
        mentees.add(e);
        mentees.add(f);

        System.out.println(match(mentors, mentees));
    }
}
