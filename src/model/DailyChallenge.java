package model;

import java.time.LocalDate;

public class DailyChallenge {
    private LocalDate date;
    private int questionsAnswered;
    private boolean completed;

    public DailyChallenge() {
        this.date = LocalDate.now();
        this.questionsAnswered = 0;
        this.completed = false;
    }

    public void incrementProgress() {
        if (!completed) {
            questionsAnswered++;
            if (questionsAnswered >= 3) {
                completed = true;
            }
        }
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getQuestionsAnswered() {
        return questionsAnswered;
    }

    public void resetIfNewDay() {
        if (!date.equals(LocalDate.now())) {
            date = LocalDate.now();
            questionsAnswered = 0;
            completed = false;
        }
    }
}
