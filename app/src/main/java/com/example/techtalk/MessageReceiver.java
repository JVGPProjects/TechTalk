package com.example.techtalk;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MessageReceiver extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String parentId;
    private LinearLayout chatContainer;
    private List<String> chatMessages; // Store chat messages

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_receiver);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        parentId = getIntent().getStringExtra("parentId");

        // Initialize UI components
        chatContainer = findViewById(R.id.chatContainer);

        chatMessages = new ArrayList<>();

        // Set up real-time listener for therapist messages
        setupRealTimeListener();
    }

    private void setupRealTimeListener() {
        db.collection("reports")
                .whereEqualTo("parentId", parentId)
                .orderBy("timestamp")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("MessageReceiverActivity", "Error listening for messages: " + e.getMessage());
                        return;
                    }

                    chatContainer.removeAllViews(); // Clear the chat container
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot document : snapshots) {
                            String message = document.getString("message");
                            long timestamp = document.getLong("timestamp");
                            addChatBubble(message, timestamp);
                        }
                    }
                });
    }

    private void addChatBubble(String message, long timestamp) {
        // Create a new LinearLayout for the chat bubble
        LinearLayout chatBubble = new LinearLayout(this);
        chatBubble.setOrientation(LinearLayout.VERTICAL);
        chatBubble.setPadding(16, 8, 16, 8);
        chatBubble.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Create a TextView for the message
        TextView messageTextView = new TextView(this);
        messageTextView.setText(message);
        messageTextView.setTextColor(Color.BLACK);
        messageTextView.setTextSize(15);

        // Set the drawable background for the message
        messageTextView.setBackgroundResource(R.drawable.chat_bubble);

        messageTextView.setPadding(40, 20, 40, 20);
        // Set margins for messageTextView
        LinearLayout.LayoutParams messageLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        messageLayoutParams.setMargins(0, 10, 0, 10);
        messageTextView.setLayoutParams(messageLayoutParams);

        // Create a TextView for the timestamp
        TextView timestampTextView = new TextView(this);
        String formattedTime = formatTimestamp(timestamp);
        timestampTextView.setText(formattedTime);
        timestampTextView.setTextColor(Color.GRAY);
        timestampTextView.setTextSize(10);

        // Add the message TextView to the chat bubble layout
        chatBubble.addView(messageTextView);
        // Add the timestamp TextView below the message
        chatBubble.addView(timestampTextView);
        // Add the chat bubble to the chat container
        chatContainer.addView(chatBubble);
    }

    private String formatTimestamp(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        java.util.Date date = new java.util.Date(timestamp);
        return sdf.format(date);
    }
}
