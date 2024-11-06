package com.example.techtalk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Refresh extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout splashOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        splashOverlay = findViewById(R.id.splashOverlay);

        // Display the splash screen overlay initially
        showSplashScreen();

        // Set up the swipe-to-refresh functionality
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reload content when swipe-to-refresh is triggered
                reloadContent();
            }
        });

        // Simulate initial loading when the app starts
        reloadContent();
    }

    private void reloadContent() {
        // Show splash screen overlay while loading
        showSplashScreen();

        // Simulate loading delay (e.g., network request or data fetching)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Hide splash screen overlay and stop refresh animation
                hideSplashScreen();
                swipeRefreshLayout.setRefreshing(false);

                // Start the new activity to show refreshed content
                Intent intent = new Intent(Refresh.this, NewContentActivity.class);
                startActivity(intent);
                finish(); // Optionally finish the current activity if you don't want to return to it
            }
        }, 2000); // Adjust delay time as needed
    }


    private void showSplashScreen() {
        splashOverlay.setVisibility(View.VISIBLE);
    }

    private void hideSplashScreen() {
        splashOverlay.setVisibility(View.GONE);
    }
}
