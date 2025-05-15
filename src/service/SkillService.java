package service;

import model.Skill;
import model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SkillService {
    private List<Skill> skillList = new ArrayList<>();
    //Array List of Skills Learned
    public SkillService() {
        skillList.add(new Skill("s1", "Java Basics", "Learn variables, loops, and conditions."));
        skillList.add(new Skill("s2", "OOP", "Learn classes, inheritance, and polymorphism."));
        skillList.add(new Skill("s3", "File I/O", "Learn how to read and write files in Java."));
    }

    public void showSkills() {
        System.out.println("\n📘 Available Skills:");
        for (Skill skill : skillList) {
            String status = skill.isUnlocked() ? "✅ Unlocked" : "🔒 Locked";
            System.out.println("- " + skill.getName() + ": " + skill.getDescription() + " [" + status + "]");
        }
    }

    public void unlockSkill(Scanner scanner, User user) {
        System.out.println("\nEnter skill name to unlock:");
        String name = scanner.nextLine();

        for (Skill skill : skillList) {
            if (skill.getName().equalsIgnoreCase(name)) {
                if (skill.isUnlocked()) {
                    System.out.println("✅ Skill already unlocked.");
                } else {
                    skill.unlock();
                    user.gainExperiencePoint(50);
                    System.out.println("🎉 You unlocked: " + skill.getName() + " and gained 50 XP!");
                }
                return;
            }
        }
        System.out.println("❌ Skill not found.");
    }
}
