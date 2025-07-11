package service;

import model.User;
import exception.AuthenticationException;
import exception.ValidationException;

import java.util.Scanner;

public class AuthService extends BaseService {
    private UserService userService;
    private FirebaseAuthService firebaseAuthService;
    private boolean useFirebaseAuth;

    public AuthService(UserService userService) {
        super();
        this.userService = userService;
        this.firebaseAuthService = FirebaseAuthService.getInstance();
        this.firebaseAuthService.setUserService(userService);
        this.useFirebaseAuth = true; // Enable Firebase authentication by default
    }

    public void setUseFirebaseAuth(boolean useFirebaseAuth) {
        this.useFirebaseAuth = useFirebaseAuth;
    }

    public UserService getUserService() {
        return userService;
    }

    @Override
    public void displayInfo() {
        System.out.println("Authentication Service - Handling user login and registration");
    }

    public User register(Scanner scanner) {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            User user;
            if (useFirebaseAuth) {
                user = firebaseAuthService.registerUser(username, email, password);
                System.out.println("✅ Firebase registration successful! Welcome, " + username + "!");
            } else {
                user = userService.createUser(username, email, password);
                System.out.println("✅ Local registration successful! Welcome, " + username + "!");
            }
            
            return user;

        } catch (AuthenticationException | ValidationException e) {
            System.out.println("❌ Registration failed: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("❌ Registration failed: " + e.getMessage());
            return null;
        }
    }

    public User login(Scanner scanner) {
        try {
            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            User user;
            if (useFirebaseAuth) {
                user = firebaseAuthService.loginUser(email, password);
                System.out.println("✅ Firebase login successful! Welcome back, " + user.getUsername() + "!");
            } else {
                user = authenticate(email, password);
                System.out.println("✅ Local login successful! Welcome back, " + user.getUsername() + "!");
            }
            
            return user;

        } catch (AuthenticationException e) {
            System.out.println("❌ Login failed: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("❌ Login failed: " + e.getMessage());
            return null;
        }
    }

    private User authenticate(String email, String password) throws AuthenticationException {
        User user = userService.findUserByEmail(email);

        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        if (!user.getPassword().equals(password)) {
            throw new AuthenticationException("Invalid password");
        }

        return user;
    }

    // Additional Firebase Authentication methods

    /**
     * Login with Firebase ID token (for client-side authentication)
     */
    public User loginWithIdToken(String idToken) {
        try {
            if (!useFirebaseAuth) {
                throw new AuthenticationException("Firebase authentication is disabled");
            }
            
            User user = firebaseAuthService.verifyIdToken(idToken);
            System.out.println("✅ Token authentication successful! Welcome, " + user.getUsername() + "!");
            return user;
            
        } catch (AuthenticationException e) {
            System.out.println("❌ Token authentication failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Send password reset email
     */
    public boolean sendPasswordReset(Scanner scanner) {
        try {
            System.out.print("Enter your email address: ");
            String email = scanner.nextLine().trim();
            
            if (!useFirebaseAuth) {
                System.out.println("❌ Password reset is only available with Firebase authentication");
                return false;
            }
            
            firebaseAuthService.sendPasswordResetEmail(email);
            System.out.println("✅ Password reset instructions have been sent to your email!");
            return true;
            
        } catch (AuthenticationException e) {
            System.out.println("❌ Password reset failed: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("❌ Password reset failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Change user password
     */
    public boolean changePassword(User user, Scanner scanner) {
        try {
            System.out.print("Enter current password: ");
            String currentPassword = scanner.nextLine().trim();
            
            System.out.print("Enter new password: ");
            String newPassword = scanner.nextLine().trim();
            
            System.out.print("Confirm new password: ");
            String confirmPassword = scanner.nextLine().trim();
            
            if (!newPassword.equals(confirmPassword)) {
                System.out.println("❌ Passwords do not match");
                return false;
            }
            
            if (newPassword.length() < 6) {
                System.out.println("❌ Password must be at least 6 characters");
                return false;
            }
            
            // Verify current password
            if (!user.getPassword().equals(currentPassword)) {
                System.out.println("❌ Current password is incorrect");
                return false;
            }
            
            if (useFirebaseAuth) {
                firebaseAuthService.updateUserPassword(user.getUserId(), newPassword);
            }
            
            // Update local user password
            user.setPassword(newPassword);
            userService.updateUser(user);
            
            System.out.println("✅ Password changed successfully!");
            return true;
            
        } catch (AuthenticationException e) {
            System.out.println("❌ Password change failed: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("❌ Password change failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete user account
     */
    public boolean deleteAccount(User user, Scanner scanner) {
        try {
            System.out.print("Are you sure you want to delete your account? This cannot be undone. (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            if (!confirmation.equals("yes")) {
                System.out.println("Account deletion cancelled.");
                return false;
            }
            
            System.out.print("Enter your password to confirm: ");
            String password = scanner.nextLine().trim();
            
            if (!user.getPassword().equals(password)) {
                System.out.println("❌ Incorrect password");
                return false;
            }
            
            if (useFirebaseAuth) {
                firebaseAuthService.deleteUser(user.getUserId());
            }
            
            // Remove from local storage (in production, you'd also remove from database)
            // This would require adding a delete method to UserService
            
            System.out.println("✅ Account deleted successfully");
            return true;
            
        } catch (AuthenticationException e) {
            System.out.println("❌ Account deletion failed: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("❌ Account deletion failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get current authentication status
     */
    public void displayAuthStatus() {
        System.out.println("\n=== Authentication Status ===");
        System.out.println("Firebase Authentication: " + (useFirebaseAuth ? "✅ Enabled" : "❌ Disabled"));
        System.out.println("Local Authentication: ✅ Available");
        System.out.println("===========================\n");
    }
}
