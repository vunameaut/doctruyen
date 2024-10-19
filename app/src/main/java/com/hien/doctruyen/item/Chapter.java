package com.hien.doctruyen.item;

public class Chapter {
    private String title;           // Tiêu đề chương
    private String content;         // Nội dung chương
    private int numberOfWords;      // Số từ của chương
    private String author;          // Tác giả của chương
    private String genre;           // Thể loại chương
    private String chapterId;       // ID của chương
    private String storyId;         // ID của truyện
    private String uid;             // UID của chương

    // Constructor trống cần thiết cho Firebase
    public Chapter() {
    }

    // Constructor đầy đủ với 7 tham số
    public Chapter(String title, String content, int numberOfWords, String author, String genre, String chapterId, String storyId) {
        this.title = title;
        this.content = content;
        this.numberOfWords = numberOfWords;
        this.author = author;
        this.genre = genre;
        this.chapterId = chapterId;
        this.storyId = storyId;
    }

    // Constructor với 6 tham số (không có numberOfWords)
    public Chapter(String title, String content, String author, String genre, String chapterId, String storyId) {
        this.title = title;
        this.content = content;
        this.numberOfWords = 0; // Có thể đặt mặc định là 0 nếu không cần số từ
        this.author = author;
        this.genre = genre;
        this.chapterId = chapterId;
        this.storyId = storyId;
    }

    // Constructor mới với 4 tham số
    public Chapter(String title, String content, String chapterId, String storyId) {
        this.title = title;
        this.content = content;
        this.chapterId = chapterId;
        this.storyId = storyId;
        this.numberOfWords = 0;  // Đặt mặc định là 0
        this.author = "";        // Đặt mặc định là rỗng
        this.genre = "";         // Đặt mặc định là rỗng
    }

    // Getter và Setter cho các thuộc tính
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNumberOfWords() {
        return numberOfWords;
    }

    public void setNumberOfWords(int numberOfWords) {
        this.numberOfWords = numberOfWords;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    // Getter và Setter cho thuộc tính uid
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
