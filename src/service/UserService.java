package service;

import model.User;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    private Map<String, User> users = new HashMap<>();

    public boolean isUserRegistered(String username) {
        return users.containsKey(username);
    }

    public boolean isPasswordCorrect(String username, String password) {
        return users.containsKey(username) && users.get(username).getPassword().equals(password);
    }

    public boolean registerUser(String username, String password) {
        if (users.containsKey(username)) return false;
        users.put(username, new User(username, password));
        return true;
    }

    public User getUser(String username) {
        return users.get(username);
    }
}
