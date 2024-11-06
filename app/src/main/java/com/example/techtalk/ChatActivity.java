package com.example.techtalk;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String parentId;
    private EditText messageEditText;
    private ImageButton sendButton;
    private LinearLayout chatContainer; // Changed to LinearLayout
    String therapistId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private List<String> chatMessages; // Store chat messages
    private TextView parentNameTextView; // Reference for the parent's email TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        parentId = getIntent().getStringExtra("parentId");

        // Initialize UI components
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        chatContainer = findViewById(R.id.chatContainer); // Reference to LinearLayout
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

        loadParentEmail(); // Load the parent's email
        loadChatMessages(); // Load existing messages if any
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTherapistMessages(); // Load therapist's messages when activity resumes
    }

    // Back button click handler
    public void onBackButtonClick(View view) {
        // Close the current activity and return to the previous one
        onBackPressed();
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
                .addOnFailureListener(e -> {
                    Log.e("ChatActivity", "Error loading parent email: " + e.getMessage());
                });
    }

    private void loadChatMessages() {
        db.collection("reports")
                .whereEqualTo("therapistId", therapistId)
                .whereEqualTo("parentId", parentId) // Load messages for the selected parent
                .orderBy("timestamp") // Ensure correct ordering
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String message = document.getString("message");
                                long timestamp = document.getLong("timestamp"); // Get the timestamp
                                addChatBubble(message, false, timestamp); // Add parent report messages
                            }
                        } else {
                            Toast.makeText(ChatActivity.this, "Error loading messages: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void sendMessage(String message) {
        // Fetch the therapist's email from the FirebaseUser
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String therapistEmail = currentUser.getEmail(); // Get the therapist's email

        Report report = new Report(parentId, therapistEmail, message, System.currentTimeMillis());

        db.collection("reports").add(report).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    // Display therapist email instead of therapist ID
                    addChatBubble(message, true, System.currentTimeMillis()); // Add therapist message
                    messageEditText.setText(""); // Clear input
                } else {
                    Toast.makeText(ChatActivity.this, "Error sending message: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadTherapistMessages() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String therapistEmail = currentUser.getEmail(); // Get the therapist's email

        db.collection("reports")
                .whereEqualTo("therapistId", therapistEmail) // Query messages using the therapist's email
                .whereEqualTo("parentId", parentId) // Filter by parent ID
                .orderBy("timestamp") // Ensure messages are ordered by time
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String message = document.getString("message");
                            long timestamp = document.getLong("timestamp"); // Get the timestamp
                            addChatBubble(message, false, timestamp); // Add therapist message
                        }
                    } else {
                        Log.e("ChatActivity", "Error loading therapist messages: " + task.getException().getMessage());
                    }
                });
    }

    private void addChatBubble(String message, boolean isTherapist, long timestamp) {
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

        // Set the drawable background based on whether the message is from the therapist or parent
        int backgroundResource = isTherapist ? R.drawable.chat_bubble : R.drawable.chat_bubble; // Use the same background for both for this example
        messageTextView.setBackgroundResource(backgroundResource);

        messageTextView.setPadding(40, 20, 40, 20);
        // Set margins for messageTextView
        LinearLayout.LayoutParams messageLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        messageLayoutParams.setMargins(0, 10, 0, 10); // Set left, top, right, bottom margins
        messageTextView.setLayoutParams(messageLayoutParams);

        // Create a TextView for the timestamp
        TextView timestampTextView = new TextView(this);
        String formattedTime = formatTimestamp(timestamp); // Format the timestamp
        timestampTextView.setText(formattedTime);
        timestampTextView.setTextColor(Color.GRAY); // Set a color for the timestamp
        timestampTextView.setTextSize(10); // Set a smaller text size for the timestamp
        timestampTextView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Set the layout parameters for the chat bubble
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) chatBubble.getLayoutParams();
        params.gravity = isTherapist ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START;
        chatBubble.setLayoutParams(params);

        // Add the message TextView to the chat bubble layout
        chatBubble.addView(messageTextView);
        // Add the timestamp TextView below the message
        chatBubble.addView(timestampTextView);
        // Add the chat bubble to the chat container
        chatContainer.addView(chatBubble);
    }

    private String formatTimestamp(long timestamp) {
        // Create a SimpleDateFormat instance to format the timestamp
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        java.util.Date date = new java.util.Date(timestamp);
        return sdf.format(date);
    }
}
