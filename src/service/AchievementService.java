package service;

import model.User;
import model.UserAchievementTracker;

public class AchievementService {
    private UserAchievementTracker tracker;

    public AchievementService(User user) {
        this.tracker = new UserAchievementTracker();
        user.setAchievementTracker(tracker);
    }

    public void quizCompleted() {
        tracker.recordQuizCompleted();
    }

    public void dailyChallengeCompleted() {
        tracker.recordDailyChallengeCompleted();
    }

    public void showAchievements() {
        tracker.showAchievements();
    }
}
