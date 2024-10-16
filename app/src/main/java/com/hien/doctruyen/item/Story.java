package com.hien.doctruyen.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Story {
    private String id;              // ID của truyện
    private String title;           // Tiêu đề truyện
    private String author;          // Tác giả truyện
    private String description;     // Mô tả truyện
    private List<String> genres;    // Danh sách các thể loại truyện
    private String imageUrl;        // URL của ảnh bìa
    private Map<String, Chapter> chapters;  // Danh sách các chương (Map với key là ID chương)
    private String uid;             // ID người dùng đăng truyện

    // Constructor trống cần thiết cho Firebase
    public Story() {
    }

    // Constructor đầy đủ
    public Story(String id, String title, String author, String description, List<String> genres,
                 String imageUrl, Map<String, Chapter> chapters, String uid) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.genres = genres;
        this.imageUrl = imageUrl;
        this.chapters = chapters;
        this.uid = uid;
    }

    // Getter và Setter cho các thuộc tính
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Kiểm tra và chuyển đổi genres nếu cần
    public List<String> getGenres() {
        if (genres == null) {
            genres = new ArrayList<>();
        }
        return genres;
    }

    public void setGenres(Object genres) {
        // Nếu dữ liệu là String, chuyển nó thành List<String>
        if (genres instanceof String) {
            List<String> genreList = new ArrayList<>();
            genreList.add((String) genres);
            this.genres = genreList;
        } else if (genres instanceof List) {
            // Nếu đã là List<String>, gán trực tiếp
            this.genres = (List<String>) genres;
        } else {
            this.genres = new ArrayList<>();
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Map<String, Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(Map<String, Chapter> chapters) {
        this.chapters = chapters;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}