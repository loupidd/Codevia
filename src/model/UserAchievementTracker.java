package model;

import java.time.LocalDate;
import java.util.*;

public class UserAchievementTracker {
    private Map<String, Achievement> achievements = new HashMap<>();
    private int quizzesCompleted = 0;
    private int dailyStreak = 0;
    private LocalDate lastDailyDate = null;

    public UserAchievementTracker() {
        achievements.put("First Quiz", new Achievement("🎯 First Quiz", "Complete your first quiz"));
        achievements.put("3-Day Streak", new Achievement("🔥 3-Day Streak", "Complete daily challenge 3 days in a row"));
        achievements.put("Quiz Master", new Achievement("📚 Quiz Master", "Complete 5 quizzes total"));
    }

    public void recordQuizCompleted() {
        quizzesCompleted++;

        if (quizzesCompleted == 1) {
            unlock("First Quiz");
        }
        if (quizzesCompleted == 5) {
            unlock("Quiz Master");
        }
    }

    public void recordDailyChallengeCompleted() {
        LocalDate today = LocalDate.now();

        if (lastDailyDate != null && lastDailyDate.plusDays(1).equals(today)) {
            dailyStreak++;
        } else if (!today.equals(lastDailyDate)) {
            dailyStreak = 1; // Reset streak if broken
        }

        lastDailyDate = today;

        if (dailyStreak == 3) {
            unlock("3-Day Streak");
        }
    }

    private void unlock(String name) {
        Achievement a = achievements.get(name);
        if (a != null && !a.isUnlocked()) {
            a.unlock();
            System.out.println("🏅 Achievement Unlocked: " + a.getName() + " - " + a.getDescription());
        }
    }

    public void showAchievements() {
        System.out.println("\n🏆 Your Achievements:");
        for (Achievement a : achievements.values()) {
            System.out.println((a.isUnlocked() ? "✅" : "❌") + " " + a.getName() + ": " + a.getDescription());
        }
        System.out.println();
    }
}
