package com.example.techtalk;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ChatMessageTest {

    @Test
    public void testGetMessage() {
        // Arrange
        String testMessage = "Hello, world!";
        long testTimestamp = 1625254800000L; // Example timestamp
        ChatMessage chatMessage = new ChatMessage(testMessage, testTimestamp);

        // Act & Assert
        assertEquals("Hello, world!", chatMessage.getMessage());
    }

    @Test
    public void testGetTimestamp() {
        // Arrange
        String testMessage = "Hello, world!";
        long testTimestamp = 1625254800000L; // Example timestamp
        ChatMessage chatMessage = new ChatMessage(testMessage, testTimestamp);

        // Act & Assert
        assertEquals(1625254800000L, chatMessage.getTimestamp());
    }
}
