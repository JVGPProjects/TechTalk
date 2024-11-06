package com.example.techtalk;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ReportTest {

    private Report report;

    @Before
    public void setUp() {
        // Initialize a Report object before each test
        report = new Report("parent123", "therapist456", "This is a test message.", 1234567890L);
    }

    @Test
    public void testConstructor() {
        // Verify that the constructor initializes fields correctly
        assertEquals("parent123", report.getParentId());
        assertEquals("therapist456", report.getTherapistId());
        assertEquals("This is a test message.", report.getMessage());
        assertEquals(1234567890L, report.getTimestamp());
    }

    @Test
    public void testSettersAndGetters() {
        // Test setting and getting parentId
        report.setParentId("newParentId");
        assertEquals("newParentId", report.getParentId());

        // Test setting and getting therapistId
        report.setTherapistId("newTherapistId");
        assertEquals("newTherapistId", report.getTherapistId());

        // Test setting and getting message
        report.setMessage("Updated message");
        assertEquals("Updated message", report.getMessage());

        // Test setting and getting timestamp
        report.setTimestamp(9876543210L);
        assertEquals(9876543210L, report.getTimestamp());
    }
}
