package com.finance.app.repository;

import com.finance.app.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Repository for managing user data with file-based persistence
 */
public class UserRepository {
    private static final String DATA_FILE = "users.json";
    private Map<String, User> users;
    private Gson gson;
    
    public UserRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.users = new HashMap<>();
        loadData();
    }
    
    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type userType = new TypeToken<Map<String, User>>() {}.getType();
                users = gson.fromJson(reader, userType);
                if (users == null) {
                    users = new HashMap<>();
                }
            } catch (IOException e) {
                System.err.println("Error loading user data: " + e.getMessage());
                users = new HashMap<>();
            }
        }
    }
    
    private void saveData() {
        try (Writer writer = new FileWriter(DATA_FILE)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }
    
    public boolean register(User user) {
        if (users.containsKey(user.getUsername())) {
            return false; // Username already exists
        }
        users.put(user.getUsername(), user);
        saveData();
        return true;
    }
    
    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
    public boolean updateUser(User user) {
        if (!users.containsKey(user.getUsername())) {
            return false;
        }
        users.put(user.getUsername(), user);
        saveData();
        return true;
    }
    
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            users.put(username, user);
            saveData();
            return true;
        }
        return false;
    }
    
    public User getUser(String username) {
        return users.get(username);
    }
    
    public boolean userExists(String username) {
        return users.containsKey(username);
    }
}
