package service;

import model.User;
import java.util.Scanner;

public class AuthService {
    private UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public User register(Scanner scanner) {
        System.out.print("👤 Enter a username: ");
        String username = scanner.nextLine();

        if (userService.isUserRegistered(username)) {
            System.out.println("❌ Username already taken!");
            return null;
        }

        System.out.print("🔐 Enter a password: ");
        String password = scanner.nextLine();

        boolean success = userService.registerUser(username, password);
        if (success) {
            System.out.println("✅ Registration successful!");
            return userService.getUser(username);
        } else {
            System.out.println("❌ Registration failed.");
            return null;
        }
    }

    public User login(Scanner scanner) {
        System.out.print("👤 Enter your username: ");
        String username = scanner.nextLine();

        if (!userService.isUserRegistered(username)) {
            System.out.println("❌ Username not found. Please register first.");
            return null;
        }

        System.out.print("🔐 Enter your password: ");
        String password = scanner.nextLine();

        if (userService.isPasswordCorrect(username, password)) {
            System.out.println("✅ Login successful.");
            return userService.getUser(username);
        } else {
            System.out.println("❌ Incorrect password.");
            return null;
        }
    }
}
