package com.example.studysupportproject;

import java.io.Serializable;

public class Schedule implements Serializable {
    private int id;
    private int userId;
    private String title;
    private String description;
    private String scheduleDate; // DATE format from database
    private String startTime;    // TIME format from database
    private String endTime;      // TIME format from database
    private String scheduleType; // Default: "personal"
    private String createdAt;

    // Constructor
    public Schedule() {
    }

    public Schedule(int id, int userId, String title, String description,
                    String scheduleDate, String startTime, String endTime,
                    String scheduleType, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.scheduleDate = scheduleDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scheduleType = scheduleType;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", scheduleDate='" + scheduleDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", scheduleType='" + scheduleType + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
