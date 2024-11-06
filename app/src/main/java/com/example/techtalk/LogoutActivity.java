package com.example.techtalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends AppCompatActivity {

    private SignInClient mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        Button logoutButton = findViewById(R.id.logoutButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        logoutButton.setOnClickListener(v -> {
            logoutUser();
        });

        cancelButton.setOnClickListener(v -> {
            // Go back to the previous activity
            finish();
        });
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
        Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the back stack
        startActivity(intent);
        finish(); // Finish the current activity
    }
}
