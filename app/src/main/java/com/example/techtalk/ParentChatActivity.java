package com.example.techtalk;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class ParentChatActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private LinearLayout chatContainer;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView scrollView;
    private String parentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_chat);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        parentId = mAuth.getCurrentUser().getUid();

        chatContainer = findViewById(R.id.chatContainer);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        scrollView = findViewById(R.id.scrollView);

        // Load the initial chat messages
        loadReports();

        // Set up the swipe-to-refresh functionality
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadReports(); // Reload the chat messages
        });
    }

    private void loadReports() {
        db.collection("reports")
                .whereEqualTo("parentId", parentId)
                .orderBy("timestamp")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        chatContainer.removeAllViews();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String message = document.getString("message");
                            Long timestamp = document.getLong("timestamp");

                            if (message != null && timestamp != null) {
                                addChatBubble(message, timestamp);
                            } else {
                                Log.e("ParentChatActivity", "Document has null values: " + document.getId());
                            }
                        }
                        scrollToBottom();
                        swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation
                    } else {
                        Log.e("ParentChatActivity", "Error loading messages: ", task.getException());
                        Toast.makeText(ParentChatActivity.this, "Failed to load reports", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation in case of error
                    }
                });
    }

    // Back button click handler
    public void onBackButtonClick(View view) {
        onBackPressed();
    }

    private void addChatBubble(String message, long timestamp) {
        TextView chatBubble = new TextView(this);
        chatBubble.setText(message);
        chatBubble.setTextSize(16);
        chatBubble.setPadding(20, 10, 20, 10);

        LinearLayout.LayoutParams chatBubbleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        chatBubbleParams.setMargins(10, 10, 10, 0);
        chatBubble.setLayoutParams(chatBubbleParams);
        chatBubble.setBackgroundResource(R.drawable.chat_bubble_1);
        chatBubble.setTextColor(getResources().getColor(android.R.color.black));

        TextView timestampTextView = new TextView(this);
        timestampTextView.setText(formatTimestamp(timestamp));
        timestampTextView.setTextSize(12);
        timestampTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));

        LinearLayout.LayoutParams timestampParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        timestampParams.setMargins(10, 0, 10, 10);
        timestampTextView.setLayoutParams(timestampParams);

        chatContainer.addView(chatBubble);
        chatContainer.addView(timestampTextView);
    }

    private String formatTimestamp(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        java.util.Date date = new java.util.Date(timestamp);
        return sdf.format(date);
    }

    // Method to scroll to the bottom of the ScrollView
    private void scrollToBottom() {
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
}
