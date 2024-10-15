package com.hien.doctruyen.item;

public class Chapter {
    private String title;           // Tiêu đề chương
    private String content;         // Nội dung chương
    private int numberOfWords;      // Số từ của chương

    // Constructor trống cần thiết cho Firebase
    public Chapter() {
    }

    // Constructor đầy đủ
    public Chapter(String title, String content, int numberOfWords) {
        this.title = title;
        this.content = content;
        this.numberOfWords = numberOfWords;
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
}
