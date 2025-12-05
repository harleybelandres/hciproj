package com.example.elderlycare.model;

import com.google.firebase.Timestamp;

public class MedicalUpdate {
    private String updateId;
    private String patientId;
    private String authorId;
    private String title;
    private String message;
    private Timestamp timestamp;
    private String type;

    public MedicalUpdate() {}

    public MedicalUpdate(String updateId, String patientId, String authorId, String title, String message, Timestamp timestamp, String type) {
        this.updateId = updateId;
        this.patientId = patientId;
        this.authorId = authorId;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }

    // Getters and setters
    public String getUpdateId() { return updateId; }
    public void setUpdateId(String updateId) { this.updateId = updateId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
