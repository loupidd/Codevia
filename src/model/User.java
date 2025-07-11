package model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String username;
    private String email;
    private String password;
    private int experiencePoint;
    private int userLevel;
    private List<String> unlockedSkills;
    private List<String> achievements;

    // Constructor
    public User() {
        this.unlockedSkills = new ArrayList<>();
        this.achievements = new ArrayList<>();
        this.experiencePoint = 0;
        this.userLevel = 1;
    }

    public User(String userId, String username, String email, String password) {
        this();
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Encapsulation - Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getExperiencePoint() { return experiencePoint; }
    public void setExperiencePoint(int experiencePoint) {
        this.experiencePoint = experiencePoint;
        updateLevel();
    }

    public int getUserLevel() { return userLevel; }
    public void setUserLevel(int userLevel) { this.userLevel = userLevel; }

    public List<String> getUnlockedSkills() { return unlockedSkills; }
    public void setUnlockedSkills(List<String> unlockedSkills) { this.unlockedSkills = unlockedSkills; }

    public List<String> getAchievements() { return achievements; }
    public void setAchievements(List<String> achievements) { this.achievements = achievements; }

    // Methods
    public void addExperience(int xp) {
        this.experiencePoint += xp;
        updateLevel();
    }

    private void updateLevel() {
        this.userLevel = (experiencePoint / 100) + 1;
    }

    public void unlockSkill(String skillName) {
        if (!unlockedSkills.contains(skillName)) {
            unlockedSkills.add(skillName);
        }
    }

    public void addAchievement(String achievement) {
        if (!achievements.contains(achievement)) {
            achievements.add(achievement);
        }
    }

    // Method expected by service classes
    public void gainExperiencePoint(int xp) {
        addExperience(xp);
    }

    // Achievement tracker field and methods
    private UserAchievementTracker achievementTracker;
    
    public UserAchievementTracker getAchievementTracker() {
        return achievementTracker;
    }
    
    public void setAchievementTracker(UserAchievementTracker tracker) {
        this.achievementTracker = tracker;
    }
}
