package com.example.techtalk;

public class Report {
    private String parentId;
    private String therapistId;
    private String message;
    private long timestamp; // Add timestamp field

    // Required empty constructor for Firestore
    public Report() {
        // Needed for Firestore
    }

    public Report(String parentId, String therapistId, String message, long timestamp) {
        this.parentId = parentId;
        this.therapistId = therapistId;
        this.message = message;
        this.timestamp = timestamp; // Initialize timestamp
    }

    // Getters and Setters
    public String getParentId() {
        return parentId;
    }

    public String getTherapistId() {
        return therapistId;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp; // Return the actual timestamp
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setTherapistId(String therapistId) {
        this.therapistId = therapistId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp; // Add setter for timestamp
    }
}
