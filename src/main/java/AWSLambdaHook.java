import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AWSLambdaHook implements RequestHandler<Object, String> {

    @Override
    public String handleRequest(Object input, Context context) {
        context.getLogger().log("Input: " + input);

        Map root = (Map) input;
        // get mentors
        List<Map> mentorsJson = (List) root.get("mentors");
        ArrayList<Mentor> mentors = new ArrayList<>();
        for (Map mentor : mentorsJson) {
            Mentor newMentor = new Mentor(((Number) mentor.get("age")).intValue(),
                    (boolean) mentor.getOrDefault("isMale", false),
                    mentor.get("ID").toString(),
                    ((Number) mentor.getOrDefault("menteeLimit", 1)).intValue());
            mentors.add(newMentor);
        }

        // get mentees
        List<Map> menteeJson = (List) root.get("mentees");
        ArrayList<Mentee> mentees = new ArrayList<>();
        for (Map mentee : menteeJson) {
            Mentee newMentee = new Mentee(((Number) mentee.get("age")).intValue(),
                    (boolean) mentee.get("isMale"),
                    mentee.get("ID").toString());
            mentees.add(newMentee);
        }

        return MatchingAlgorithm.match(mentors, mentees);
    }

}