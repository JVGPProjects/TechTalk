package com.example.techtalk;

public class ChatMessage {
    private String message;
    private long timestamp;

    public ChatMessage(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
