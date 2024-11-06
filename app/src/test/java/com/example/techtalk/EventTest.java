package com.example.techtalk;

import junit.framework.TestCase;
import java.util.Map;

public class EventTest extends TestCase {

    public void testEventConstructorWithUserId() {
        String eventName = "Tech Talk";
        String location = "Main Hall";
        long eventTime = 1678901234567L;
        String userId = "user123";

        Event event = new Event(eventName, location, eventTime, userId);

        assertEquals("Tech Talk", event.getEventName());
        assertEquals("Main Hall", event.getLocation());
        assertEquals(1678901234567L, event.getEventTime());
        assertEquals("user123", event.getUserId());
    }

    public void testEventConstructorWithoutUserId() {
        String eventName = "Code Workshop";
        String location = "Room 101";
        long eventTime = 1678901234567L;

        Event event = new Event(eventName, location, eventTime);

        assertEquals("Code Workshop", event.getEventName());
        assertEquals("Room 101", event.getLocation());
        assertEquals(1678901234567L, event.getEventTime());
        assertNull(event.getUserId()); // User ID should be null
    }

    public void testEventConstructorWithAllFields() {
        String title = "Hackathon";
        String date = "2024-11-05";
        String description = "A 24-hour coding event";
        long eventTime = 1678901234567L;
        String eventName = "Hackathon";
        String location = "Tech Center";

        Event event = new Event(title, date, description, eventTime, eventName, location);

        assertEquals("Hackathon", event.getTitle());
        assertEquals("2024-11-05", event.getDate());
        assertEquals("A 24-hour coding event", event.getDescription());
        assertEquals(1678901234567L, event.getEventTime());
        assertEquals("Hackathon", event.getEventName());
        assertEquals("Tech Center", event.getLocation());
    }

    public void testSettersAndGetters() {
        Event event = new Event();

        event.setTitle("Seminar");
        event.setDate("2024-12-10");
        event.setDescription("Tech seminar on AI");
        event.setEventTime(1678901234567L);
        event.setEventName("AI Seminar");
        event.setLocation("Conference Room");
        event.setUserId("user456");

        assertEquals("Seminar", event.getTitle());
        assertEquals("2024-12-10", event.getDate());
        assertEquals("Tech seminar on AI", event.getDescription());
        assertEquals(1678901234567L, event.getEventTime());
        assertEquals("AI Seminar", event.getEventName());
        assertEquals("Conference Room", event.getLocation());
        assertEquals("user456", event.getUserId());
    }

    public void testToMap() {
        Event event = new Event("Meeting", "2024-11-06", "Project meeting", 1678901234567L, "Project Meeting", "Board Room");
        event.setUserId("user789");

        Map<String, Object> map = event.toMap();

        assertEquals("Meeting", map.get("title"));
        assertEquals("2024-11-06", map.get("date"));
        assertEquals("Project meeting", map.get("description"));
        assertEquals(1678901234567L, map.get("eventTime"));
        assertEquals("Project Meeting", map.get("eventName"));
        assertEquals("Board Room", map.get("location"));
        assertEquals("user789", map.get("userId"));
    }
}
