package service;

import model.User;
import java.util.Scanner;

public class AuthService {
    public User register(Scanner scanner){
        System.out.println("Choose a username: ");
        String username = scanner.nextLine();
        System.out.println("Choose a password");
        String password = scanner.nextLine();

        // Return - Success Messages - New User
        // Dummy Authentication method
        System.out.println("✅ Registration Successful!");
        return new User(username, password);
    }

    public User login(Scanner scanner) {
        System.out.println("Enter username: ");
        String username = scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.nextLine();

        // Return - Success Messages -  New User
        // Dummy Authentication method
        System.out.println("✅ Registration Successful!");
        return new User(username, password);
    }
}
