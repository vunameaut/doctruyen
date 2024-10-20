package com.hien.doctruyen.item;

import java.io.Serializable;

public class Comment implements Serializable {
    private String id;
    private String storyId;  // ID của truyện mà bình luận thuộc về
    private String userId;   // ID của người dùng bình luận
    private String username; // Tên người dùng
    private String avatarUrl; // URL ảnh đại diện của người dùng
    private String content;  // Nội dung bình luận
    private long timestamp;  // Thời gian bình luận

    // Constructor mặc định
    public Comment() {
    }

    // Constructor đầy đủ
    public Comment(String id, String storyId, String userId, String username, String avatarUrl, String content, long timestamp) {
        this.id = id;
        this.storyId = storyId;
        this.userId = userId;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Constructor đơn giản với 3 tham số (username, content, avatarUrl)
    public Comment(String username, String content, String avatarUrl) {
        this.username = username;
        this.content = content;
        this.avatarUrl = avatarUrl;
        this.timestamp = System.currentTimeMillis(); // Gán thời gian hiện tại
    }

    // Getter và Setter cho các thuộc tính
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
