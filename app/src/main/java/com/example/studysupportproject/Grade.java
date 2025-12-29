package com.example.studysupportproject;

import java.io.Serializable;

public class Grade implements Serializable {
    private int id;
    private int studentId;
    private int classId;
    private String subjectName;
    private double gradeValue;
    private String gradeType; // midterm, final, etc.
    private int semesterId;
    private String semesterName;
    private String schoolYear;
    private int teacherId;
    private String notes;
    private String createdAt;
    private String updatedAt;

    // Constructor
    public Grade() {
    }

    public Grade(int id, int studentId, int classId, String subjectName,
                 double gradeValue, String gradeType, int semesterId,
                 String semesterName, String schoolYear, int teacherId,
                 String notes, String createdAt, String updatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.classId = classId;
        this.subjectName = subjectName;
        this.gradeValue = gradeValue;
        this.gradeType = gradeType;
        this.semesterId = semesterId;
        this.semesterName = semesterName;
        this.schoolYear = schoolYear;
        this.teacherId = teacherId;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public double getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(double gradeValue) {
        this.gradeValue = gradeValue;
    }

    public String getGradeType() {
        return gradeType;
    }

    public void setGradeType(String gradeType) {
        this.gradeType = gradeType;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
