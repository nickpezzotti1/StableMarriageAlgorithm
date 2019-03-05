import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

public class MatchingAlgorithmTest {

    @Test
    public void test3Mentors4Mentees() {
        test("src/test/testValues/test1/input.txt", "src/test/testValues/test1/expected_output.txt");
    }

    @Test
    public void testEqualNumberOfUsers() {
        test("src/test/testValues/test2/input.txt", "src/test/testValues/test2/expected_output.txt");
    }

    @Test
    public void testConfigParameters() {
        test("src/test/testValues/test3/input.txt", "src/test/testValues/test3/expected_output.txt");
    }

    @Test
    public void testHobbies() {
        test("src/test/testValues/test4/input.txt", "src/test/testValues/test4/expected_output.txt");
    }

    @Test
    public void testInterests() {
        test("src/test/testValues/test5/input.txt", "src/test/testValues/test5/expected_output.txt");
    }

    @Test(timeout=2500)
    public void testMoreMenteesThanMentors() {
        test("src/test/testValues/test6/input.txt", "src/test/testValues/test6/expected_output.txt");
    }

    @Test(timeout=2500)
    public void test4Mentors3Mentees() {
        test("src/test/testValues/test7/input.txt", "src/test/testValues/test7/expected_output.txt");
    }

    @Test(timeout=2500)
    public void testMenteeLimit() {
        test("src/test/testValues/test8/input.txt", "src/test/testValues/test8/expected_output.txt");
    }

    private void test(String json_input_file_path, String json_expected_output_file_path) {
        String jsonRequestBody = null;
        String expectedResponse = null;

        try {
            jsonRequestBody = String.join("\n", Files.readAllLines(Paths.get(json_input_file_path)));
            expectedResponse = String.join("\n", Files.readAllLines(Paths.get(json_expected_output_file_path)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expectedResponse, MatchingAlgorithm.match(jsonRequestBody));
    }
}
