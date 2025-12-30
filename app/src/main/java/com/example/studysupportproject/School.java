package com.example.studysupportproject;

public class School {
    private int id;
    private String schoolName;

    public School() {
    }

    public School(int id, String schoolName) {
        this.id = id;
        this.schoolName = schoolName;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
}
