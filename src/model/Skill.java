package model;

public class Skill {
    private String id;
    private String name;
    private String description;
    private boolean unlocked;

    public Skill(String id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
        this.unlocked = false;
    }

    //Class - Get Method
    public String getName(){
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void unlock() {
        this.unlocked = true;
    }
}
