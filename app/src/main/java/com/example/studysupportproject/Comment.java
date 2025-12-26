package com.example.studysupportproject;

public class Comment {
    private int id;
    private int postId;
    private int commenterId;
    private String commenterUsername;
    private String commenterFullName;
    private String commenterAvatar;
    private String content;
    private String createdAt;
    private String updatedAt;

    // Constructor with commenter info
    public Comment(int id, int postId, int commenterId, String commenterUsername, String commenterFullName,
                   String commenterAvatar, String content, String createdAt, String updatedAt) {
        this.id = id;
        this.postId = postId;
        this.commenterId = commenterId;
        this.commenterUsername = commenterUsername;
        this.commenterFullName = commenterFullName;
        this.commenterAvatar = commenterAvatar;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Comment(int id, int postId, int commenterId, String content,
                   String createdAt, String updatedAt) {
        this(id, postId, commenterId, "", "", "", content, createdAt, updatedAt);
    }

    // Getters
    public int getId() { return id; }
    public int getPostId() { return postId; }
    public int getCommenterId() { return commenterId; }
    public String getCommenterUsername() { return commenterUsername; }
    public String getCommenterFullName() { return commenterFullName; }
    public String getCommenterAvatar() { return commenterAvatar; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}