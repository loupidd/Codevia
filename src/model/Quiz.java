package model;

import java.util.List;

public class Quiz {
    private String quizId;
    private String skillName;
    private List<Question> questions;
    private int passingScore;

    public Quiz(String quizId, String skillName, List<Question> questions, int passingScore) {
        this.quizId = quizId;
        this.skillName = skillName;
        this.questions = questions;
        this.passingScore = passingScore;
    }

    // Encapsulation
    public String getQuizId() { return quizId; }
    public String getSkillName() { return skillName; }
    public List<Question> getQuestions() { return questions; }
    public int getPassingScore() { return passingScore; }
}
