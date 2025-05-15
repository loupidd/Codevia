package model;

public class User {
    //Private Class Declaration
    private String username;
    private String password;
    private int experiencePoint;
    private int userLevel;

    //Public Encapsulation
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.experiencePoint = 0;
        this.userLevel = 1;
    }

    // Class - Get Method
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getExperiencePoint() {
        return experiencePoint;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void gainExperiencePoint(int amount){
        this.experiencePoint += amount;
        if (experiencePoint >= userLevel * 100) {
            experiencePoint =0;
            userLevel++;
            System.out.println("You Have Leveled Up! You Are Now level " + userLevel);
        }
    }
    private UserAchievementTracker achievementTracker;

    public void setAchievementTracker(UserAchievementTracker tracker) {
        this.achievementTracker = tracker;
    }
}
