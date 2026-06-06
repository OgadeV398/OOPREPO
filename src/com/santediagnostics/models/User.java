package com.santediagnostics.models;

import java.time.LocalDateTime;

public class User {

    private int id;
    private String fullName;
    private String email;
    private String passwordHash;
    private String role;
    private boolean isFirstLogin;
    private boolean isVerified;
    private LocalDateTime createdAt;

    public User() {}

    public User(int id, String fullName, String email, String passwordHash,
                String role, boolean isFirstLogin, boolean isVerified, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isFirstLogin = isFirstLogin;
        this.isVerified = isVerified;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isFirstLogin() { return isFirstLogin; }
    public void setFirstLogin(boolean isFirstLogin) { this.isFirstLogin = isFirstLogin; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean isVerified) { this.isVerified = isVerified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{id=" + id + ", fullName=" + fullName + ", role=" + role + "}";
    }
}