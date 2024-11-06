package com.example.techtalk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.view.View;

public class AccountLogo extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private TextView emailTextView;
    private TextView usernameTextView;
    private TextView profileUsernameTextView;

    private ImageView profileImageButton;
    private String previousProfileImageUrl; // Store the previous profile image URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_logo);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance(); // Initialize Firebase Storage
        storageRef = storage.getReference(); // Get a reference to the storage

        profileImageButton = findViewById(R.id.profileImageButton);
        emailTextView = findViewById(R.id.emailTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        profileUsernameTextView = findViewById(R.id.profileUsernameTextView);
        Button logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(v -> logout());

        fetchUserData(); // Load user data on activity creation
    }

    private void fetchUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    emailTextView.setText(mAuth.getCurrentUser().getEmail());
                    usernameTextView.setText(documentSnapshot.getString("username"));
                    profileUsernameTextView.setText(documentSnapshot.getString("username"));
                    previousProfileImageUrl = documentSnapshot.getString("profileImageUrl");

                    // Load the latest profile image
                    if (previousProfileImageUrl != null && !previousProfileImageUrl.isEmpty()) {
                        Glide.with(this)
                                .load(previousProfileImageUrl)
                                .circleCrop()
                                .into(profileImageButton);
                    } else {
                        profileImageButton.setImageResource(R.drawable.pfp1); // Set a default image if none exists
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(AccountLogo.this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void logout() {
        mAuth.signOut(); // Signs out the user from Firebase
        finish(); // Closes the current activity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // Back button click handler
    public void onBackButtonClick(View view) {
        onBackPressed();
    }
}
