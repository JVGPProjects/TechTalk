package com.example.techtalk;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private String title;
    private String date;
    private String description;
    private String documentId; // Field to store the document ID
    private long eventTime; // Field to store event time
    private String eventName; // Field to store event name
    private String location; // Field to store location
    private String userId;


    public Event() {
        // Default constructor
    }

    // Constructor for saving event with user ID
    public Event(String eventName, String location, long selectedEventTime, String currentUserId) {
        this.eventName = eventName; // Assigning parameters to instance variables
        this.location = location;
        this.eventTime = selectedEventTime;
        this.userId = currentUserId; // Optionally store user ID if needed
    }

    // Constructor for storing event without user ID
    public Event(String eventName, String location, long selectedEventTime) {
        this.eventName = eventName;
        this.location = location;
        this.eventTime = selectedEventTime;
    }

    public Event(String title, String date, String description, long eventTime, String eventName, String location) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.eventTime = eventTime;
        this.eventName = eventName;
        this.location = location;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId; // Store the document ID
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("date", date);
        map.put("description", description);
        map.put("eventTime", eventTime);
        map.put("eventName", eventName);
        map.put("location", location);
        map.put("userId", userId);
        return map;
    }
}
