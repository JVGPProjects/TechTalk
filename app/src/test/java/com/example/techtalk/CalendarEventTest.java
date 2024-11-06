package com.example.techtalk;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CalendarEventTest {

    private CalendarEvent calendarEvent;

    @Before
    public void setUp() {
        // Initialize the CalendarEvent object before each test
        calendarEvent = new CalendarEvent("Meeting", "Office", 1625254800000L, 14, 30);
    }

    @Test
    public void testEventName() {
        // Test getter and setter for eventName
        assertEquals("Meeting", calendarEvent.getEventName());
        calendarEvent.setEventName("Conference");
        assertEquals("Conference", calendarEvent.getEventName());
    }

    @Test
    public void testLocation() {
        // Test getter and setter for location
        assertEquals("Office", calendarEvent.getLocation());
        calendarEvent.setLocation("Home");
        assertEquals("Home", calendarEvent.getLocation());
    }

    @Test
    public void testEventTime() {
        // Test getter and setter for eventTime
        assertEquals(1625254800000L, calendarEvent.getEventTime());
        calendarEvent.setEventTime(1625341200000L);
        assertEquals(1625341200000L, calendarEvent.getEventTime());
    }

    @Test
    public void testHourAndMinute() {
        // Test getter and setter for hour and minute
        assertEquals(14, calendarEvent.getHour());
        assertEquals(30, calendarEvent.getMinute());
        calendarEvent.setHour(16);
        calendarEvent.setMinute(45);
        assertEquals(16, calendarEvent.getHour());
        assertEquals(45, calendarEvent.getMinute());
    }

    @Test
    public void testUserId() {
        // Test getter and setter for userId
        assertNull(calendarEvent.getUserId()); // Initially, userId should be null
        calendarEvent.setUserId("user123");
        assertEquals("user123", calendarEvent.getUserId());
    }

    @Test
    public void testDocumentId() {
        // Test getter and setter for documentId
        assertNull(calendarEvent.getDocumentId()); // Initially, documentId should be null
        calendarEvent.setDocumentId("doc123");
        assertEquals("doc123", calendarEvent.getDocumentId());
    }

    @Test
    public void testToMap() {
        // Test the toMap method
        calendarEvent.setUserId("user123");
        HashMap<String, Object> map = calendarEvent.toMap();
        assertNotNull(map);
        assertEquals("Meeting", map.get("eventName"));
        assertEquals("Office", map.get("location"));
        assertEquals(1625254800000L, map.get("eventTime"));
        assertEquals(14, map.get("hour"));
        assertEquals(30, map.get("minute"));
        assertEquals("user123", map.get("userId"));
    }
}
