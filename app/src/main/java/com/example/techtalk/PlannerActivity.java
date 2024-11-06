package com.example.techtalk;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PlannerActivity extends AppCompatActivity {

    private LinearLayout eventsContainer;
    private Map<Long, List<Event>> eventsMap;
    private FirebaseFirestore db;
    private long selectedEventTime;
    private Event eventToEdit; // Store the event to edit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_planner);
            // Initialize your views and data here
        } catch (Exception e) {
            Log.e("PlannerActivity", "Error initializing activity", e);
        }

        // Initialize views and Firestore
        eventsContainer = findViewById(R.id.eventsContainer);
        eventsMap = new HashMap<>();
        db = FirebaseFirestore.getInstance();

        ImageButton addEventButton = findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(v -> showAddEventDialog(null));

        // Load events from Firestore
        loadEventsFromFirestore();
    }

    // Back button click handler
    public void onBackButtonClick(View view) {
        // Close the current activity and return to the previous one
        onBackPressed();
    }

    private void loadEventsFromFirestore() {
        String currentUserId = getCurrentUserId(); // Implement this method to get the logged-in user's ID

        db.collection("events")
                .whereEqualTo("userId", currentUserId) // Filter events by userId
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("PlannerActivity", "Error loading events: ", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        eventsMap.clear(); // Clear the map to avoid duplication

                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Event event = doc.toObject(Event.class);
                            event.setDocumentId(doc.getId());

                            if (!eventsMap.containsKey(event.getEventTime())) {
                                eventsMap.put(event.getEventTime(), new ArrayList<>());
                            }
                            eventsMap.get(event.getEventTime()).add(event);
                        }

                        displayAllEvents(); // Refresh the display with updated events
                    }
                });
    }


    // Implement a method to get the current user's ID
    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null; // Return user ID or handle not logged in case
    }

    private void displayAllEvents() {
        eventsContainer.removeAllViews();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        int colorIndex = 0;

        for (long eventDate : eventsMap.keySet()) {
            for (Event event : eventsMap.get(eventDate)) {
                CardView cardView = new CardView(this);
                cardView.setCardElevation(8); // Optional: Add shadow effect
                cardView.setRadius(16); // Soft edges

                // Set LayoutParams with margins
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                cardParams.setMargins(0, 0, 0, 16); // Add space after each card
                cardView.setLayoutParams(cardParams);

                LinearLayout cardLayout = new LinearLayout(this);
                cardLayout.setOrientation(LinearLayout.VERTICAL);
                cardLayout.setPadding(12, 12, 12, 12);

                // Alternate background colors
                if (colorIndex % 2 == 0) {
                    cardLayout.setBackgroundColor(Color.parseColor("#DBF6BF"));// First color
                } else {
                    cardLayout.setBackgroundColor(Color.parseColor("#DBF6BF")); // Second color
                }
                colorIndex++;

                // Event Name TextView
                TextView eventNameView = new TextView(this);
                eventNameView.setText("Event: " + event.getEventName());
                eventNameView.setTextSize(16);
                eventNameView.setTextColor(getResources().getColor(android.R.color.black));
                eventNameView.setGravity(Gravity.START);
                eventNameView.setPadding(8, 8, 8, 8);

                // Event Location TextView
                TextView eventLocationView = new TextView(this);
                eventLocationView.setText("Location: " + event.getLocation());
                eventLocationView.setTextSize(14);
                eventLocationView.setTextColor(getResources().getColor(android.R.color.black));
                eventLocationView.setGravity(Gravity.START);
                eventLocationView.setPadding(8, 8, 8, 8);

                // Event Date TextView
                TextView eventDateView = new TextView(this);
                eventDateView.setText(dateFormat.format(event.getEventTime()));
                eventDateView.setTextSize(14);
                eventDateView.setTextColor(getResources().getColor(android.R.color.black));
                eventDateView.setGravity(Gravity.START);
                eventDateView.setPadding(8, 8, 8, 8);

                // Create a horizontal layout for buttons
                LinearLayout buttonLayout = new LinearLayout(this);
                buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
                buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                buttonLayout.setGravity(Gravity.CENTER); // Align buttons to the end

                // Edit and Delete buttons (initially hidden)
                Button editButton = new Button(this);
                editButton.setText("Edit");
                editButton.setVisibility(View.GONE); // Initially hidden

                Button deleteButton = new Button(this);
                deleteButton.setText("Delete");
                deleteButton.setVisibility(View.GONE); // Initially hidden

                // Set onClickListener to toggle button visibility
                cardLayout.setOnClickListener(v -> {
                    if (editButton.getVisibility() == View.VISIBLE) {
                        editButton.setVisibility(View.GONE);
                        deleteButton.setVisibility(View.GONE);
                    } else {
                        editButton.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.VISIBLE);
                    }
                });

                editButton.setOnClickListener(v -> showAddEventDialog(event));
                deleteButton.setOnClickListener(v -> deleteEvent(event));

                // Add buttons to the button layout
                buttonLayout.addView(editButton);
                buttonLayout.addView(deleteButton);

                // Add views to the card layout
                cardLayout.addView(eventNameView);
                cardLayout.addView(eventLocationView);
                cardLayout.addView(eventDateView);
                cardLayout.addView(buttonLayout); // Add the button layout here

                // Add card layout to card view
                cardView.addView(cardLayout);
                eventsContainer.addView(cardView);

                // Add empty TextView after each event
                TextView emptyTextView = new TextView(this);
                emptyTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                emptyTextView.setHeight(16); // Set height for spacing
                emptyTextView.setVisibility(View.GONE); // Set visibility to GONE (no text)

                // Add the empty TextView to the events container
                eventsContainer.addView(emptyTextView);
            }
        }
    }

    private void showAddEventDialog(Event event) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_event);

        EditText eventNameInput = dialog.findViewById(R.id.eventNameInput);
        EditText locationInput = dialog.findViewById(R.id.locationInput);
        Button datePickerButton = dialog.findViewById(R.id.datePickerButton);
        Button timePickerButton = dialog.findViewById(R.id.timePickerButton);
        Button saveButton = dialog.findViewById(R.id.saveButton);

        // If editing an event, populate the fields
        if (event != null) {
            eventNameInput.setText(event.getEventName());
            locationInput.setText(event.getLocation());
            selectedEventTime = event.getEventTime();
        }

        datePickerButton.setOnClickListener(v -> showDatePicker());
        timePickerButton.setOnClickListener(v -> showTimePicker());
        saveButton.setOnClickListener(v -> {
            String eventName = eventNameInput.getText().toString().trim();
            String location = locationInput.getText().toString().trim();

            if (TextUtils.isEmpty(eventName) || TextUtils.isEmpty(location)) {
                Toast.makeText(this, "Please, fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save or update the event
            if (event != null) {
                updateEvent(event, eventName, location, selectedEventTime);
            } else {
                saveEvent(eventName, location, selectedEventTime); // Ensure to pass selectedEventTime correctly
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void saveEvent(String eventName, String location, long selectedEventTime) {
        // Log event details for debugging
        Log.d("PlannerActivity", "Event Name: " + eventName);
        Log.d("PlannerActivity", "Location: " + location);
        Log.d("PlannerActivity", "Selected Event Time: " + selectedEventTime);
        Log.d("PlannerActivity", "User ID: " + getCurrentUserId());

        Event newEvent = new Event(eventName, location, this.selectedEventTime, getCurrentUserId());
        db.collection("events").add(newEvent)
                .addOnSuccessListener(documentReference -> {
                    newEvent.setDocumentId(documentReference.getId());
                    addNotification(newEvent); // Set notification after event is saved
                    Toast.makeText(this, "Event saved!", Toast.LENGTH_SHORT).show();
                    // Do not call displayAllEvents here; the real-time listener will handle the UI refresh
                })
                .addOnFailureListener(e -> Log.e("PlannerActivity", "Error saving event: " + e.getMessage()));
    }


    private void updateEvent(Event event, String eventName, String location, long eventTime) {
        Map<String, Object> updatedEvent = new HashMap<>();
        updatedEvent.put("eventName", eventName);
        updatedEvent.put("location", location);
        updatedEvent.put("eventTime", eventTime);

        db.collection("events").document(event.getDocumentId())
                .update(updatedEvent)
                .addOnSuccessListener(aVoid -> {
                    cancelNotification(event); // Cancel old notification
                    addNotification(event); // Add new notification after updating
                    Toast.makeText(this, "Event updated!", Toast.LENGTH_SHORT).show();
                    displayAllEvents();
                })
                .addOnFailureListener(e -> Log.e("PlannerActivity", "Error updating event: " + e.getMessage()));
    }

    private void deleteEvent(Event event) {
        db.collection("events").document(event.getDocumentId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    cancelNotification(event); // Cancel notification for the deleted event
                    Toast.makeText(this, "Event deleted!", Toast.LENGTH_SHORT).show();

                    // Remove the event from the local map and refresh the UI
                    List<Event> eventList = eventsMap.get(event.getEventTime());
                    if (eventList != null) {
                        eventList.remove(event);
                        if (eventList.isEmpty()) {
                            eventsMap.remove(event.getEventTime());
                        }
                    }
                    displayAllEvents();
                })
                .addOnFailureListener(e -> Log.e("PlannerActivity", "Error deleting event: " + e.getMessage()));
    }


    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    selectedEventTime = calendar.getTimeInMillis(); // Set selected event time
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    selectedEventTime = calendar.getTimeInMillis(); // Set selected event time
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }

    private void addNotification(Event event) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("eventName", event.getEventName());
        intent.putExtra("eventLocation", event.getLocation());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) event.getEventTime(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, event.getEventTime(), pendingIntent);
        }
    }

    private void cancelNotification(Event event) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) event.getEventTime(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
