package com.example.techtalk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
public class ParentHomeActivity extends AppCompatActivity {
    private ImageButton logoButton;
    private CalendarView calendarView;
    private ImageButton addEventButton;
    private Button plannerButton;
    private BottomNavigationView bottomNavigationView;
    private long selectedDate;
    private int selectedHour = -1;
    private int selectedMinute = -1;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView welcomeMessage;
    private SwipeRefreshLayout swipeRefreshLayout;

    // In-memory storage for events
    private HashMap<Long, List<CalendarEvent>> eventsMap = new HashMap<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // Initialize Firebase
        setContentView(R.layout.activity_parent_home);

        // Initialize views
        welcomeMessage = findViewById(R.id.welcomeText);
        logoButton = findViewById(R.id.logoButton);
        mAuth = FirebaseAuth.getInstance();
        calendarView = findViewById(R.id.calendarView);
        addEventButton = findViewById(R.id.addEventButton);
        db = FirebaseFirestore.getInstance();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        plannerButton = findViewById(R.id.plannerButton);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Initially hide the Add Event button
        addEventButton.setVisibility(View.GONE);
        // Check and request permissions


        // Set click listeners for buttons7
        logoButton.setOnClickListener(v -> {
            // Create and show the tooltip
            Toast.makeText(ParentHomeActivity.this, "TechTalk", Toast.LENGTH_SHORT).show();
        });

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate = calendar.getTimeInMillis();
            addEventButton.setVisibility(View.VISIBLE);
        });

        // Add event button listener
        addEventButton.setOnClickListener(v -> {
            if (selectedDate != 0) {
                showAddEventDialog(null);
            } else {
                Toast.makeText(ParentHomeActivity.this, "Please select a date first", Toast.LENGTH_SHORT).show();
            }
        });

        // Planner button listener
        plannerButton.setOnClickListener(v -> {
            Intent intent = new Intent(ParentHomeActivity.this, PlannerActivity.class);
            startActivity(intent);
        });

        // Clear any selected items in the navigation view
        bottomNavigationView.setSelectedItemId(R.id.navigation_home); // This will deselect any selected item

        // BottomNavigationView item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_Reports) {
                startActivity(new Intent(ParentHomeActivity.this, ParentChatActivity.class));
                return true;
            } else if (id == R.id.navigation_home) {
                startActivity(new Intent(ParentHomeActivity.this, WelcomeActivity.class));
                return true; // Already on the home page
            } else if (id == R.id.navigation_settings) {
                startActivity(new Intent(ParentHomeActivity.this, SettingsActivity.class));
                return true;
            } else {
                return false;
            }
        });

        // Set up swipe-to-refresh behavior
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Reload data or refresh UI
            fetchUserData();  // Or any other refresh actions
            swipeRefreshLayout.setRefreshing(false); // Stop the refresh indicator
        });


        fetchUserData();
    }

    private void fetchUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String username = document.getString("username");
                    welcomeMessage.setText("Welcome, " + username + "!");
                } else {
                    Toast.makeText(ParentHomeActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ParentHomeActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {

            }
        }
    }

    private void showAddEventDialog(final CalendarEvent eventToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(eventToEdit == null ? "Add Event" : "Edit Event");

        // Create a linear layout to hold the input fields
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_event_calendar, null);
        builder.setView(dialogView);

        // Set up input fields
        final EditText eventNameInput = dialogView.findViewById(R.id.eventNameInput);
        final EditText locationInput = dialogView.findViewById(R.id.locationInput);
        final Button timePickerButton = dialogView.findViewById(R.id.timePickerButton);

        if (eventToEdit != null) {
            eventNameInput.setText(eventToEdit.getEventName());
            locationInput.setText(eventToEdit.getLocation());
            selectedHour = eventToEdit.getHour();
            selectedMinute = eventToEdit.getMinute();
            timePickerButton.setText(String.format("Selected Time: %02d:%02d", selectedHour, selectedMinute));
        }

        // Set up the time picker dialog
        timePickerButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                selectedHour = hourOfDay;
                selectedMinute = minute;
                timePickerButton.setText(String.format("Selected Time: %02d:%02d", selectedHour, selectedMinute));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        // Set up the buttons
        builder.setPositiveButton(eventToEdit == null ? "Set Event" : "Update Event", (dialog, which) -> {
            String eventName = eventNameInput.getText().toString();
            String selectedLocation = locationInput.getText().toString();

            if (!eventName.isEmpty() && selectedHour != -1 && selectedMinute != -1 && !selectedLocation.isEmpty()) {
                Calendar eventCalendar = Calendar.getInstance();
                eventCalendar.setTimeInMillis(selectedDate);
                eventCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                eventCalendar.set(Calendar.MINUTE, selectedMinute);
                long eventTimeMillis = eventCalendar.getTimeInMillis();

                if (eventTimeMillis < System.currentTimeMillis()) {
                    Toast.makeText(ParentHomeActivity.this, "Event time must be in the future!", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // Check user here
                if (currentUser != null) {
                    if (eventToEdit == null) {
                        addEvent(eventName, eventTimeMillis, selectedLocation, selectedHour, selectedMinute, currentUser.getUid());
                    } else {
                        updateEvent(eventToEdit, eventName, eventTimeMillis, selectedLocation);
                    }
                } else {
                    Toast.makeText(ParentHomeActivity.this, "You must be logged in to add an event.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ParentHomeActivity.this, "Please, fill in all details", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addEvent(String eventName, long eventTime, String location, int hour, int minute, String userId) {
        Log.d("ParentHomeActivity", "Adding event: " + eventName + " at " + location);

        CalendarEvent event = new CalendarEvent(eventName, location, eventTime, hour, minute);
        event.setUserId(userId);

        // Check if the event time is in the future
        if (eventTime < System.currentTimeMillis()) {
            Toast.makeText(this, "Event must be scheduled in the future.", Toast.LENGTH_SHORT).show();
            return;
        }

        eventsMap.computeIfAbsent(selectedDate, k -> new ArrayList<>()).add(event);

        db.collection("events").add(event)
                .addOnSuccessListener(documentReference -> {
                    scheduleAlarm(event);
                    Toast.makeText(ParentHomeActivity.this, "Event added!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ParentHomeActivity.this, "Failed to add event!", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateEvent(CalendarEvent event, String newEventName, long newEventTime, String newLocation) {
        event.setEventName(newEventName);
        event.setLocation(newLocation);
        event.setTime(newEventTime);
        db.collection("events").document(event.getEventId()).set(event)
                .addOnSuccessListener(aVoid -> Toast.makeText(ParentHomeActivity.this, "Event updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ParentHomeActivity.this, "Error updating event", Toast.LENGTH_SHORT).show());
    }

    private void scheduleAlarm(CalendarEvent event) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("eventName", event.getEventName());
        intent.putExtra("eventLocation", event.getLocation());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) event.getTime(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, event.getTime(), pendingIntent);
        }
    }
}
