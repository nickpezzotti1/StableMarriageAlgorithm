import java.util.List;

public class Mentee extends User {

    protected Mentor mentor; // this value is assigned from the algorithm

    public Mentee(String id, Integer age, Boolean isMale, List<String> hobbies, List<String> interests) {
        super(id, age, isMale, hobbies, interests);
    }

    public Mentor getMentor() {
        return mentor;
    }

    public boolean hasMentor() {
        return (mentor == null? false : true);
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }
}
