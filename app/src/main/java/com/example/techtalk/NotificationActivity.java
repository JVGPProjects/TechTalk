package com.example.techtalk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {

    private TextView eventDetailsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        eventDetailsTextView = findViewById(R.id.eventDetailsTextView);

        // Get the event details from the intent
        Intent intent = getIntent();
        String eventName = intent.getStringExtra("event_name");
        String eventLocation = intent.getStringExtra("event_location");

        // Display the event details
        if (eventName != null && eventLocation != null) {
            String eventDetails = String.format("Event: %s\n\nLocation: %s", eventName, eventLocation);
            eventDetailsTextView.setText(eventDetails);
        }

    }
}
