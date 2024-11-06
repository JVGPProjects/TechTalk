package com.example.techtalk;

import java.util.HashMap;

public class CalendarEvent {
    private String eventName;
    private String location;
    private long eventTime;
    private int hour;
    private int minute;
    private String userId; // Added to keep track of the user
    private String documentId; // To reference the Firestore document

    public CalendarEvent(String eventName, String location, long eventTime, int hour, int minute) {
        this.eventName = eventName;
        this.location = location;
        this.eventTime = eventTime;
        this.hour = hour; // Fixed to set hour correctly
        this.minute = minute; // Fixed to set minute correctly
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("eventName", eventName);
        map.put("location", location);
        map.put("eventTime", eventTime);
        map.put("hour", hour);
        map.put("minute", minute);
        map.put("userId", userId); // Store userId
        return map;
    }

    // Getters and Setters
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

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    // Set the event time
    public void setTime(long newEventTime) {
        this.eventTime = newEventTime;
    }

    // Get the event ID (document ID)
    public String getEventId() {
        return documentId; // Assuming documentId is the event ID
    }

    // Get the time for scheduling
    public long getTime() {
        return eventTime;
    }
}
