package service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import com.google.firebase.ErrorCode;
import exception.AuthenticationException;
import exception.ValidationException;
import model.User;

import java.util.concurrent.ExecutionException;

public class FirebaseAuthService {
    private static FirebaseAuthService instance;
    private FirebaseAuth firebaseAuth;
    private UserService userService;
    private boolean initialized = false;

    private FirebaseAuthService() {
        // Delayed initialization
    }

    private synchronized void initialize() throws Exception {
        if (!initialized) {
            firebaseAuth = FirebaseAuth.getInstance();
            initialized = true;
        }
    }

    public static FirebaseAuthService getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthService();
        }
        return instance;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new user with Firebase Authentication
     */
    public User registerUser(String username, String email, String password) throws AuthenticationException, ValidationException {
        try {
            // Initialize Firebase Auth if not already
            initialize();

            // Validate input
            validateUserInput(username, email, password);

            // Check if user already exists locally
            if (userService.findUserByEmail(email) != null) {
                throw new ValidationException("User with this email already exists");
            }

            // Create user in Firebase Auth
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setDisplayName(username)
                    .setEmailVerified(false);

            UserRecord userRecord = firebaseAuth.createUser(request);
            System.out.println("✅ Firebase user created: " + userRecord.getUid());

            // Create user in local database
            User user = userService.createUser(userRecord.getUid(), username, email, password);
            
            return user;

        } catch (FirebaseAuthException e) {
            String errorMessage = getAuthErrorMessage(e);
            throw new AuthenticationException("Registration failed: " + errorMessage);
        } catch (Exception e) {
            throw new AuthenticationException("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Authenticate user with Firebase
     */
    public User loginUser(String email, String password) throws AuthenticationException {
        try {
            // Initialize Firebase Auth if not already
            initialize();

            // Get user by email from Firebase
            UserRecord userRecord = firebaseAuth.getUserByEmail(email);
            
            // Note: Firebase Admin SDK doesn't directly verify passwords
            // For admin SDK, we'll verify the password against our local storage
            // In production, you'd use Firebase Client SDK for proper authentication
            
            User localUser = userService.findUserByEmail(email);
            if (localUser == null) {
                throw new AuthenticationException("User not found in local database");
            }

            // Verify password (in production, use Firebase Client SDK)
            if (!verifyPassword(localUser, password)) {
                throw new AuthenticationException("Invalid password");
            }

            // Update user info from Firebase if needed
            if (!localUser.getUsername().equals(userRecord.getDisplayName())) {
                localUser = updateUserFromFirebase(localUser, userRecord);
            }

            System.out.println("✅ Firebase authentication successful for: " + userRecord.getEmail());
            return localUser;

        } catch (FirebaseAuthException e) {
            throw new AuthenticationException("Login failed: " + getAuthErrorMessage(e));
        } catch (Exception e) {
            throw new AuthenticationException("Login failed: " + e.getMessage());
        }
    }

    /**
     * Verify user's identity token (for client-side authentication)
     */
    public User verifyIdToken(String idToken) throws AuthenticationException {
        try {
            var decodedToken = firebaseAuth.verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();

            // Get user from local database
            User user = userService.findUserByEmail(email);
            if (user == null) {
                // User exists in Firebase but not locally, create local user
                UserRecord userRecord = firebaseAuth.getUser(uid);
                user = userService.createUser(uid, userRecord.getDisplayName(), email, "");
            }

            return user;

        } catch (FirebaseAuthException e) {
            throw new AuthenticationException("Token verification failed: " + e.getMessage());
        } catch (ValidationException e) {
            throw new AuthenticationException("User creation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new AuthenticationException("Token verification failed: " + e.getMessage());
        }
    }

    /**
     * Update user password in Firebase
     */
    public void updateUserPassword(String uid, String newPassword) throws AuthenticationException {
        try {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                    .setPassword(newPassword);

            firebaseAuth.updateUser(request);
            System.out.println("✅ Password updated successfully for user: " + uid);

        } catch (FirebaseAuthException e) {
            throw new AuthenticationException("Password update failed: " + getAuthErrorMessage(e));
        }
    }

    /**
     * Delete user from Firebase Auth
     */
    public void deleteUser(String uid) throws AuthenticationException {
        try {
            firebaseAuth.deleteUser(uid);
            System.out.println("✅ User deleted from Firebase: " + uid);

        } catch (FirebaseAuthException e) {
            throw new AuthenticationException("User deletion failed: " + getAuthErrorMessage(e));
        }
    }

    /**
     * Send password reset email
     */
    public void sendPasswordResetEmail(String email) throws AuthenticationException {
        try {
            // Note: Admin SDK doesn't directly send password reset emails
            // This would typically be done on the client side
            // Here we'll just verify the user exists
            firebaseAuth.getUserByEmail(email);
            System.out.println("✅ User verified for password reset: " + email);
            System.out.println("📧 In production, password reset email would be sent via client SDK");

        } catch (FirebaseAuthException e) {
            throw new AuthenticationException("Password reset failed: " + getAuthErrorMessage(e));
        }
    }

    /**
     * Get user info from Firebase
     */
    public UserRecord getFirebaseUser(String uid) throws AuthenticationException {
        try {
            return firebaseAuth.getUser(uid);
        } catch (FirebaseAuthException e) {
            throw new AuthenticationException("Failed to get user info: " + getAuthErrorMessage(e));
        }
    }

    // Private helper methods

    private void validateUserInput(String username, String email, String password) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
        if (email == null || !isValidEmail(email)) {
            throw new ValidationException("Invalid email format");
        }
        if (password == null || password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean verifyPassword(User user, String password) {
        // In production, you'd use proper password hashing
        // For now, simple comparison (not secure for production)
        return user.getPassword().equals(password);
    }

    private User updateUserFromFirebase(User localUser, UserRecord firebaseUser) {
        // Update local user with Firebase user info if needed
        // This is where you'd sync any changes from Firebase
        return localUser;
    }

    private String getAuthErrorMessage(FirebaseAuthException e) {
        String errorCode = e.getErrorCode().toString();
        switch (errorCode) {
            case "EMAIL_ALREADY_EXISTS":
                return "An account with this email already exists";
            case "INVALID_EMAIL":
                return "Invalid email address";
            case "WEAK_PASSWORD":
                return "Password is too weak";
            case "USER_NOT_FOUND":
                return "No user found with this email";
            case "WRONG_PASSWORD":
                return "Incorrect password";
            case "TOO_MANY_ATTEMPTS_TRY_LATER":
                return "Too many failed attempts. Please try again later";
            default:
                return e.getMessage();
        }
    }
}
