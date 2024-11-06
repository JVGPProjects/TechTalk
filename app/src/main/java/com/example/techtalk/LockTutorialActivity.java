package com.example.techtalk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LockTutorialActivity extends AppCompatActivity {
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_tutorial); // Replace with your actual layout file name

        next = findViewById(R.id.nextButton); // Ensure the ID matches your layout
        // Set click listener for the start button
        next.setOnClickListener(v -> {
            Intent intent = new Intent(LockTutorialActivity.this, ImageGalleryLockActivity.class);
            startActivity(intent);
            finish(); // Call finish to remove this activity from the back stack
        });
    }
}
