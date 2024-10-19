package com.hien.doctruyen.item;

public class User {
    private String uid;      // ID duy nhất của người dùng
    private String username; // Tên người dùng
    private String email;    // Email của người dùng
    private String avatar;   // URL của hình đại diện người dùng
    private boolean blocked; // Trạng thái tài khoản bị khóa
    private String role;     // Vai trò của người dùng (user, admin, v.v.)

    public User() {
        // Constructor mặc định cho Firebase
    }

    // Getter và Setter cho uid
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // Getter và Setter cho username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter và Setter cho email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter và Setter cho avatar
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    // Getter và Setter cho trạng thái blocked
    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    // Getter và Setter cho role
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
