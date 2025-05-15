package model;

import java.util.List;

public class Quiz {
    private String skillName;
    private List<Question> questions;

    public Quiz(String skillName, List<Question> questions){
        this.skillName = skillName;
        this.questions = questions;
    }

    public String getSkillName() {
        return skillName;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
