package com.example.techtalk;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TherapistChatActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String parentId;
    private EditText messageEditText;
    private ImageButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapist_chat);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        parentId = getIntent().getStringExtra("parentId");

        // Initialize UI components
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString();
            if (!message.isEmpty()) {
                sendReportToParent(message);
            } else {
                Toast.makeText(TherapistChatActivity.this, "Please type a message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendReportToParent(String message) {
        String therapistId = mAuth.getCurrentUser().getUid();

        Map<String, Object> report = new HashMap<>();
        report.put("parentId", parentId);
        report.put("therapistId", therapistId);
        report.put("message", message);
        report.put("timestamp", System.currentTimeMillis());

        db.collection("reports")
                .add(report)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(TherapistChatActivity.this, "Report sent successfully", Toast.LENGTH_SHORT).show();
                    messageEditText.setText(""); // Clear the input field
                })
                .addOnFailureListener(e -> {
                    Log.e("TherapistChatActivity", "Error sending report: ", e);
                    Toast.makeText(TherapistChatActivity.this, "Failed to send report", Toast.LENGTH_SHORT).show();
                });
    }
}
