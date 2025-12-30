package com.example.studysupportproject;

public class ClassItem {
    private int id;
    private int schoolId;
    private int semesterId;
    private String className;
    private int subjectId;
    private String subjectName;

    public ClassItem() {
    }

    public ClassItem(int id, int schoolId, int semesterId, String className) {
        this.id = id;
        this.schoolId = schoolId;
        this.semesterId = semesterId;
        this.className = className;
        this.subjectId = -1;
        this.subjectName = null;
    }

    public ClassItem(int id, int schoolId, int semesterId, String className, int subjectId, String subjectName) {
        this.id = id;
        this.schoolId = schoolId;
        this.semesterId = semesterId;
        this.className = className;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}

