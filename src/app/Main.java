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
        
        // Display authentication status on startup
        authService.displayAuthStatus();
        
        while (true) {
            User user = null;

            while (user == null) {
                System.out.println("📚 Welcome to Codevia!");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Password Reset");
                System.out.println("4. Authentication Settings");
                System.out.println("5. Exit App");
                System.out.print("Insert Your Choice here: ");

                int option = scanner.nextInt();
                scanner.nextLine();

                if (option == 1) {
                    user = authService.register(scanner);
                } else if (option == 2) {
                    user = authService.login(scanner);
                } else if (option == 3) {
                    authService.sendPasswordReset(scanner);
                } else if (option == 4) {
                    showAuthSettings(authService, scanner);
                } else if (option == 5) {
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
                System.out.println("\n Welcome, " + user.getUsername()
                        + " | Level: " + user.getUserLevel() + " | XP: " + user.getExperiencePoint());
                System.out.println("1. View Available Skills");
                System.out.println("2. Unlock Skill");
                System.out.println("3. Play Quizzes");
                System.out.println("4. View Daily Challenge");
                System.out.println("5. View Achievements");
                System.out.println("6. Account Settings");
                System.out.println("7. Logout");

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
                        System.out.println("Enter the skill name to take its Quiz: ");
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
                        showAccountSettings(authService, user, scanner);
                        break;
                    case 7:
                        System.out.println("👋 Goodbye, " + user.getUsername() + "!");
                        user = null;
                        break;
                    default:
                        System.out.println("Invalid Option!");
                }
            } while (user != null); // While Condition
        }
    }

    private static void showAuthSettings(AuthService authService, Scanner scanner) {
        while (true) {
            System.out.println("\n=== Authentication Settings ===");
            authService.displayAuthStatus();
            System.out.println("1. Toggle Firebase Authentication");
            System.out.println("2. View Current Settings");
            System.out.println("3. Refresh Users from Database");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose option: ");

            try {
                int option = Integer.parseInt(scanner.nextLine());
                
                switch (option) {
                    case 1:
                        System.out.print("Enable Firebase Authentication? (y/n): ");
                        String choice = scanner.nextLine().trim().toLowerCase();
                        boolean enable = choice.equals("y") || choice.equals("yes");
                        authService.setUseFirebaseAuth(enable);
                        System.out.println("✅ Firebase Authentication " + (enable ? "enabled" : "disabled"));
                        break;
                    case 2:
                        authService.displayAuthStatus();
                        break;
                    case 3:
                        refreshUsers(authService);
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("❌ Invalid option");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number");
            }
        }
    }

    private static void showAccountSettings(AuthService authService, User user, Scanner scanner) {
        while (true) {
            System.out.println("\n=== Account Settings ===");
            System.out.println("User: " + user.getUsername());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Level: " + user.getUserLevel());
            System.out.println("Experience: " + user.getExperiencePoint() + " XP");
            System.out.println("\n1. Change Password");
            System.out.println("2. View Account Info");
            System.out.println("3. Delete Account");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose option: ");

            try {
                int option = Integer.parseInt(scanner.nextLine());
                
                switch (option) {
                    case 1:
                        authService.changePassword(user, scanner);
                        break;
                    case 2:
                        displayDetailedAccountInfo(user);
                        break;
                    case 3:
                        if (authService.deleteAccount(user, scanner)) {
                            System.out.println("👋 Account deleted. Returning to main menu...");
                            return; // This will cause the user to be logged out
                        }
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("❌ Invalid option");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number");
            }
        }
    }

    private static void displayDetailedAccountInfo(User user) {
        System.out.println("\n=== Detailed Account Information ===");
        System.out.println("👤 User ID: " + user.getUserId());
        System.out.println("📝 Username: " + user.getUsername());
        System.out.println("📧 Email: " + user.getEmail());
        System.out.println("🎆 Experience Points: " + user.getExperiencePoint() + " XP");
        System.out.println("🎖️ Level: " + user.getUserLevel());
        
        System.out.println("\n🛠️ Unlocked Skills:");
        if (user.getUnlockedSkills().isEmpty()) {
            System.out.println("  No skills unlocked yet");
        } else {
            for (String skill : user.getUnlockedSkills()) {
                System.out.println("  ✅ " + skill);
            }
        }
        
        System.out.println("\n🏆 Achievements:");
        if (user.getAchievements().isEmpty()) {
            System.out.println("  No achievements earned yet");
        } else {
            for (String achievement : user.getAchievements()) {
                System.out.println("  🏅 " + achievement);
            }
        }
        System.out.println("=======================================\n");
    }

    private static void refreshUsers(AuthService authService) {
        UserService userService = authService.getUserService();
        userService.refreshUsersFromDatabase();
        System.out.println("✅ Users have been refreshed from the database.");
    }
}
