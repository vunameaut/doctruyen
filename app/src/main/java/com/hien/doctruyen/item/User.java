package com.hien.doctruyen.item;

public class User {
    private String username;
    private String email;
    private String avatar; // URL của hình ảnh

    public User() {
        // Constructor mặc định cho Firebase
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
