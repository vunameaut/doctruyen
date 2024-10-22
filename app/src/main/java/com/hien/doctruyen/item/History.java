package com.hien.doctruyen.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class History implements Serializable {
    private String id;
    private String title;
    private String imageUrl;
    private Long currentChapter;  // Chap đang đọc
    private Long latestChapter;   // Chap mới nhất

    public History(String id, String title, String author, String description, List<String> genres, String imageUrl, Map<String, Chapter> chapters, Long currentChapter, Long latestChapter) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.currentChapter = currentChapter;
        this.latestChapter = latestChapter;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
    public Long getCurrentChapter() { return currentChapter; }
    public Long getLatestChapter() { return latestChapter; }
}
