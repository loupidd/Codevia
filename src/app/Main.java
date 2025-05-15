package app;


import model.User;
import service.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //Package Call
        SkillService skillService = new SkillService();
        QuizService quizService = new QuizService();
        DailyChallengeService challengeService = new DailyChallengeService();
        //User Credentials
        UserService userService = new UserService();
        AuthService authService = new AuthService(userService);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            User user = null;

            while (user == null) {
                System.out.println("📚 Welcome to Codevia!");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit App");
                System.out.print("Insert Your Choice here: ");

                int option = scanner.nextInt();
                scanner.nextLine();

                if (option == 1) {
                    user = authService.register(scanner);
                } else if (option == 2) {
                    user = authService.login(scanner);
                } else if (option == 3) {
                    System.out.println("Goodbye!");
                    return;
                } else {
                    System.out.println("❌ Invalid Option");
                }

                if (user == null) {
                    System.out.println("🔁 Try again.\n");
                }
            }

            //Achievement to User
            AchievementService achievementService = new AchievementService(user);

            do {
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
                switch (input) {
                    case 1:
                        skillService.showSkills();
                        break;
                    case 2:
                        skillService.unlockSkill(scanner, user);
                        break;
                    case 3:
                        System.out.println("Enter the skill name to take it's Quiz: ");
                        String skillName = scanner.nextLine();
                        quizService.startQuiz(skillName, scanner, user, achievementService, challengeService);
                        break;
                    case 4:
                        challengeService.showStatus();
                        break;
                    case 5:
                        achievementService.showAchievements();
                        break;
                    case 6:
                        System.out.println("Goodbye!");
                        user = null;
                        break;
                    default:
                        System.out.println("Invalid Option!");
                }
            } while (user != null); // While Condition
        }
    }
}