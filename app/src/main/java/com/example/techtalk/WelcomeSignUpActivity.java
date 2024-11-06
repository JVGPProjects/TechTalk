package com.example.techtalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeSignUpActivity extends AppCompatActivity {
    private Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_signup);

        start = findViewById(R.id.startButton); // Ensure the ID matches your layout
        // Set click listener for the start button
        start.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeSignUpActivity.this, MainMenuActivity.class);
            startActivity(intent);
            finish(); // Call finish to remove this activity from the back stack
        });
    }
}
