package models;

import enums.UserRole;

public class User {
    private String userId;
    private String username;
    private String password;
    private String name;
    private String email;
    private UserRole role; // Use enum for role


    public User(String userId, String username, String password, String name, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = UserRole.STUDENT; // Default role
    }

    public User(String userId, String username, String password, String name, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = UserRole.valueOf(role);
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = UserRole.valueOf(role);
    }
}