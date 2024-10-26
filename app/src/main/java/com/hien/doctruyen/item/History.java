package com.hien.doctruyen.item;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class History implements Serializable {
    private String id;
    private String title;
    private String imageUrl;
    private Long currentChapter;  // Chap đang đọc
    private Long latestChapter;   // Chap mới nhất
    private Story story;          // Đối tượng Story

    public History(String id, String title, String imageUrl, Long currentChapter, Long latestChapter, Story story) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.currentChapter = currentChapter;
        this.latestChapter = latestChapter;
        this.story = story;
    }

    // Getters và setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
    public Long getCurrentChapter() { return currentChapter; }
    public Long getLatestChapter() { return latestChapter; }
    public Story getStory() { return story; }
    public void setStory(Story story) { this.story = story; }
}
