package com.example.techtalk;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ParentChat extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String parentId;
    private EditText messageEditText;
    private ImageButton sendButton;
    private LinearLayout chatContainer;
    private String therapistId;
    private List<String> chatMessages;
    private TextView parentNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        therapistId = mAuth.getCurrentUser().getUid();
        parentId = getIntent().getStringExtra("parentId");

        // Initialize UI components
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        chatContainer = findViewById(R.id.chatContainer);
        parentNameTextView = findViewById(R.id.parentNameTextView);

        chatMessages = new ArrayList<>();

        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString();
            if (!message.isEmpty()) {
                sendMessage(message);
            } else {
                Toast.makeText(this, "Please type a message or report", Toast.LENGTH_SHORT).show();
            }
        });

        loadParentEmail();
        loadChatMessages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChatMessages();
    }

    private void loadParentEmail() {
        db.collection("users").document(parentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String parentEmail = documentSnapshot.getString("email");
                        parentNameTextView.setText(parentEmail != null ? parentEmail : "Unknown Parent");
                    } else {
                        parentNameTextView.setText("No Parent Found");
                    }
                })
                .addOnFailureListener(e -> Log.e("ChatActivity", "Error loading parent email: " + e.getMessage()));
    }

    private void loadChatMessages() {
        db.collection("reports")
                .whereEqualTo("therapistId", therapistId)
                .whereEqualTo("parentId", parentId)
                .orderBy("timestamp")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        chatContainer.removeAllViews(); // Clear previous messages
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String message = document.getString("message");
                            long timestamp = document.getLong("timestamp");
                            addChatBubble(message, timestamp);
                        }
                    } else {
                        Toast.makeText(ParentChat.this, "Error loading messages: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendMessage(String message) {
        String therapistEmail = mAuth.getCurrentUser().getEmail();
        Report report = new Report(parentId, therapistEmail, message, System.currentTimeMillis());

        db.collection("reports").add(report).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addChatBubble(message, System.currentTimeMillis());
                messageEditText.setText(""); // Clear input field
            } else {
                Toast.makeText(ParentChat.this, "Error sending message: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addChatBubble(String message, long timestamp) {
        // Inflate the chat bubble layout
        View chatBubbleView = LayoutInflater.from(this).inflate(R.layout.bubble_chat_item, chatContainer, false);

        // Get references to the TextViews
        TextView messageTextView = chatBubbleView.findViewById(R.id.messageTextView);
        TextView timestampTextView = chatBubbleView.findViewById(R.id.timestampTextView);

        // Set the message and timestamp
        messageTextView.setText(message);
        timestampTextView.setText(formatTimestamp(timestamp));

        // Add the chat bubble to the chat container
        chatContainer.addView(chatBubbleView);
    }

    private String formatTimestamp(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        java.util.Date date = new java.util.Date(timestamp);
        return sdf.format(date);
    }
}
