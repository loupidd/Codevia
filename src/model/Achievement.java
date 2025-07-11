package model;

import interface_.Achievable;

public class Achievement implements Achievable {
    private String achievementId;
    private String name;
    private String description;
    private int requiredXP;
    private boolean isUnlocked;

    public Achievement(String achievementId, String name, String description, int requiredXP) {
        this.achievementId = achievementId;
        this.name = name;
        this.description = description;
        this.requiredXP = requiredXP;
        this.isUnlocked = false;
    }

    // Encapsulation
    public String getAchievementId() { return achievementId; }
    public String getName() { return name; }
    public boolean isUnlocked() { return isUnlocked; }

    // Interface implementation
    @Override
    public void unlock() {
        this.isUnlocked = true;
        System.out.println("Achievement unlocked: " + name);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getRequiredXP() {
        return requiredXP;
    }
}