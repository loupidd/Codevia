package service;

import model.Skill;
import model.User;
import exception.SkillNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SkillService extends BaseService {
    private List<Skill> skills;

    public SkillService() {
        super();
        this.skills = new ArrayList<>();
        initializeSkills();
    }

    private void initializeSkills() {
        skills.add(new Skill("1", "Java Basics", "Learn Java fundamentals", 0));
        skills.add(new Skill("2", "OOP Concepts", "Object-Oriented Programming", 50));
        skills.add(new Skill("3", "Data Structures", "Arrays, Lists, Maps", 100));
        skills.add(new Skill("4", "Algorithms", "Sorting and Searching", 200));
        skills.add(new Skill("5", "Database", "SQL and NoSQL databases", 300));
    }

    @Override
    public void displayInfo() {
        System.out.println("Skill Service - Managing learning skills and courses");
        System.out.println("Available skills: " + skills.size());
    }

    public void showSkills() {
        System.out.println("\n📚 Available Skills:");
        System.out.println("===================");
        for (Skill skill : skills) {
            String status = skill.isUnlocked() ? "✅ Unlocked" : "🔒 Locked";
            System.out.println(skill.getSkillName() + " - " + skill.getDescription() +
                    " (Required XP: " + skill.getRequiredXP() + ") " + status);
        }
    }

    public void unlockSkill(Scanner scanner, User user) {
        System.out.print("Enter skill name to unlock: ");
        String skillName = scanner.nextLine().trim();

        try {
            Skill skill = findSkillByName(skillName);

            if (skill.isUnlocked()) {
                System.out.println("❌ Skill already unlocked!");
                return;
            }

            if (user.getExperiencePoint() < skill.getRequiredXP()) {
                System.out.println("❌ Not enough XP! Required: " + skill.getRequiredXP() +
                        ", You have: " + user.getExperiencePoint());
                return;
            }

            skill.setUnlocked(true);
            user.unlockSkill(skillName);
            System.out.println("✅ Skill unlocked: " + skillName);

        } catch (SkillNotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    public Skill findSkillByName(String name) throws SkillNotFoundException {
        return skills.stream()
                .filter(skill -> skill.getSkillName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new SkillNotFoundException("Skill not found: " + name));
    }

    public List<Skill> getSkills() {
        return skills;
    }
}