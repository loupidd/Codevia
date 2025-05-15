package service;

import model.DailyChallenge;
import model.User;

public class DailyChallengeService {
    private DailyChallenge dailyChallenge = new DailyChallenge();

    public void trackQuestionAnswered(User user, AchievementService achievementService) {
        dailyChallenge.resetIfNewDay();


        if (!dailyChallenge.isCompleted()) {
            dailyChallenge.incrementProgress();

            if (dailyChallenge.isCompleted()) {
                int rewardXp = 50;
                System.out.println("🏆 Daily Challenge Completed! You earned " + rewardXp + " bonus XP!");
                user.gainExperiencePoint(rewardXp);

                //Achievement Trigger
                achievementService.dailyChallengeCompleted();
            } else {
                System.out.println("📊 Daily Progress: " +
                        dailyChallenge.getQuestionsAnswered() + "/3 questions answered today.");
            }
        }
    }

    public void showStatus() {
        dailyChallenge.resetIfNewDay();
        System.out.println("📅 Daily Challenge: Answer 3 quiz questions");
        System.out.println("Progress: " + dailyChallenge.getQuestionsAnswered() + "/3");
        System.out.println("Status: " + (dailyChallenge.isCompleted() ? "✅ Completed" : "⏳ In Progress"));
    }
}
