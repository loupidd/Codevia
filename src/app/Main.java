package app;


import model.User;
import service.DailyChallengeService;
import service.QuizService;
import service.AuthService;
import service.SkillService;
import service.AchievementService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //Package Call
        AuthService authService = new AuthService();
        SkillService skillService = new SkillService();
        QuizService quizService = new QuizService();
        DailyChallengeService challengeService =  new DailyChallengeService();

        Scanner scanner = new Scanner(System.in);
        User user = null;

        //Program Header
        System.out.println("📚Welcome to SkillForge!");
        System.out.println("1.Register\n2.Login");
        System.out.print("Insert Your Choice here: ");
        int option = scanner.nextInt();
        scanner.nextLine();

        //Login Validation
        if (option == 1) {
            user = authService.register(scanner);
        } else if (option == 2) {
            user = authService.login(scanner);
        } else {
            System.out.println("Invalid Option");
            return;
        }

        //Null Safety - Login
        if (user == null) {
            System.out.println("Authentication Failed!");
            return;
        }

        //Achievement to User
        AchievementService achievementService = new AchievementService(user);

        while (true) {
            System.out.println("\n Welcome," + user.getUsername()
                    + " | Level: " + user.getUserLevel() + " | XP: " + user.getExperiencePoint());
            System.out.println("1. View Available Skills");
            System.out.println("2. Unlock Skill");
            System.out.println("3. PLay Quizzes");
            System.out.println("4. View Daily Challenge");
            System.out.println("5. View Achievements");
            System.out.println("6. Exit");

            int input = Integer.parseInt(scanner.nextLine());


            // Switch Case Menu Input
            switch(input) {
                case 1:
                    skillService.showSkills();
                    break;
                case 2:
                    skillService.unlockSkill(scanner, user);
                    break;
                case 3:
                    System.out.println("Enter the skill name to take it's Quiz: ");
                    String skillName = scanner.nextLine();
                    quizService.startQuiz(skillName,scanner,user,  achievementService,  challengeService);
                    break;
                case 4:
                    challengeService.showStatus();
                    break;
                case 5:
                    achievementService.showAchievements();
                    break;
                case 6:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid Option!");
            }
        }
    }
}