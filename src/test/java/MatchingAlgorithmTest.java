import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

public class MatchingAlgorithmTest {

    @Test
    public void test3Mentors4Mentees() {
        String jsonRequestBody = null;
        String expectedResponse = null;

        try {
            jsonRequestBody = String.join("\n", Files.readAllLines(Paths.get("src/test/testValues/test1/input.txt")));
            expectedResponse = String.join("\n", Files.readAllLines(Paths.get("src/test/testValues/test1/expected_output.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expectedResponse, MatchingAlgorithm.match(jsonRequestBody));
    }

    @Test
    public void testEqualNumberOfUsers() {
        String jsonRequestBody = null;
        String expectedResponse = null;

        try {
            jsonRequestBody = String.join("\n", Files.readAllLines(Paths.get("src/test/testValues/test2/input.txt")));
            expectedResponse = String.join("\n", Files.readAllLines(Paths.get("src/test/testValues/test2/expected_output.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expectedResponse, MatchingAlgorithm.match(jsonRequestBody));
    }

    @Test
    public void testConfigParameters() {
        String jsonRequestBody = null;
        String expectedResponse = null;

        try {
            jsonRequestBody = String.join("\n", Files.readAllLines(Paths.get("src/test/testValues/test3/input.txt")));
            expectedResponse = String.join("\n", Files.readAllLines(Paths.get("src/test/testValues/test3/expected_output.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expectedResponse, MatchingAlgorithm.match(jsonRequestBody));
    }
}
