package com.example.studysupportproject;

public class Post {
    private int id;
    private String title;
    private String content;
    private int authorId;
    private String postType;
    private boolean isPublished;
    private String publishedAt;
    private String createdAt;
    private String updatedAt;

    public Post(int id, String title, String content, int authorId, String postType,
                boolean isPublished, String publishedAt, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.postType = postType;
        this.isPublished = isPublished;
        this.publishedAt = publishedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getAuthorId() { return authorId; }
    public String getPostType() { return postType; }
    public boolean isPublished() { return isPublished; }
    public String getPublishedAt() { return publishedAt; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}

