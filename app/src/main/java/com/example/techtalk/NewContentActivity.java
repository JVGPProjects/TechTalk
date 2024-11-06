package com.example.techtalk;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class NewContentActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView contentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        contentTextView = findViewById(R.id.contentTextView);

        // Set up the swipe-to-refresh functionality
        swipeRefreshLayout.setOnRefreshListener(() -> reloadContent());

        // Optionally, you can trigger an initial content load
        reloadContent();
    }

    private void reloadContent() {
        // Simulate loading delay (e.g., network request or data fetching)
        new Handler().postDelayed(() -> {
            // Update your content here, for example:
            contentTextView.setText("New content loaded!");

            // Stop the refresh animation
            swipeRefreshLayout.setRefreshing(false);
        }, 2000); // Adjust delay time as needed
    }

    // Optional: Back button functionality if needed
    public void onBackButtonClick(View view) {
        finish(); // Close the current activity and return to the previous one
    }
}
