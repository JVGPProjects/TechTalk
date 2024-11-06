package com.example.techtalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;

public class PopupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the event details from the intent
        Intent intent = getIntent();
        String eventName = intent.getStringExtra("event_name");
        String eventLocation = intent.getStringExtra("event_location");

        // Show the dialog with event details
        showEventDetailsDialog(eventName, eventLocation);
    }

    private void showEventDetailsDialog(String eventName, String eventLocation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Event Reminder");

        // Create a layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_event_details, null);
        builder.setView(dialogView);

        TextView eventDetailsTextView = dialogView.findViewById(R.id.eventDetailsTextView);
        Button okButton = dialogView.findViewById(R.id.okButton);

        // Set event details
        if (eventName != null && eventLocation != null) {
            eventDetailsTextView.setText(String.format("Event: %s\nLocation: %s", eventName, eventLocation));
        }

        // Set OK button listener
        okButton.setOnClickListener(v -> finish()); // Close the dialog

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
