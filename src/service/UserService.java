package service;

import model.User;
import exception.DatabaseException;
import exception.ValidationException;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class UserService extends BaseService {
    private List<User> users; // In-memory storage for demo

    public UserService() {
        super();
        this.users = new ArrayList<>();
        initializeUsers();
    }

    private void initializeUsers() {
        // Add demo users first (for immediate use)
        users.add(new User("1", "admin", "admin@codevia.com", "admin123"));
        users.add(new User("2", "student", "student@codevia.com", "student123"));
        
        // Try to load users from Firebase in background
        loadUsersFromDatabaseAsync();
    }
    
    private void loadUsersFromDatabaseAsync() {
        // Load from Firebase asynchronously
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                // Wait a bit for Firebase to initialize
                Thread.sleep(2000);
                if (isFirebaseConnected()) {
                    loadUsersFromDatabase();
                }
            } catch (Exception e) {
                System.err.println("Failed to load users from database: " + e.getMessage());
            }
        });
    }

    @Override
    public void displayInfo() {
        System.out.println("User Service - Managing user accounts and profiles");
        System.out.println("Total users: " + users.size());
    }

    public User createUser(String username, String email, String password) throws ValidationException {
        // Generate new user ID for non-Firebase users
        String userId = String.valueOf(users.size() + 1);
        return createUser(userId, username, email, password);
    }

    public User createUser(String userId, String username, String email, String password) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
        if (email == null || !isValidEmail(email)) {
            throw new ValidationException("Invalid email format");
        }
        if (password == null || password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }

        // Check if user already exists
        if (findUserByEmail(email) != null) {
            throw new ValidationException("User with this email already exists");
        }

        User user = new User(userId, username, email, password);
        users.add(user);

        // Save to database
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("password", password);
        userData.put("experiencePoint", user.getExperiencePoint());
        userData.put("userLevel", user.getUserLevel());
        userData.put("firebaseUid", userId);
        userData.put("unlockedSkills", user.getUnlockedSkills());
        userData.put("achievements", user.getAchievements());

        try {
            performDatabaseOperation("save", "users", userId, userData);
        } catch (Exception e) {
            System.err.println("Failed to save user to database: " + e.getMessage());
        }

        return user;
    }

    public User findUserByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public User findUserByFirebaseUid(String firebaseUid) {
        return users.stream()
                .filter(user -> user.getUserId().equals(firebaseUid))
                .findFirst()
                .orElse(null);
    }

    public User findUserById(String userId) {
        return users.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public void updateUser(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", user.getUsername());
        userData.put("email", user.getEmail());
        userData.put("password", user.getPassword());
        userData.put("experiencePoint", user.getExperiencePoint());
        userData.put("userLevel", user.getUserLevel());
        userData.put("firebaseUid", user.getUserId());
        userData.put("unlockedSkills", user.getUnlockedSkills());
        userData.put("achievements", user.getAchievements());

        try {
            performDatabaseOperation("update", "users", user.getUserId(), userData);
        } catch (Exception e) {
            System.err.println("Failed to update user: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    /**
     * Load all users from Firebase database
     */
    private void loadUsersFromDatabase() {
        try {
            System.out.println("Loading users from Firebase database...");
            
            // Get all users from Firebase
            List<Map<String, Object>> userDataList = firebaseService.getAll("users");
            
            // Clear existing users except demo users if we have Firebase data
            if (!userDataList.isEmpty()) {
                // Keep only demo users initially, then add Firebase users
                List<User> demoUsers = new ArrayList<>();
                for (User user : users) {
                    if (user.getUserId().equals("1") || user.getUserId().equals("2")) {
                        demoUsers.add(user);
                    }
                }
                users.clear();
                users.addAll(demoUsers);
            }
            
            for (Map<String, Object> userData : userDataList) {
                try {
                    User user = createUserFromData(userData);
                    if (user != null && !isDuplicateUser(user)) {
                        users.add(user);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load user: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Failed to load users from database: " + e.getMessage());
            System.out.println("Will proceed with demo users...");
        }
    }
    
    private boolean isDuplicateUser(User newUser) {
        return users.stream().anyMatch(user -> user.getEmail().equals(newUser.getEmail()));
    }

    /**
     * Create User object from Firebase data
     */
    private User createUserFromData(Map<String, Object> userData) {
        try {
            // Extract basic user information
            String userId = (String) userData.get("id"); // Document ID from Firebase
            if (userId == null) {
                userId = (String) userData.get("firebaseUid");
            }
            
            String username = (String) userData.get("username");
            String email = (String) userData.get("email");
            String password = (String) userData.get("password");
            
            if (userId == null || username == null || email == null || password == null) {
                System.err.println("Missing required user data for user: " + userData);
                return null;
            }
            
            // Create user object
            User user = new User(userId, username, email, password);
            
            // Set experience and level
            if (userData.get("experiencePoint") != null) {
                Object xpObj = userData.get("experiencePoint");
                int experiencePoint = (xpObj instanceof Integer) ? (Integer) xpObj : 
                                     (xpObj instanceof Long) ? ((Long) xpObj).intValue() : 0;
                user.setExperiencePoint(experiencePoint);
            }
            
            if (userData.get("userLevel") != null) {
                Object levelObj = userData.get("userLevel");
                int userLevel = (levelObj instanceof Integer) ? (Integer) levelObj : 
                               (levelObj instanceof Long) ? ((Long) levelObj).intValue() : 1;
                user.setUserLevel(userLevel);
            }
            
            // Set unlocked skills
            @SuppressWarnings("unchecked")
            List<String> unlockedSkills = (List<String>) userData.get("unlockedSkills");
            if (unlockedSkills != null) {
                user.setUnlockedSkills(new ArrayList<>(unlockedSkills));
            }
            
            // Set achievements
            @SuppressWarnings("unchecked")
            List<String> achievements = (List<String>) userData.get("achievements");
            if (achievements != null) {
                user.setAchievements(new ArrayList<>(achievements));
            }
            
            return user;
            
        } catch (Exception e) {
            System.err.println("Error creating user from data: " + e.getMessage());
            return null;
        }
    }

    /**
     * Refresh users from database (useful for syncing)
     */
    public void refreshUsersFromDatabase() {
        users.clear();
        loadUsersFromDatabase();
        
        // Add demo users if no users found
        if (users.isEmpty()) {
            users.add(new User("1", "admin", "admin@codevia.com", "admin123"));
            users.add(new User("2", "student", "student@codevia.com", "student123"));
        }
    }

    /**
     * Get all users (for admin purposes)
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Get user count
     */
    public int getUserCount() {
        return users.size();
    }
}
