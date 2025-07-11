package model;

import interface_.Learnable;

public class Skill implements Learnable {
    private String skillId;
    private String skillName;
    private String description;
    private int requiredXP;
    private boolean isUnlocked;
    private boolean isCompleted;

    // Constructor
    public Skill(String skillId, String skillName, String description, int requiredXP) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.description = description;
        this.requiredXP = requiredXP;
        this.isUnlocked = false;
        this.isCompleted = false;
    }

    // Encapsulation
    public String getSkillId() { return skillId; }
    public String getSkillName() { return skillName; }
    public String getDescription() { return description; }
    public int getRequiredXP() { return requiredXP; }
    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }

    // Interface implementation
    @Override
    public void learn() {
        System.out.println("Learning " + skillName + "...");
        this.isCompleted = true;
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}