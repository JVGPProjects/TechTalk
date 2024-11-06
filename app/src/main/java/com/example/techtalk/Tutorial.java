package com.example.techtalk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Tutorial extends AppCompatActivity {

    private ImageButton backButton;
    private String userRole; // Variable to store the user role
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);

        backButton = findViewById(R.id.backButton);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Initialize Firestore
        databaseReference = FirebaseDatabase.getInstance().getReference("users"); // Adjust path as necessary

        // Set the OnClickListener for the back button
        backButton.setOnClickListener(view -> onBackPressed());

        // Handle button clicks for changing passwords and other activities
        Button welcomeTutorialButton = findViewById(R.id.welcomeTutorialButton);
        Button lockTutorialButton = findViewById(R.id.lockTutorialButton);

        // Retrieve the user's role from SharedPreferences
        userRole = getSharedPreferences("AppPreferences", MODE_PRIVATE).getString("role", null);

        welcomeTutorialButton.setOnClickListener(v -> {
            startActivity(new Intent(Tutorial.this, MainMenuActivity.class));
        });

        lockTutorialButton.setOnClickListener(v -> navigateToNextActivity());
    }

    private void navigateToNextActivity() {
        String userId = mAuth.getCurrentUser().getUid();  // Get the current user ID

        // Fetch the user document from Firestore
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the role from the Firestore document
                        userRole = documentSnapshot.getString("role");

                        if (userRole == null) {
                            Toast.makeText(this, "User role not found in database. Please try again.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Intent intent;

                        // Navigate based on user role
                        if ("therapist".equals(userRole)) {
                            intent = new Intent(Tutorial.this, TherapistLockTutorialActivity.class);
                        } else if ("parent".equals(userRole)) {
                            intent = new Intent(Tutorial.this, LockTutorialActivity.class);
                        } else {
                            Toast.makeText(this, "User role not recognized.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        startActivity(intent);
                        finish();  // Finish the current activity after starting the new one

                    } else {
                        Toast.makeText(this, "User not found in database.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Tutorial", "Error fetching user role: ", e);
                    Toast.makeText(this, "Error checking user role.", Toast.LENGTH_SHORT).show();
                });
    }
}
